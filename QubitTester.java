package qkd;


import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.sqrt;

public class QubitTester {

    public static void main(String[] args){

        try{
        RunTest();
        } catch (Exception e){
            System.out.println("Something Broke!");
        }

    }

    private static Exception QubitErrorException;
    private static Exception InvalidMeasurementException;
    private static Exception UnlikelyStatisticsException;
    
    private static String[] test(qubit q) throws Exception {

        Random rand = new SecureRandom();
        //0:"0", 1:"1", 2:"+", 3:"-"
        int state = rand.nextInt(4);
        int newstate = -1;

        switch (state) {
            case (0):
                q.prepZero();
                break;
            case (1):
                q.prepOne();
                break;
            case (2):
                q.prepPlus();
                break;
            case (3):
                q.prepMinus();
                break;
        }

        //0:X, 1:Z, 2:H
        int gate = rand.nextInt(3);
        switch (gate) {
            case (0):
                q.pauliX();
                if (state == 0){ newstate = 1;}
                if (state == 1){ newstate = 0;}
                if (state == 2){ newstate = 2;}
                if (state == 3){ newstate = 3;}
                break;
            case (1):
                q.pauliZ();
                if (state == 0){ newstate = 0;}
                if (state == 1){ newstate = 1;}
                if (state == 2){ newstate = 3;}
                if (state == 3){ newstate = 2;}
                break;
            case (2):
                q.hadamard();
                if (state == 0){ newstate = 2;}
                if (state == 1){ newstate = 3;}
                if (state == 2){ newstate = 0;}
                if (state == 3){ newstate = 1;}
                break;
        }

        //0:(0/1), 1:(+/-)
        int basis = rand.nextInt(2);

        String result1 = null;
        switch (basis) {
            case (0):
                result1 = q.measureZeroOne();

                if (!(result1.equals("0") || result1.equals("1"))) {
                    System.out.println("Zero-One measurement did not return zero or one");
                    throw InvalidMeasurementException;
                }
                if (newstate == 0 && !result1.equals("0")) {
                    System.out.println("Zero state did not measure to zero");
                    throw InvalidMeasurementException;
                }
                if (newstate == 1 && !result1.equals("1")) {
                    System.out.println("One state did not measure to one");
                    throw InvalidMeasurementException;
                }
                break;

            case (1):
                result1 = q.measurePlusMinus();

                if (!(result1.equals("+") || result1.equals("-"))) {
                    System.out.println("Plus-Minus measurement did not return plus or minus");
                    throw InvalidMeasurementException;
                }
                if (newstate == 2 && !result1.equals("+")) {
                    System.out.println("Plus state did not measure to plus");
                    throw InvalidMeasurementException;
                }
                if (newstate == 3 && !result1.equals("-")) {
                    System.out.println("Minus state did not measure to minus");
                    throw InvalidMeasurementException;
                }
                break;
        }

        //0:(0/1), 1:(+/-)
        basis = rand.nextInt(2);
        String result2 = null;
        switch (basis) {
            case (0):
                result2 = q.measureZeroOne();
                if (!(result2.equals("0") || result2.equals("1"))) {
                    System.out.println("Zero-One measurement did not return zero or one");
                    throw InvalidMeasurementException;
                }
                if (result1.equals("0") && !result2.equals("0")) {
                    System.out.println("Zero state did not measure to zero");
                    throw InvalidMeasurementException;
                }
                if (result1.equals("1") && !result2.equals("1")) {
                    System.out.println("One state did not measure to one");
                    throw InvalidMeasurementException;
                }
                break;

            case (1):
                result2 = q.measurePlusMinus();
                if (!(result2.equals("+") || result2.equals("-"))) {
                    System.out.println("Plus-Minus measurement did not return plus or minus");
                    throw InvalidMeasurementException;
                }
                if (result1.equals("+") && !result2.equals("+")) {
                    System.out.println("Plus state did not measure to plus");
                    throw InvalidMeasurementException;
                }
                if (result1.equals("-") && !result2.equals("-")) {
                    System.out.println("Minus state did not measure to minus");
                    throw InvalidMeasurementException;
                }
                break;
        }

        String[] Result = new String[3];

        switch (newstate){
            case(0):
                Result[0] = "0";
                break;
            case(1):
                Result[0] = "1";
                break;
            case(2):
                Result[0] = "+";
                break;
            case(3):
                Result[0] = "-";
                break;
        }
        Result[1] = result1;
        Result[2] = result2;
        return Result;
    }


    public static void checkLikely(int n1, int n2, String name) throws Exception {

        //de Moivre-Laplace Theorem: this variable should be ~ N(0,1)
        double x = (2*n1 - (n1+n2))/sqrt(n1 + n2);

        int nsigma = 2;
        if( x > nsigma || x < -nsigma) {
            System.out.println("Data was " + x + " standard deviations from acceptable threshold (2)");
            throw UnlikelyStatisticsException;
        }

    }

    public static void RunTest(){
        try {
            HashMap<String, Integer> outcomes = new HashMap<>();
            qubit q = new qubit();
            Random rand = new SecureRandom();
            int n = 10000;

            for (int i = 0; i < n; i++) {
                float p = rand.nextFloat();
                if (p < 0.2) {
                    q = new qubit();
                }

                String[] Data = test(q);

                String oc1 = Data[0] + Data[1];
                String oc2 = Data[1] + Data[2];

                if (!outcomes.containsKey(oc1)) outcomes.put(oc1, 0);
                if (!outcomes.containsKey(oc2)) outcomes.put(oc2, 0);

                outcomes.put(oc1, outcomes.get(oc1) + 1);
                outcomes.put(oc2, outcomes.get(oc2) + 1);

            }
            checkLikely(outcomes.get("0+"), outcomes.get("0-"), "Plus-Minus from Zero");
            checkLikely(outcomes.get("1+"), outcomes.get("1-"), "Plus-Minus from One");
            checkLikely(outcomes.get("+0"), outcomes.get("+1"), "Zero-One from Plus");
            checkLikely(outcomes.get("-0"), outcomes.get("-1"), "Zero-One from Minus");

            System.out.println("Test Succeeded! Nice");
        } catch (Exception e){
            System.out.println("\tFAILURE! (but try it several times, this script only succeeds with high probabilty)");
        }
    }

}
