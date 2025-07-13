package org.kurodev.sound.wav;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

public class SineWaveGenerator {
    private final List<Frequency> frequencies = new ArrayList<>();
    @Getter
    private final FrequencyMode mode;

    private SineWaveGenerator(FrequencyMode mode) {
        this.mode = mode;
    }

    public static SineWaveGenerator of(FrequencyMode mode, double frequency) {
        return of(mode, frequency, 1);
    }

    public static SineWaveGenerator of(FrequencyMode mode, double frequency, double weight) {
        return new SineWaveGenerator(mode).addFrequency(frequency, weight);
    }

    public SineWaveGenerator addFrequency(double frequency) {
        return addFrequency(frequency, 1);
    }

    public SineWaveGenerator addFrequency(double frequency, double weight) {
        frequencies.add(new Frequency(frequency, weight));
        return this;
    }

    public int size() {
        return frequencies.size();
    }


    public Frequency getFrequency(int index) {
        return frequencies.get(index);
    }

    public enum FrequencyMode {
        ADDITIVE {
            @Override
            public double apply(double a, double val) {
                return a + val;
            }
        },
        MULTIPLICATIVE {
            @Override
            public double apply(double a, double val) {
                return a * val;
            }
        };

        public abstract double apply(double a, double val);
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public class Frequency {
        private final double frequency;
        private final double weight;
    }

}
