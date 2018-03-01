package qkd;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;

public class Party
{

    private static Identity name;

    private static InetAddress address = null;
    private static Socket s1 = null;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static Random rand = null;
    private static Scanner keyboard = null;

    public void Protocol()
    {
        System.out.println( "Parent Protocol" );
    }

    static void Initialize( Identity username )
    {
        try
        {
            keyboard = new Scanner( System.in );

            System.out.println( "Please enter the Quantum IP Address or return an empty line to use LocalHost" );
            while ( true )
            {
                try
                {
                    String addr = keyboard.nextLine();
                    if ( addr.equals( "" ) )
                    {
                        address = InetAddress.getLocalHost();
                        s1 = new Socket( address, config.ServerPort ); // You can use static final constant PORT_NUM
                        break;
                    }
                    address = InetAddress.getByName( addr );
                    s1 = new Socket( address, config.ServerPort ); // You can use static final constant PORT_NUM
                    break;
                } catch ( Exception e )
                {
                    System.out.println( "Please try again." );
                }

            }


            oos = new ObjectOutputStream( s1.getOutputStream() );
            ois = new ObjectInputStream( s1.getInputStream() );


            rand = new SecureRandom();


            name = username;

            System.out.println( "Hello " + name );
            boolean connected = false;
            while ( !connected )
            {

                oos.writeObject( name );
                oos.flush();

                System.out.println( "Awaiting other party" );
                String line = null;
                try
                {
                    line = ( String ) ois.readObject();
                } catch ( Exception e )
                {
                    System.out.println( "HANDSHAKE FAILED! Try increasing sleep time at the end of Initialize" );
                }

                if ( line.equals( "CONNECTED" ) )
                {
                    System.out.println( "Starting protocol:" );
                    connected = true;
                } else
                {
                    System.out.println( "Connection Refused" );
                    System.exit( 0 );
                }
            }

        } catch ( IOException e )
        {
            e.printStackTrace();
            System.err.print( "IO Exception, Initialization Error" );
        }

        //This Delay is important. It ensures the other party has enough time to receive confirmation before this
        // thread's corresponding protocol sends an object. If this thread sends an object before the other thread
        // confirms it's connection the handshake will explode. This synchronizes the protocol's start.
        try
        {
            Thread.sleep( 500 );
        } catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
    }

    static void sendString( String str ) throws IOException
    {
        Identity recipient = null;
        if ( name.equals( Identity.Alice ) )
        {
            recipient = Identity.Bob;
        }
        if ( name.equals( Identity.Bob ) )
        {
            recipient = Identity.Alice;
        }
        if ( name.equals( Identity.Eve ) ) throw new IOException( "EVE CANNOT SEND STRINGS! NO CHEATING" );
        Message msg = new Message( recipient, str );
        sendData( msg );
    }

    static String getString()
    {

        try
        {
            Message msg = getData();
            return ( String ) msg.message;
        } catch ( Exception e )
        {

        }
        System.out.println( "GETSTRING DIED WHAT DID YOU DO!?!?!" );
        return null;
    }

    public static void sendPhoton( photon p, Identity recipient ) throws IOException
    {

        p.depolarize( config.HalfChannelDepolarize );
        if ( rand.nextDouble() < config.HalfChannelLoss )
        {
            p.present = false;
        }

        Message msg = new Message( recipient, p );
        sendData( msg );

    }

    static void sendPhoton( photon p ) throws IOException
    {
        Identity recipient = null;
        if ( name.equals( Identity.Alice ) )
        {
            recipient = Identity.Bob;
        }
        if ( name.equals( Identity.Bob ) )
        {
            recipient = Identity.Alice;
        }

        p.depolarize( config.HalfChannelDepolarize );
        if ( rand.nextDouble() < config.HalfChannelLoss )
        {
            p.present = false;
        }

        if ( QuantumChannel.EveConnected )
        {
            p.depolarize( config.HalfChannelDepolarize );
            if ( rand.nextDouble() < config.HalfChannelLoss )
            {
                p.present = false;
            }
        }

        Message msg = new Message( recipient, p );
        sendData( msg );
    }

    static photon getPhoton() throws InterruptedException
    {
        try
        {
            Message msg = getData();
            return ( photon ) msg.message;
        } catch ( Exception e )
        {
            System.out.println( "Waiting for Photon" );
            Thread.sleep( 50 );
        }
        System.out.println( "GET PHOTON DIED WHAT DID YOU DO!?!?!" );
        return null;
    }

    private static void sendData( Message message ) throws IOException
    {
        oos.writeObject( message );
        oos.flush();
    }

    private static Message getData() throws InterruptedException
    {
        while ( true )
        {
            try
            {
                return ( Message ) ois.readObject();
            } catch ( Exception e )
            {
                System.out.println( "Waiting for String" );
                Thread.sleep( 50 );
            }
        }
    }

    static void getStatistics( String bits ) throws IOException
    {
        String newString = "FINAL" + bits;
        Message newmessage = new Message( name, newString );
        oos.writeObject( newmessage );
        oos.flush();

        System.out.println( getString() );
        System.out.println( getString() );
        System.out.println( getString() );
        System.out.println( getString() );
        System.out.println( getString() );
        System.out.println( getString() );
        System.out.println( getString() );
        System.out.println( getString() );
    }


    static void doCryptography( String sharedRandomness ) throws IOException
    {
        String legalChars = "abcdefghijklmnopqrstuvwxyz,.!?' ";
        if ( name.equals( Identity.Alice ) )
        {
            int len = sharedRandomness.length();
            int numChars = ( len / 5 ) / config.repetitionCodeBlockSize;
            System.out.println();
            System.out.println( "What " + numChars + " letter message would you like to send?" );
            System.out.println( "Legal Characters are: abcdefghijklmnopqrstuvwxyz,.!?' and space." );
            System.out.println( "|=====================================|" );

            PollInput:
            while ( true )
            {
                String message = keyboard.nextLine();
                if ( message.length() <= numChars )
                {
                    for ( int i = 0; i < message.length(); i++ )
                    {
                        if ( legalChars.indexOf( message.charAt( i ) ) == -1 )
                        {
                            System.out.println( "Invalid input, try again." );
                            continue PollInput;
                        }
                    }

                    if ( message.length() < numChars )
                    {
                        int padding = numChars - message.length();
                        StringBuilder pad = new StringBuilder();
                        for ( int i = 0; i < padding; i++ )
                        {
                            pad.append( " " );
                        }
                        message = message + pad;
                    }

                    String encryptedString = encrypt( message, sharedRandomness );
                    sendString( encryptedString );
                    System.out.println( "Sent: " + message );
                } else
                {
                    System.out.println( "Invalid input, try again." );
                    // No need for a continue since this is the last statement in the loop
                }
            }
        }
        if ( name.equals( Identity.Bob ) || name.equals( Identity.Eve ) )
        {

            String data = getString();
            System.out.println();
            System.out.println( "|=====================================|" );
            System.out.println( "|=========| Encrypted Message |=======|" );
            System.out.println( "|=====================================|" );
            System.out.println( "Alice: " + decrypt( data, sharedRandomness ) );
        }

    }

    private static String encrypt( String message, String sharedRandomness )
    {
        String legalChars = "abcdefghijklmnopqrstuvwxyz,.!?' ";
        int len = message.length();
        StringBuilder encrypted = new StringBuilder();
        StringBuilder binary = new StringBuilder();

        //Convert ascii String to binary String
        for ( int i = 0; i < len; i++ )
        {
            String bin = Integer.toBinaryString( legalChars.indexOf( message.charAt( i ) ) );
            if ( bin.length() < 5 )
            {
                int dif = 5 - bin.length();
                String pad = "";
                String pad1 = "";
                String zero = "0";
                for ( int j = 0; j < dif; j++ )
                {
                    pad = pad1 + zero;
                    pad1 = pad;
                }
                bin = ( pad + bin );
            }
            if ( bin.length() != 5 )
            {
                System.out.println( "PADDING HAS FAILED" );
            }
            binary.append( bin );
        }

        StringBuilder expandedBinary = new StringBuilder();
        for ( int i = 0; i < binary.length(); i++ )
        {
            for ( int j = 0; j < config.repetitionCodeBlockSize; j++ )
            {
                expandedBinary.append( Character.toString( binary.charAt( i ) ) );
            }
        }

        //Xor with random bits
        for ( int i = 0; i < expandedBinary.length(); i++ )
        {
            if ( expandedBinary.charAt( i ) == '0' && sharedRandomness.charAt( i ) == '0' )
            {
                encrypted.append( "0" );
            }
            if ( expandedBinary.charAt( i ) == '1' && sharedRandomness.charAt( i ) == '1' )
            {
                encrypted.append( "0" );
            }
            if ( expandedBinary.charAt( i ) == '0' && sharedRandomness.charAt( i ) == '1' )
            {
                encrypted.append( "1" );
            }
            if ( expandedBinary.charAt( i ) == '1' && sharedRandomness.charAt( i ) == '0' )
            {
                encrypted.append( "1" );
            }
        }

        return encrypted.toString();
    }

    private static String decrypt( String encryptedString, String sharedRandomness )
    {
        String legalChars = "abcdefghijklmnopqrstuvwxyz,.!?' ";
        if ( encryptedString.length() % config.repetitionCodeBlockSize != 0 )
        {
            System.out.println( "SOMETHING TERRIBLE HAPPENED" );
        }

        StringBuilder unXORbinary = new StringBuilder();

        for ( int i = 0; i < encryptedString.length(); i++ )
        {
            if ( encryptedString.charAt( i ) == '0' && sharedRandomness.charAt( i ) == '0' )
            {
                unXORbinary.append( "0" );
            }
            if ( encryptedString.charAt( i ) == '1' && sharedRandomness.charAt( i ) == '1' )
            {
                unXORbinary.append( "0" );
            }
            if ( encryptedString.charAt( i ) == '0' && sharedRandomness.charAt( i ) == '1' )
            {
                unXORbinary.append( "1" );
            }
            if ( encryptedString.charAt( i ) == '1' && sharedRandomness.charAt( i ) == '0' )
            {
                unXORbinary.append( "1" );
            }
        }

        int numChars = encryptedString.length() / config.repetitionCodeBlockSize;

        StringBuilder reduced = new StringBuilder();
        for ( int i = 0; i < numChars; i++ )
        {
            int one = 0;
            int zero = 0;
            for ( int j = config.repetitionCodeBlockSize * i; j < config.repetitionCodeBlockSize * i + config.repetitionCodeBlockSize; j++ )
            {
                if ( unXORbinary.charAt( j ) == '0' )
                {
                    zero++;
                } else
                {
                    one++;
                }
            }
            if ( one > zero )
            {
                reduced.append( "1" );
            } else
            {
                reduced.append( "0" );
            }
        }

        StringBuilder message = new StringBuilder();
        for ( int i = 0; i < reduced.length() / 5; i++ )
        {
            StringBuilder binary = new StringBuilder();
            for ( int j = 5 * i; j < 5 + 5 * i; j++ )
            {
                binary.append( Character.toString( reduced.charAt( j ) ) );
            }
            int num = Integer.parseInt( binary.toString(), 2 );
            message.append( Character.toString( legalChars.charAt( num ) ) );
        }
        return message.toString();
    }
}