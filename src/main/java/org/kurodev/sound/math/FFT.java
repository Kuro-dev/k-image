package org.kurodev.sound.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class FFT {
    private static int findNextHighestPowerOf2(int n) {
        int num = 1;
        while (n > 0) {
            num <<= 1;
            n >>= 1;
        }
        return num;
    }

    public static Complex[] fft(Complex[] input) {
        int n = input.length;

        if (n == 1) return new Complex[]{input[0]};

        if (n % 2 != 0) {
            throw new IllegalArgumentException("Input must have power of 2 elements");
        }

        //calculate even FFTs
        Complex[] even = new Complex[n / 2];
        for (int i = 0; i < n / 2; i++) {
            even[i] = input[i * 2];
        }
        Complex[] evenFFT = fft(even);

        //calculate odd FFTs
        Complex[] odd = new Complex[n / 2];
        for (int i = 0; i < n / 2; i++) {
            odd[i] = input[i * 2 + 1];
        }
        Complex[] oddFFT = fft(odd);

        //combine
        Complex[] output = new Complex[n];
        for (int i = 0; i < n / 2; i++) {
            double ith = -2 * i * Math.PI / n;
            Complex wk = new Complex(Math.cos(ith), Math.sin(ith));
            output[i] = evenFFT[i].plus(wk.times(oddFFT[i]));
            output[i + n / 2] = evenFFT[i].minus(wk.times(oddFFT[i]));
        }
        return output;
    }

    public static Complex[] fft(double[] input) {
        Complex[] numbers = new Complex[input.length];
        for (int i = 0; i < input.length; i++) {
            numbers[i] = new Complex(input[i], 0);
        }
        return fft(numbers);
    }

    public static Result[] findFrequencies(double[] input, int sampleRate) {
        double[] numbers = new double[findNextHighestPowerOf2(input.length)];
        System.arraycopy(input, 0, numbers, 0, input.length);
        Complex[] fft = fft(numbers);
        Result[] results = new Result[fft.length / 2];
        for (int i = 0; i < results.length; i++) {
            double freq = (double) ((i) * sampleRate) / fft.length; // maybe should be i + 1, but I don't know.
            double amplitude = fft[i].length();
            double phase = fft[i].angle();
            results[i] = new Result(freq, amplitude, phase);
        }
        return results;
    }

    public static Result findLoudestFrequency(Result[] results) {
        Result loudest = results[0];
        for (Result result : results) {
            if (result.amplitude > loudest.amplitude) {
                loudest = result;
            }
        }
        return loudest;
    }

    @AllArgsConstructor
    @Getter
    public static class Result {
        private double frequency;
        private double amplitude;
        private double phase;

        @Override
        public String toString() {
            return "frequency: " + frequency + ", amplitude: " + amplitude + ", phase: " + phase;
        }
    }
}
