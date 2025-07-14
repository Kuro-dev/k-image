package org.kurodev.sound;

import org.junit.jupiter.api.Test;
import org.kurodev.sound.math.FFT;
import org.kurodev.sound.wav.Track;

import java.nio.file.Path;

public class WavTest {
    private static final Path TEST_FILE_MONO = Path.of("./testWAVs/whiteNoiseMono.wav");
    private static final Path TEST_FILE_STEREO = Path.of("./testWAVs/whiteNoiseStereo.wav");
    private static final Path TEST_FILE_POP = Path.of("./testWAVs/japan-city-pop.wav");

    @Test
    public void test1() throws Exception {
        Track track = Track.ofSineWave(1, 1000);
        FFT.Result[] results = FFT.findFrequencies(track.getData()[0], 44100);
        System.out.println(FFT.findLoudestFrequency(results));
    }
}
