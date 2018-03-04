package qkd;

import java.io.IOException;
import java.util.ArrayList;

public class Eve extends Party {

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        Initialize(Identity.Eve);

        for (int i = 0; i < config.numProtocolRuns; i++) {
            String secureBits = protocol();
            //getStatistics( secureBits );
            doCryptography(secureBits);
        }
    }

    public static String protocol() throws IOException, InterruptedException {
        String deduced = "";
        int numBits = config.numberofBits;
        String eveBasis = "";
        //Alice will send three equal bits in the same basis, so just alternate measuring bases to get all the bits.
        for (int i = 0; i < numBits; i++) {
            eveBasis += "01X";
        }
        photon[] received = new photon[numBits * config.repetitionCodeBlockSize];
        for (int i = 0; i < received.length; i++) {
            received[i] = getPhoton();
        }

        for (int i = 0; i < eveBasis.length(); i++) {
            boolean measure = true;
            if (eveBasis.charAt(i) == '0')
                received[i].filterH();
            else if (eveBasis.charAt(i) == '1')
                received[i].filterD();
            else measure = false;
            if (measure)
                deduced += received[i].present ? "0" : "1"; //this is legal, because Eve's detector works perfectly.
            else deduced += "X";
            sendPhoton(received[i], Identity.Bob);
        }
        String aliceBasis = getString();
        String aliceBasisTimes3 = "";
        String bobBasis = getString();

        for (int i = 0; i < aliceBasis.length(); i++) {
            for (int j = 0; j < config.repetitionCodeBlockSize; j++) {
                aliceBasisTimes3 += aliceBasis.charAt(i);
            }
        }
        String finalKeyWithSecurity = "";
        for (int i = 0; i < eveBasis.length(); i++) {
            //eve's key must match Alice's basis at either the first or second position of the three-character repetition
            if (eveBasis.charAt(i) == aliceBasisTimes3.charAt(i))
                finalKeyWithSecurity += deduced.charAt(i);
        }
        finalKeyWithSecurity = siftKey(finalKeyWithSecurity, aliceBasis, bobBasis);
        String finalKey = "";
        for (int i = 0; i < finalKeyWithSecurity.length(); i++)
            finalKey += i % 4 != 0 ? finalKeyWithSecurity.charAt(i) : "";

        String aliceSecKey = getString();
        String bobSecKey = getString();
        int errorCount = 0;
        for (int i = 0; i < Math.min(aliceSecKey.length(), bobSecKey.length()); i++) {
            if (aliceSecKey.charAt(i) != bobSecKey.charAt(i)) {
                errorCount++;
            }
        }
        if ((double) errorCount / aliceSecKey.length() > 0.36) {
            System.out.println("Alice and Bob caught you.");
        } else {
            System.out.println("Alice and Bob did not catch you.");
        }
        System.out.println(finalKeyWithSecurity);
        System.out.println(finalKey);
        return finalKey;
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