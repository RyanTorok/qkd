package qkd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import qkd.complex;
import qkd.qubit;


public class Alice extends Party{

    static Party Connection = null;
    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        Initialize(Identity.Alice);
        String secureBits = "";

        /*getStatistics(secureBits);
        doCryptography(secureBits);//*/
    }

    private static String protocol(int n) throws IOException {
        return "";
    }
}
