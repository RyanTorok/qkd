package qkd;

import java.io.Serializable;

/*
This class has been provided to help streamline the manipulation of complex numbers.
You may add additional functions if you like but you may not alter the functions which have been provided for you.
The grading script may rely on the existing functions and altering their behavior may cause undefined behavior
within the grading script.

This class implements the Serializable interface so that objects of it's type
can be easily sent through sockets (between computers).
 */

public class complex implements Serializable{
    double real;
    double im;

    public complex(double real, double im) {
        this.real = real;
        this.im = im;
    }

    //Return magnitude squared of amplitude
    static double magnitude2(complex amplitude){
        return amplitude.real*amplitude.real + amplitude.im*amplitude.im;
    }

    //Returns c1 + c1
    static complex add(complex c1, complex c2){
        return new complex(c1.real+c2.real, c1.im+c2.im);
    }

    //Returns c1 - c2
    static complex subtract(complex c1, complex c2){
        return new complex(c1.real-c2.real, c1.im-c2.im);
    }

    //Returns scalar*c1
    static complex multiply(double scalar, complex c1){
        return new complex(scalar*c1.real, scalar*c1.im);
    }
}
