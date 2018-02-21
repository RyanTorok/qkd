package qkd;

import java.io.IOException;


public class Alice extends Party {

    public static final double MAX_ACCEPTABLE_ERROR = .05; //just a guess, will have to test what is a sufficient error rate
    public static final int NUM_TEST_BITS = 10000;
    public static final int NUM_MSG_BITS = 100000;

    static Party Connection = null;

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        Initialize(Identity.Alice);
        String secureBits = protocol(NUM_MSG_BITS);
        getStatistics(secureBits);
        doCryptography(secureBits);
    }

    private static String protocol(int n) throws IOException {
        while(!validateConnection());
        return getSiftedKey(n);
    }

    private static String getSiftedKey(int n) throws IOException {
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
        return sifted;
    }

    static boolean validateConnection() throws IOException {
        sendString(Integer.toString(NUM_TEST_BITS));
        String randomBits = photon.randomBits(NUM_TEST_BITS);
        String randomBasis = photon.randomBits(NUM_TEST_BITS);
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
        sendString(randomBasis);
        String bobBasis = getString();
        String sifted = siftKey(randomBits, randomBasis, bobBasis);
        if (sifted.length() < NUM_TEST_BITS / 8)
            return false;
        sendString(sifted);
        Double errorRate = Double.parseDouble(getString());
        if (errorRate > MAX_ACCEPTABLE_ERROR) {
            System.out.println("Upon verifying the channel, the bit error rate was " + errorRate + ". " +
                    "It is likely an eavesdropper is present. Aborting the connection.");
            System.exit(1);
        }
        return true;
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
