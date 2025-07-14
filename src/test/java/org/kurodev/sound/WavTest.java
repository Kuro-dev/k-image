package org.kurodev.sound;

import org.junit.jupiter.api.Test;
import org.kurodev.sound.wav.Track;

import java.nio.file.Files;
import java.nio.file.Path;

public class WavTest {
    private static final Path TEST_FILE_MONO = Path.of("./testWAVs/whiteNoiseMono.wav");
    private static final Path TEST_FILE_STEREO = Path.of("./testWAVs/whiteNoiseStereo.wav");
    private static final Path TEST_FILE_POP = Path.of("./testWAVs/japan-city-pop.wav");

    @Test
    public void test1() throws Exception {
        Track t = Track.of(Files.newInputStream(TEST_FILE_POP));
        t.encode(Path.of("./pop.wav"));
    }
}
