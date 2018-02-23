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
        String testBits = getString();
        String sendTest = "";
        for (int i = 0; i < sifted.length(); i++) {
            if (testBits.charAt(i) == '1')
                sendTest += sifted.charAt(i);
        }
        sendString(sendTest);
        Double errorRate = Double.parseDouble(getString());
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
