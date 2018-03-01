package qkd;

import java.io.IOException;

public class Eve extends Party
{
    public static void main( String args[] ) throws IOException, ClassNotFoundException, InterruptedException
    {
        Initialize( Identity.Eve );
        for ( int i = 0; i < config.numProtocolRuns; i++ )
        {
            String secureBits = protocol();

            getStatistics( secureBits );
            doCryptography( secureBits );
        }
    }

    public static String protocol() throws IOException, InterruptedException
    {
        // Eve steals the photons that should have gone to Bob!

        // Determine the random bases that we are going to measure in
        // 0 -> H/V
        // 1 -> D/A
        String myBases = photon.randomBits( config.numberofBits );

        // Keep receiving photons until we have them all
        StringBuilder results = new StringBuilder();
        for ( int i = 0; i < myBases.length(); i++ )
        {
            // Check the repetition block number of times with the same base
            for ( int j = 0; j < config.repetitionCodeBlockSize; j++ )
            {
                photon nextPhoton = getPhoton();

                // Measure the photon in a random base (as determined earlier)
                // to construct our result string and send new photons to Bob
                if ( myBases.charAt( i ) == '0' )
                {
                    // Measure in the H/V base
                    nextPhoton.filterH();
                    if ( nextPhoton.detect() )
                    {
                        results.append( "0" );

                        // Send a photon to Bob that is the same as what we measured
                        photon newPhoton = new photon();
                        newPhoton.prepH();
                        sendPhoton( newPhoton, Identity.Bob );
                    } else
                    {
                        results.append( "1" );

                        // Send a photon to Bob that is the same as what we measured
                        photon newPhoton = new photon();
                        newPhoton.prepV();
                        sendPhoton( newPhoton, Identity.Bob );
                    }
                } else
                {
                    // Measure in the D/A base
                    nextPhoton.filterD();
                    if ( nextPhoton.detect() )
                    {
                        results.append( "0" );

                        // Send a photon to Bob that is the same as what we measured
                        photon newPhoton = new photon();
                        newPhoton.prepD();
                        sendPhoton( newPhoton, Identity.Bob );
                    } else
                    {
                        results.append( "1" );

                        // Send a photon to Bob that is the same as what we measured
                        photon newPhoton = new photon();
                        newPhoton.prepA();
                        sendPhoton( newPhoton, Identity.Bob );
                    }
                }
            }
        }

        // Eve has intercepted the photons!
        // Now Alice and Bob announce their bases, Eve can easily intercept this data

        String bases1 = getString();
        String bases2 = getString();

        StringBuilder key = new StringBuilder();

        // Eve needs to condense her key and discard anywhere that Alice and Bob
        // measured in different bases since they'll both be discarding this data
        for ( int i = 0; i < bases1.length(); i++ )
        {
            if ( bases2.charAt( i ) == bases1.charAt( i ) )
            {
                // Take the average of the three bits corresponding to that correct basis pair
                char newChar = ( char ) ( Math.round( ( results.charAt( i * 3 ) +
                        results.charAt( i * 3 + 1 ) +
                        results.charAt( i * 3 + 2 ) ) / 3.0 ) );
                key.append( newChar );
            }
        }

        // Disregard the portion of the key that Alice and Bob swap
        // Receive what they swap because not intercepting may
        // cause an error in the other code
        String keySwap1 = getString();
        String keySwap2 = getString();
        StringBuilder endKey = new StringBuilder();
        for ( int i = 0; i < key.length(); i++ )
        {
            if ( i % 4 != 0 )
                endKey.append( key.charAt( i ) );
        }

        // Eve has the key! Maybe Alice and Bob detected her... maybe not
        return endKey.toString();
    }
}
