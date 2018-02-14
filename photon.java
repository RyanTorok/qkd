package qkd;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;

public class photon implements Serializable {
    //Density Matrix
    double[][] state = new double[2][2];
    Boolean present;
    Random rand;

    public photon() {
        prepH();
        present = true;
        rand = new SecureRandom();
    }
    //======================== State Prep
    public void prepH(){
        state[0][0] = 1;
        state[0][1] = 0;
        state[1][0] = 0;
        state[1][1] = 0;
        present = true;
    }
    public void prepV(){
        state[0][0] = 0;
        state[0][1] = 0;
        state[1][0] = 0;
        state[1][1] = 1;
        present = true;
    }
    public void prepD(){
        state[0][0] = 0.5;
        state[0][1] = 0.5;
        state[1][0] = 0.5;
        state[1][1] = 0.5;
        present = true;
    }
    public void prepA(){
        state[0][0] = 0.5;
        state[0][1] = -0.5;
        state[1][0] = -0.5;
        state[1][1] = 0.5;
        present = true;
    }
    //======================== Polarizing Filters
    public void filterH(){
        double p = state[0][0];
        if(rand.nextDouble() < p){
            prepH();
        }else{
            prepV();
            present = false;
        }
    }
    public void filterV(){
        double p = state[1][1];
        if(rand.nextDouble() < p){
            prepV();
        }else{
            prepH();
            present = false;
        }
    }
    public void filterD(){
        double p = state[0][1] + 0.5;
        if(rand.nextDouble() < p){
            prepD();
        }else{
            prepA();
            present = false;
        }
    }
    public void filterA(){
        double p = state[1][0] + 0.5;
        if(rand.nextDouble() > p){
            prepA();
        }else{
            prepD();
            present = false;
        }
    }
    //======================== Noise & Detection

    //Become a maximally mixed state with probability p
    public void depolarize(double p){
        state[0][0] = (p/2)+(1-p)*state[0][0];
        state[0][1] *= 1-p;
        state[1][0] *= 1-p;
        state[1][1] = (p/2)+(1-p)*state[1][1];
    }

    public String detect(){
        if(present){
            present = false;
            if(rand.nextDouble() < config.efficiency){ return "1"; }
            else{ return "0"; }
        }else{
            if(rand.nextDouble() < config.darkChance){ return "1"; }
            else{ return "0"; }
        }
    }

    public static String randomBits(int n){
        if(n==0){ return "";}
        Random rand = new SecureRandom();
        String bits = new String();
        for(int i = 0; i < n; i++){
            bits += Integer.toString(rand.nextInt(2));
        }
        return bits;
    }


}


