package qkd;


/*
Name:       <enter here>
UT EID's:   <enter here>

 */


import java.io.Serializable;
import static java.lang.Math.random;
import static java.lang.Math.sqrt;
//import static qkd.complex.add;
//import static qkd.complex.multiply;
//import static qkd.complex.subtract;

/*
This Class is for you to implement
Measurement functions will return strings
 "0", "1", "+", "-"

If you return 0 instead of "0" for example, the testing script
will certainly break.

You are encouraged to look at the complex.java class,
it has been provided for you.
 */

public class qubit implements Serializable{


    //TODO implement constructor
    public qubit() {
    }

    //TODO
    public void prepZero(){
    }

    //TODO
    public void prepOne(){
    }

    //TODO
    public void prepPlus(){
    }

    //TODO
    public void prepMinus(){
    }

    //TODO (MUST RETURN SINGLE CHARACTER STRINGS)
    public String measureZeroOne(){
        return new String();
    }

    //TODO (MUST RETURN SINGLE CHARACTER STRINGS)
    public String measurePlusMinus(){
        return new String();
    }

    //TODO
    public void pauliX(){
    }

    //TODO
    public void pauliZ(){
    }
    //TODO
    public void hadamard(){
    }


}
