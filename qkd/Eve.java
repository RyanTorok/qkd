ckage qkd;

import java.io.IOException;
import java.util.ArrayList;

public class Eve extends Party {

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        Initialize(Identity.Eve);

        String secureBits = "";

        /*getStatistics(secureBits);
        doCryptography(secureBits);//*/
    }

    public static String protocol() throws IOException, InterruptedException {
        String deduced = "";
        int numBits = config.numberofBits;
        String eveBasis = photon.randomBits(numBits);
        for (int i = 0; i < numBits; i++) {
            deduced += '_';
        }
        photon[] received = new photon[numBits];
        for (int i = 0; i < received.length; i++) {
            received[i] = getPhoton();
        }
        for (int i = 0; i < eveBasis.length(); i++) {
            boolean measure = ;
            if (measure) {
                if (eveBasis.charAt(i) == '0')
                    received[i].filterH();
                else received[i].filterD();
                deduced = deduced.substring(0, i) + received[i].detect() + deduced.substring(i + 1);
            }
        }
        for (photon p : received) {
            sendPhoton(p);
        }
        String aliceBasis = getString();
        String bobBasis = getString();
        deduced = siftKey(deduced, aliceBasis, bobBasis);
        eveBasis = siftKey(eveBasis, aliceBasis, bobBasis);
        for (int i = 0; i < deduced.length(); i++) {
            if (aliceBasis.charAt(i) != eveBasis.charAt(i))
                deduced = deduced.substring(0, i) + "_" + deduced.substring(i + 1);
        }
        return deduced;
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

