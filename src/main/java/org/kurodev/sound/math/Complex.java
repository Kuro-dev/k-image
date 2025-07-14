package org.kurodev.sound.math;

import lombok.Getter;

@Getter
public class Complex {
    private final double real;
    private final double imag;

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public double length() {
        return Math.sqrt(real * real + imag * imag);
    }

    public double angle() {
        return Math.atan2(imag, real);
    }

    public Complex plus(Complex other) {
        return new Complex(real + other.real, imag + other.imag);
    }

    public Complex minus(Complex other) {
        return new Complex(real - other.real, imag - other.imag);
    }

    public Complex times(Complex other) {
        double real = this.real * other.real - this.imag * other.imag;
        double imag = this.real * other.imag + this.imag * other.real;
        return new Complex(real, imag);
    }

    public Complex scale(double scale) {
        return new Complex(real * scale, imag * scale);
    }

    @Override
    public String toString() {
        return "(" + real + "," + imag + ")";
    }
}