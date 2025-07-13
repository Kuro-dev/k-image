package org.kurodev.sound.wav;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@AllArgsConstructor
@Builder
@Getter
@Slf4j
public class Track {
    public static final int DEFAULT_SAMPLE_RATE = 44100;

    private final int sampleRate;
    private final double[][] data;

    public static Track of(InputStream is) throws IOException {
        return WavDecoder.decode(is);
    }

    public int getNumOfChannels() {
        return data.length;
    }

    public int getNumOfSamples() {
        return data[0].length;
    }

    public double getSample(int channel, int index) {
        return data[channel][index];
    }

    public double getMaxAmplitude() {
        return Arrays.stream(data)
                .flatMapToDouble(Arrays::stream)
                .max()
                .orElseThrow(() -> new IllegalStateException("No data"));
    }

    public void changeVolume(double volume) {
        for (int n = 0; n < data.length; n++) {
            for (int i = 0; i < data[n].length; i++) {
                data[n][i] *= volume;
            }
        }
    }

    public void normalize() {
        changeVolume(1 / getMaxAmplitude());
    }

    public static Track of(int sampleRate, int length, TimeUnit timeUnit, Function<Double, Double> generatorFn) {
        long len = timeUnit.toSeconds(length) * sampleRate;
        if (len > Integer.MAX_VALUE) {
            throw new IllegalStateException("(Length * Samplerate) is too long, please reduce either");
        }
        double[][] data = new double[1][];
        data[0] = new double[(int) len];
        for (int i = 0; i < data[0].length; i++) {
            data[0][i] = generatorFn.apply(Double.valueOf(i));
        }
        return new Track(sampleRate, data);
    }

    public static Track of(int length, TimeUnit timeUnit, Function<Double, Double> generatorFn) {
        return of(44100, length, timeUnit, generatorFn);
    }

    public static Track of(int lengthSeconds, Function<Double, Double> generatorFn) {
        return of(lengthSeconds, TimeUnit.SECONDS, generatorFn);
    }

    public static Track ofSineWave(int lengthSeconds, double frequency) {
        double pi2Frequency = (2 * Math.PI * frequency) / DEFAULT_SAMPLE_RATE;
        return of(lengthSeconds, a -> Math.sin(a * pi2Frequency));
    }

    public static Track ofSineWave(int lengthSeconds, SineWaveGenerator generator) {
        final double pi2Frequency = (2 * Math.PI) / DEFAULT_SAMPLE_RATE;
        final int size = generator.size();
        Track t = of(lengthSeconds, a -> {
            double result = generator.getMode().ordinal();
            for (int i = 0; i < size; i++) {
                var frequency = generator.getFrequency(i);
                result = generator.getMode().apply(result, Math.sin(frequency.getFrequency() * a * pi2Frequency) * frequency.getWeight());
            }
            return result;
        });
        t.normalize();
        return t;
    }

    public static Track ofWhiteNoise(int lengthSeconds) {
        return of(lengthSeconds, a -> Math.random() * 2 - 1);
    }

    public void encode(OutputStream out) throws IOException {
        WavDecoder.encode(this, out);
    }

    public void encode(Path p) throws IOException {
        encode(Files.newOutputStream(p));
    }
}
