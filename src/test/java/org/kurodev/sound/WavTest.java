package org.kurodev.sound;

import org.junit.jupiter.api.Test;
import org.kurodev.sound.wav.SineWaveGenerator;
import org.kurodev.sound.wav.Track;
import org.kurodev.sound.wav.WavPlayer;

import java.nio.file.Path;

public class WavTest {
    private static final Path TEST_FILE_MONO = Path.of("./testWAVs/whiteNoiseMono.wav");
    private static final Path TEST_FILE_STEREO = Path.of("./testWAVs/whiteNoiseStereo.wav");

    @Test
    public void test1() {
        SineWaveGenerator generator = SineWaveGenerator.of(SineWaveGenerator.FrequencyMode.ADDITIVE, 1000)
                .addFrequency(100);
        Track t = Track.ofSineWave(5, generator);
        WavPlayer player = new WavPlayer(t);
        player.playAndWait();

    }
}
