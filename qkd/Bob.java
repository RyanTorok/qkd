package qkd;

import java.io.IOException;

import static qkd.Alice.MAX_ACCEPTABLE_ERROR;

public class Bob extends Party {

    static Party Connection = null;
    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        Initialize(Identity.Bob);
        String secureBits = protocol();
        getStatistics(secureBits);
        doCryptography(secureBits);
    }

    private static String protocol() throws InterruptedException, IOException {
        while (!validateConnection());
        return getSiftedKey();
    }

    private static String getSiftedKey() throws IOException, InterruptedException {
        Integer numBits = Integer.parseInt(getString());
        String randomBasis = photon.randomBits(numBits);
        photon[] received = new photon[numBits];
        for (int i = 0; i < received.length; i++) {
            received[i] = getPhoton();
        }
        String key = "";
        for (int i = 0; i < received.length; i++) {
            if (randomBasis.charAt(i) == '0') {
                //measure in H/V
                received[i].filterH();
                key += (received[i].present) ? "0" : "1";
            } else {
                //measure in D/A
                received[i].filterD();
                key += (received[i].present) ? "0" : "1";
            }
        }
        sendString("done measuring");
        String aliceBasis = getString();
        sendString(randomBasis);
        String sifted = siftKey(key, aliceBasis, randomBasis);
        return sifted;
    }

    static boolean validateConnection() throws IOException, InterruptedException {
        Integer numBits = Integer.parseInt(getString());
        String randomBasis = photon.randomBits(numBits);
        photon[] received = new photon[numBits];
        for (int i = 0; i < received.length; i++) {
            received[i] = getPhoton();
        }
        String key = "";
        for (int i = 0; i < received.length; i++) {
            if (randomBasis.charAt(i) == '0') {
                //measure in H/V
                received[i].filterH();
                key += (received[i].present) ? "0" : "1";
            } else {
                //measure in D/A
                received[i].filterD();
                key += (received[i].present) ? "0" : "1";
            }
        }
        String aliceBasis = getString();
        sendString(randomBasis);
        String sifted = siftKey(key, aliceBasis, randomBasis);
        if (sifted.length() < numBits / 8) {
            return false;
        }
        String aliceSifted = getString();
        assert sifted.length() == aliceSifted.length();
        //compare sifted keys
        Integer numErrors = 0;
        for (int i = 0; i < sifted.length(); i++) {
            if (sifted.charAt(i) != aliceSifted.charAt(i))
                numErrors++;
        }
        Double errorRate = (double) numErrors / sifted.length();
        sendString(errorRate.toString());
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
