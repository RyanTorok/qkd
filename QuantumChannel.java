package qkd;

import java.io.*;
import java.net.*;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static qkd.QuantumChannel.*;


public class QuantumChannel {
    static ArrayList<ServerThread> ActiveParties;
    static boolean AliceConnected = false;
    static boolean BobConnected = false;
    static boolean EveConnected = false;


    static String AliceFinalBits = "-1";
    static String BobFinalBits = "-1";
    static String EveFinalBits = "-1";

    static int photonsSent = 0;

    public static void main(String args[]) throws UnknownHostException {

        Socket serversock  =  null;
        ServerSocket ss2=null;
        ActiveParties = new ArrayList<>();

        System.out.println("Quantum IP: " + InetAddress.getLocalHost().toString());
        System.out.println("Awaiting Connection......");
        try{
            ss2 = new ServerSocket(config.ServerPort); // can also use static final PORT_NUM , when defined
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Server error");
        }
        while(true){

            try{
                serversock= ss2.accept();
                ServerThread st=new ServerThread(serversock);
                ActiveParties.add(st);
                st.start();

            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("Connection Error");

            }
        }
    }



    public static synchronized void forwardTo(Message message, Identity sender) throws IOException, ClassNotFoundException, InterruptedException {

        Identity name = message.recipient;

        //I know this is ugly, but for some reason java implodes when I try to
        // store the actual thread instead of iterating through the ArrayList.
        // I don't get it but this works and style has become a tertiary concern at this point.

        for(int i = 0; i < ActiveParties.size(); i++){
            if(ActiveParties.get(i).name.equals(name)){

                //If Eve is present and is not the one forwarding
                if(EveConnected == true && !sender.equals(Identity.Eve)){

                    for(int j = 0; j < ActiveParties.size(); j++){
                        if(ActiveParties.get(j).name.equals(Identity.Eve)){
                            ActiveParties.get(j).send(message);
                        }
                    }
                }
                if(message.message instanceof String || !sender.equals(Identity.Eve)){
                    if(message.message instanceof photon){photonsSent++;}
                    if (name.equals(Identity.Bob)) { ActiveParties.get(i).send(message); }
                    if (name.equals(Identity.Alice)) { ActiveParties.get(i).send(message); }
                    if (name.equals(Identity.Eve)) { ActiveParties.get(i).send(message); }
                }
            }
        }
    }

    private static void CalcStats() throws InterruptedException, IOException, ClassNotFoundException {
        int goodbits = 0;
        int goodEveBits = 0;

        for(int i = 0; i < AliceFinalBits.length(); i++){
            if(AliceFinalBits.charAt(i)==BobFinalBits.charAt(i)){
                goodbits++;
                if(EveConnected && AliceFinalBits.charAt(i)==EveFinalBits.charAt(i)){
                    goodEveBits++;
                }
            }
        }

        String str1 = "/=============================|=======\\";
        String str2 = "|===========| Statistics |============|";
        String str3 = "|=====================================|";
        String str4 = Integer.toString(photonsSent)+"\tTotal Photons Sent\t\t\t\t";
        String str5 = Integer.toString(AliceFinalBits.length()) + "\tTotal Bits Exchanged\t\t\t";
        String str6 = Integer.toString(goodbits) + "\tBits Correctly Exchanged\t\t\t\t\t";
        String str7 = "|=====================================|";
        String str8 = "\\=====================================/";
        if(EveConnected) {
            str7 = Integer.toString(goodEveBits) + "\tBits Correctly Eavesdropped";
        }

        forwardTo(new Message(Identity.Alice, str1),Identity.Eve);
        forwardTo(new Message(Identity.Alice, str2),Identity.Eve);
        forwardTo(new Message(Identity.Alice, str3),Identity.Eve);
        forwardTo(new Message(Identity.Alice, str4),Identity.Eve);
        forwardTo(new Message(Identity.Alice, str5),Identity.Eve);
        forwardTo(new Message(Identity.Alice, str6),Identity.Eve);
        forwardTo(new Message(Identity.Alice, str7),Identity.Eve);
        forwardTo(new Message(Identity.Alice, str8),Identity.Eve);

        forwardTo(new Message(Identity.Bob, str1),Identity.Eve);
        forwardTo(new Message(Identity.Bob, str2),Identity.Eve);
        forwardTo(new Message(Identity.Bob, str3),Identity.Eve);
        forwardTo(new Message(Identity.Bob, str4),Identity.Eve);
        forwardTo(new Message(Identity.Bob, str5),Identity.Eve);
        forwardTo(new Message(Identity.Bob, str6),Identity.Eve);
        forwardTo(new Message(Identity.Bob, str7),Identity.Eve);
        forwardTo(new Message(Identity.Bob, str8),Identity.Eve);

        if(EveConnected) {
            forwardTo(new Message(Identity.Eve, str1), Identity.Eve);
            forwardTo(new Message(Identity.Eve, str2), Identity.Eve);
            forwardTo(new Message(Identity.Eve, str3), Identity.Eve);
            forwardTo(new Message(Identity.Eve, str4), Identity.Eve);
            forwardTo(new Message(Identity.Eve, str5), Identity.Eve);
            forwardTo(new Message(Identity.Eve, str6), Identity.Eve);
            forwardTo(new Message(Identity.Eve, str7), Identity.Eve);
            forwardTo(new Message(Identity.Eve, str8), Identity.Eve);
        }
    }


    public static void doFinalStats(Identity name, String bits) throws InterruptedException, IOException, ClassNotFoundException {

        if(name.equals(Identity.Alice) && AliceFinalBits.equals("-1")){ AliceFinalBits = bits;}
        if(name.equals(Identity.Bob) && BobFinalBits.equals("-1")){ BobFinalBits = bits;}
        if(name.equals(Identity.Eve) && EveFinalBits.equals("-1")){ EveFinalBits = bits;}

        if(!AliceFinalBits.equals("-1") && !BobFinalBits.equals("-1")){
            if(EveConnected) {
                if (!EveFinalBits.equals("-1")) {
                    CalcStats();
                }
            } else {
                CalcStats();
            }
        }
    }
}

class ServerThread extends Thread{

    Identity name;
    Identity otherParty;
    String line=null;
    Object message=null;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    Socket s=null;
    ArrayList<ServerThread> ActiveParties;

    public ServerThread(Socket s){
        this.s=s;
    }

    public void run() {
        try{
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
        }catch(IOException e){
            System.out.println("IO error in server thread");
        }

        try {

            //Get Name & Confirm Connection
            while(!AliceConnected || !BobConnected || !EveConnected) {
                Identity name = (Identity)ois.readObject();

                this.name = name;
                if(name.equals(Identity.Alice)){
                    //if(QuantumChannel.AliceConnected) System.exit(0);
                    QuantumChannel.AliceConnected = true;
                    //QuantumChannel.AliceThread = this;
                    break;
                }
                if(name.equals(Identity.Bob)){
                    //if(QuantumChannel.BobConnected) System.exit(0);
                    QuantumChannel.BobConnected = true;
                    //QuantumChannel.BobThread = this;
                    break;
                }
                if(name.equals(Identity.Eve)){
                    //if(QuantumChannel.EveConnected) System.exit(0);
                    QuantumChannel.EveConnected = true;
                    //QuantumChannel.BobThread = this;
                    break;
                }
            }

            //Wait Until Both Parties are connected before sending confirmation:

            while(true){
                Thread.sleep(300);
                if(QuantumChannel.AliceConnected && QuantumChannel.BobConnected){ break;}
            }

            //Send Confirmation to each party that a connection has been established.
            //After this confirmation both parties may begin their protocol.

            oos.writeObject("CONNECTED");
            oos.flush();
            System.out.println("Connection confirmed for  " + name);

            //Listen on thread for data to forward
            while(true){
                Object Msg = null;
                Object content = null;
                try {
                    Msg = (Message)ois.readObject();

                   //This Block Handles Message Forwarding For the statistics
                   if(((Message)Msg).message instanceof String){
                        String msg =  (String)((Message)Msg).message;
                        String prefix = null;
                        if(msg.length() > 5){
                            prefix = msg.substring(0,5);
                            if(prefix.equals("FINAL")){
                                String finalBits = msg.substring(6,msg.length());
                                doFinalStats(name,finalBits);
                                continue;
                            }
                        }
                    }

                    //Forward Messages Normally
                    QuantumChannel.forwardTo(((Message)Msg), name);
                    Thread.sleep(25);

                } catch (IOException e) {

                    oos.reset();
                    if(name.equals(Identity.Alice)){QuantumChannel.AliceConnected = false;}
                    if(name.equals(Identity.Bob)){QuantumChannel.BobConnected = false;}
                    if(name.equals(Identity.Eve)){QuantumChannel.EveConnected = false;}

                    for(int i = 0; i < QuantumChannel.ActiveParties.size(); i++){
                        if(QuantumChannel.ActiveParties.get(i).name.equals(name)){
                            QuantumChannel.ActiveParties.remove(i);
                        }
                    }

                    return ;
                }
            }

        } catch (IOException e) {
            System.out.println("IO Error/ Client "+ name +" terminated abruptly");
        }
        catch(NullPointerException e){
            System.out.println("Client "+ name +" Closed");
        } catch (ClassNotFoundException e) {
            System.out.println("You sent some kind of NONSENSE through this channel");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("INTURRUPTED");
            e.printStackTrace();
        } catch (Exception e) {

        } finally{
            try{
                System.out.println("Connection Closing..");
                if (ois!=null){
                    ois.close();
                    System.out.println(" Socket Input Stream Closed");
                }

                if(oos!=null){
                    oos.close();
                    System.out.println("Socket Out Closed");
                }
                if (s!=null){
                    s.close();
                    System.out.println("Socket Closed");
                }

            }
            catch(IOException ie){
                System.out.println("Socket Close Error");
            }
        }
    }

    public void send(Object message) throws IOException {
        oos.writeObject(message);
        oos.flush();
    }

}