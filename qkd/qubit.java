package qkd;


/*
Name:       Ryan Torok
UT EID's:   rt24776

 */


import java.io.Serializable;
import static java.lang.Math.random;
import static java.lang.Math.sqrt;

/*
This Class is for you to implement
Measurement functions will return strings
 "0", "1", "+", "-"

If you return 0 instead of "0" for example, the testing script
will certainly break.

You are encouraged to look at the complex.java class,
it has been provided for you.
 */
//comment
public class qubit implements Serializable{

    private complex c1, c2;
    static final double sr2 = Math.sqrt(2);

    public qubit() {
	    c1 = new complex(0,0);
	    c2 = new complex(0,0);
    }

    public void prepZero(){
	c1.real = 1;
	c1.im = 0;
	c2.real = 0;
	c2.im = 0; 
    }

    public void prepOne(){
	c1.real = 0;
	c1.im = 0;
	c2.real = 1;
	c2.im = 0;
    }

    public void prepPlus(){
	c1.real = 1/sr2;
	c1.im = 0;
	c2.real = 1/sr2;
	c2.im = 0;
    }

    public void prepMinus(){
	c1.real = 1/sr2;
	c1.im = 0;
	c2.real = -1/sr2;
	c2.im = 0;
    }

    public String measureZeroOne() {
	    double mag = Math.sqrt(complex.magnitude2(c1) + complex.magnitude2(c2));
	    if (Math.random() < complex.magnitude2(complex.multiply(1/mag, c1))) {
		    prepZero();
		    return "0";
	    } else {
		    prepOne();
		    return "1";
	    }
    }

    public String measurePlusMinus() {
	    complex alphaPrime = complex.multiply(1/sr2, (complex.add(c1, c2)));
	    complex betaPrime = complex.multiply(1/sr2, (complex.subtract(c1, c2)));
	    double mag = Math.sqrt(complex.magnitude2(alphaPrime) + complex.magnitude2(betaPrime));
	    if (Math.random() < complex.magnitude2(complex.multiply(1/mag, alphaPrime))) {
		    prepPlus();
		    return "+";
	    } else {
		    prepMinus();
		    return "-";	
	    }
    }

    public void pauliX(){
	    complex temp = c1;
	    c1 = c2;
	    c2 = temp;
    }

    public void pauliZ(){
	    c2 = complex.multiply(-1, c2);
    }

    public void hadamard(){
	    complex c1Old = c1, c2Old = c2;
	    c1 = complex.add(c1Old, c2Old);
	    c2 = complex.subtract(c1Old, c2Old);
	    c1 = complex.multiply(1/sr2, c1);
	    c2 = complex.multiply(1/sr2, c2);
    }
}
