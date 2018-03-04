package qkd;

import java.io.IOException;

public class Alice extends Party
{
    public static void main( String args[] ) throws IOException
    {

        Initialize( Identity.Alice );
        for ( int i = 0; i < config.numProtocolRuns; i++ )
        {
            String secureBits = protocol();
           // getStatistics( secureBits );
            doCryptography( secureBits );
        }
    }

    private static String protocol() throws IOException
    {
        String binaryKey = photon.randomBits( config.numberofBits );    //the initial generated binary key
        String bases = photon.randomBits( config.numberofBits );    //binary string corresponding to the bases binaryKey is sent in

        for ( int i = 0; i < config.numberofBits; i++ )
        {
            char bit = binaryKey.charAt( i );    //bit at current position
            char basis = bases.charAt( i );    //basis at current position

            for ( int j = 0; j < config.repetitionCodeBlockSize; j++ )
            {
                //following code prepares a photon using the bit and the basis for sending
                photon p = new photon();
                if ( bit == '0' && basis == '0' )
                {    //H/V basis 0 bit, so H
                    p.prepH();
                } else if ( bit == '0' && basis == '1' )
                {    //D/A basis 0 bit, so D
                    p.prepD();
                } else if ( bit == '1' && basis == '0' )
                {    //H/V basis 1 bit, so V
                    p.prepV();
                } else
                {    //D/A basis 1 bit, so A
                    p.prepA();
                }
                sendPhoton( p );
                //end

            }
        }
        sendString( bases );    //send bases to bob
        String bobBases = getString();    //get bob's bases

        //following code creates a new binaryKey with bits corresponding to non-matching bases thrown out
        StringBuilder newBinaryKey = new StringBuilder();
        for ( int i = 0; i < config.numberofBits; i++ )
        {
            if ( bases.charAt( i ) == bobBases.charAt( i ) )
            {
                newBinaryKey.append( binaryKey.charAt( i ) );
            }
        }
        //end

        //following code creates the part of the key used to check for eavesdropping
        //uses every 4th set of three characters of the original binaryKey
        StringBuilder partialKey = new StringBuilder();
        for ( int i = 0; i < newBinaryKey.length(); i++ )
        {
            if ( i % 4 == 0 )
            {
                partialKey.append( newBinaryKey.charAt( i ) );
                partialKey.append( newBinaryKey.charAt( i ) );
                partialKey.append( newBinaryKey.charAt( i ) );
            }
        }
        System.out.println(newBinaryKey.toString());
        StringBuilder keyRemnants = new StringBuilder();    //parts of new binaryKey not in partialKey
        for ( int i = 0; i < newBinaryKey.length(); i++ )
        {
            if ( i % 4 == 0 )
            {
                continue;
            }
            keyRemnants.append( newBinaryKey.charAt( i ) );
        }
        //end

        //following checks to see if Eve was eavesdropping
        //if more than 10% of the bits are off, we decide she was
        int errorCount = 0;
        sendString( partialKey.toString() );
        String bobBinaryKey = getString();
        for ( int i = 0; i < Math.min( partialKey.length(), bobBinaryKey.length() ); i++ )
        {
            if ( partialKey.charAt( i ) != bobBinaryKey.charAt( i ) )
            {
                errorCount++;
            }
        }
        if ( ( double ) errorCount / partialKey.length() > 0.36 )
        {
            System.out.println( "eve hacked us" );
        } else
        {
            System.out.println( "eve did not hack us" );
        }
        //end
        System.out.println(keyRemnants.toString());
        return keyRemnants.toString();
    }
}
