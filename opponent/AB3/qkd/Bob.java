package qkd;

import java.io.IOException;

public class Bob extends Party
{

    public static void main( String args[] ) throws IOException, InterruptedException
    {

        Initialize( Identity.Bob );

        for ( int i = 0; i < config.numProtocolRuns; i++ )
        {
            String secureBits = protocol();
           // getStatistics( secureBits );
            doCryptography( secureBits );
        }
    }

    private static String protocol() throws InterruptedException, IOException
    {
        // Alice should generate a random bitstring to send to me
        // Encoded with randomized quantum states

        // Determine the random bases that we are going to measure in
        // 0 -> H/V
        // 1 -> D/A
        String bases = photon.randomBits( config.numberofBits );

        // Keep receiving photons until we have them all
        StringBuilder results = new StringBuilder();
        for ( int i = 0; i < bases.length(); i++ )
        {
            // Check the repetition block number of times with the same base
            for ( int j = 0; j < config.repetitionCodeBlockSize; j++ )
            {
                photon nextPhoton = getPhoton();

                // Measure the photon in a random base (as determined earlier)
                if ( bases.charAt( i ) == '0' )
                {
                    // Prepare to measure in the H/V base
                    nextPhoton.filterH();
                } else
                {
                    // Prepare to measure in the D/A base
                    nextPhoton.filterD();
                }

                // Measure the photon to construct our result string
                if ( nextPhoton.detect() )
                {
                    results.append( "0" );
                } else results.append( "1" );
            }
        }
        // Receive alice's bases and announce our bases to Alice
        String aBases = getString();
        sendString( bases );
        StringBuilder longKey = new StringBuilder();
        StringBuilder key = new StringBuilder();

        for ( int i = 0; i < bases.length(); i++ )
        {
            if ( bases.charAt( i ) == aBases.charAt( i ) )
            {
                // Long key is the full correct key (with redundancies)
                longKey.append( results.charAt( i * 3 ) );
                longKey.append( results.charAt( i * 3 + 1 ) );
                longKey.append( results.charAt( i * 3 + 2 ) );

                // Take the average of the three bits corresponding to that correct basis pair
                char newChar = ( char ) ( Math.round( ( results.charAt( i * 3 ) +
                        results.charAt( i * 3 + 1 ) +
                        results.charAt( i * 3 + 2 ) ) / 3.0 ) );
                key.append( newChar );
            }
        }

        // We have a key!
        // Get a portion of the key Alice sifted to determine if we have an eavesdropper
        String aKey = getString();

        // Don't just send the end of the key to each other to check for Eve!
        // This is what Eve will be expecting, and Eve will probably throw out
        // a portion of the key that is just at the end
        // If we instead compare every fourth value, it is a lot harder for Eve
        // to construct her own correct key

        // Theoretically, this information could be determined when the program is
        // written and then every message sent from there on out could conclude
        // with a short bitstring that indicates the new sampling pattern that
        // should be done to extract the portion of the key to compare (like the
        // German enigma changing the key every day, but without the pesky heuristic
        // of morning weather messages) (Sampling methods to compare could include:
        // Every fourth value starting at 2, every prime value, every fourth value
        // not divisible by 16, etc.)
        // These would have to be preset, but information concerning updates to these
        // patterns could always be sent using the system in itself


        // Compare the result string before it was corrected for noise!
        // This allows for us to far more accurately track for Eve
        StringBuilder bKey = new StringBuilder();
        for ( int i = 0; i < longKey.length(); i += 4 )
        {
            bKey.append( longKey.charAt( i ) );
        }

        // Send the portion of the key to compare to alice
        sendString( bKey.toString() );

        // Figure out if there is an eavesdropper!
        int numDiff = 0;
        for ( int i = 0; i < Math.min( aKey.length(), bKey.length() ); i++ )
        {
            if ( aKey.charAt( i ) != bKey.charAt( i ) )
            {
                numDiff++;
            }
        }

        if ( ( double ) numDiff / aKey.length() > 0.36 )
        {
            // There is probably an eavesdropper!
            System.out.println( "There's an eavesdropper! (maybe) Come back and try again later." );
        } else System.out.println( "There is no Eavesdropper (hopefully)." );

        // Only use the part of the key that we didn't just reveal
        StringBuilder endKey = new StringBuilder();
        for ( int i = 0; i < key.length(); i++ )
        {
            if ( i % 4 != 0 )
                endKey.append( key.charAt( i ) );
        }
        return endKey.toString();
    }
}
