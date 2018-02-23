package qkd;

import java.io.IOException;


public class Alice extends Party {

    public static final double MAX_ACCEPTABLE_ERROR = .01; //just a guess, will have to test what is the best error rate
    public static final double PCT_TEST_BITS = .1;
    public static final int NUM_MSG_BITS = 1000;

    static Party Connection = null;

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        Initialize(Identity.Alice);
        String secureBits = protocol(NUM_MSG_BITS);
        getStatistics(secureBits);
        doCryptography(secureBits);
    }

    private static String protocol(int n) throws IOException {
        sendString(Integer.toString(n));
        String randomBits = photon.randomBits(n);
        String randomBasis = photon.randomBits(n);
        for (int i = 0; i < randomBits.length(); i++) {
            photon q = new photon();
            if (randomBasis.charAt(i) == '0')
                if (randomBits.charAt(i) == '0')
                    q.prepH();
                else q.prepV();
            else if (randomBits.charAt(i) == '0')
                q.prepD();
            else q.prepA();
            sendPhoton(q);
        }
        String done = getString();
        assert done.equals("done measuring");
        sendString(randomBasis);
        String bobBasis = getString();
        String sifted = siftKey(randomBits, randomBasis, bobBasis);
        String testBits = "";
        for (int i = 0; i < sifted.length(); i++) {
            testBits += (Math.random() < PCT_TEST_BITS) ? "1" : "0";
        }
        sendString(testBits);
        String bobTestBits = getString();
        int BTBIndex = 0;
        int errorCount = 0;
        for (int i = 0; i < sifted.length(); i++) {
            if (testBits.charAt(i) == '1')
                if (sifted.charAt(i) != bobTestBits.charAt(BTBIndex++))
                    errorCount++;
        }
        double errorRate = (double) errorCount / bobTestBits.length();
        sendString(Double.toString(errorRate));
        if (errorRate > MAX_ACCEPTABLE_ERROR) {
            System.out.println("Upon verifying the channel, the bit error rate was " + errorRate + ". " +
                    "It is likely an eavesdropper is present. Aborting the connection.");
            System.exit(1);
        }
        for (int i = 0; i < sifted.length(); i++) {
            if (testBits.charAt(i) == '1')
                sifted = sifted.substring(0, i) + sifted.substring(i + 1);
        }
        return sifted;
    }

    static String siftKey(String key, String basis1, String basis2) {
        String result = "";
        for (int i = 0; i < key.length(); i++) {
            if (basis1.charAt(i) == basis2.charAt(i))
                result += key.charAt(i);
        }
        return result;
    }
}

