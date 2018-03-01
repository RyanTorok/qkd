package qkd;


/*
 * Name:       Garrett Wezniak, Aeddon Chipman
 * UT EID's:   gw6846, alc4922
 */


import java.io.Serializable;

import static java.lang.Math.random;
import static java.lang.Math.sqrt;
import static qkd.complex.magnitude2;
import static qkd.complex.add;
import static qkd.complex.multiply;
import static qkd.complex.subtract;

/**
 * This Class is for you to implement
 * Measurement functions will return strings
 * "0", "1", "+", "-"
 *
 * If you return 0 instead of "0" for example, the testing script
 * will certainly break.
 *
 * You are encouraged to look at the complex.java class,
 * it has been provided for you.
 */
public class qubit implements Serializable
{
    // Represents a qubit's state as alpha|0> + beta|1>
    private complex alpha, beta;

    public qubit()
    {
        // Init to nothing (so that the prob of measuring anything is 0)
        alpha = new complex( 0, 0 );
        beta  = new complex( 0, 0 );
    }

    public void prepZero()
    {
        alpha = new complex( 1, 0 );
        beta = new complex( 0, 0 );
    }

    public void prepOne()
    {
        alpha = new complex( 0, 0 );
        beta = new complex( 1, 0 );
    }

    public void prepPlus()
    {
        alpha = new complex( 1 / sqrt( 2 ), 0 );
        beta = new complex( 1 / sqrt( 2 ), 0 );
    }

    public void prepMinus()
    {
        alpha = new complex( 1 / sqrt( 2 ), 0 );
        beta = new complex( -1 / sqrt( 2 ), 0 );
    }

    /**
     * Since alpha and beta are the coefficients of |0> and |1>,
     * simply get the magnitude squared of these coefficients to
     * get the probability of measurement
     * @return The measured value as a string literal, "0" or "1"
     */
    public String measureZeroOne()
    {
        // Round probabilities to five decimal places to fix issues with repeating decimals
        // Otherwise, repeating decimals can lead to having two probabilities that don't add to one
        double prob0 =( Math.round( magnitude2( alpha ) * 100000 ) / 100000.0 );
        double prob1 =( Math.round( magnitude2( beta ) * 100000 ) / 100000.0 );

        // Security Check: Make sure the probabilities add to one
        if ( prob0 + prob1 != 1 )
        {
            System.err.println( "Error: Probabilities didn't add to 1 when measuring in the 0/1 basis");
            System.exit( 1 );
        }

        boolean newState = random() < prob0;

        // Collapse the state
        if ( newState )
            prepZero();
        else prepOne();

        return newState ? "0" : "1";
    }

    /**
     * Since alpha and beta are the coefficients of |0> and |1>,
     * we must calculate the coefficients of |+> and |-> and then
     * take the magnitude squared of those coefficients to
     * get the probability of measurement
     * @return The measured value as a string literal, "+" or "-"
     */
    public String measurePlusMinus()
    {
        double probPlus = magnitude2( multiply( 1 / sqrt( 2 ), add( alpha, beta ) ) );
        double probMinus = magnitude2( multiply( 1 / sqrt( 2 ), subtract( alpha, beta ) ) );

        // Round probabilities to five decimal places to fix issues with repeating decimals
        // Otherwise, repeating decimals can lead to having two probabilities that don't add to one
        probPlus =( Math.round( probPlus * 100000 ) / 100000.0 );
        probMinus =( Math.round( probMinus * 100000 ) / 100000.0 );

        // Security Check: Make sure the probabilities add to one
        if ( probPlus + probMinus != 1 )
        {
            System.err.println( "Error: Probabilities didn't add to 1 when measuring in the +/- basis");
            System.exit( 1 );
        }

        boolean newState = random() < probPlus;

        // Collapse the state
        if ( newState )
            prepPlus();
        else prepMinus();

        return newState ? "+" : "-";
    }

    /**
     * | 0 1 |
     * | 1 0 |
     * Essentially just flip the values of alpha and beta
     */
    public void pauliX()
    {
        complex temp = alpha;
        alpha = beta;
        beta = temp;
    }

    /**
     * | 1 0  |
     * | 0 -1 |
     * Just make beta negative
     */
    public void pauliZ()
    {
        beta = multiply( -1, beta );
    }

    /**
     * | 1 1  |
     * | 1 -1 | all times 1 / sqrt( 2 )
     * This one is a bit more involved
     */
    public void hadamard()
    {
        complex tempAlpha = multiply( 1 / sqrt( 2 ), add( alpha, beta ) );
        complex tempBeta = multiply( 1 / sqrt( 2 ), subtract( alpha, beta ) );
        alpha = tempAlpha;
        beta = tempBeta;
    }
}