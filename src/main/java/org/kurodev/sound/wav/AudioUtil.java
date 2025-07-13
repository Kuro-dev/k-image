package org.kurodev.sound.wav;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class AudioUtil {

    private static final boolean IS_WINDOWS =
            System.getProperty("os.name").toLowerCase().contains("win");

    private AudioUtil() {
    }   // utility class – no instantiation

    /**
     * Ensure the given AudioInputStream can be opened by a Clip on all
     * platforms (Windows in particular). If the stream is already in
     * 8‑ or 16‑bit PCM, returns the same reference; otherwise converts
     * to 16‑bit PCM on the fly.
     */
    public static AudioInputStream ensurePlayable(Track track) throws UnsupportedAudioFileException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        track.encode(baos);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream((new ByteArrayInputStream(baos.toByteArray())));
        AudioFormat src = audioStream.getFormat();

        //If we not on windows it's fine. 8‑ or 16‑bit signed PCM is also fine everywhere.
        if (!IS_WINDOWS ||
                (src.getEncoding() == AudioFormat.Encoding.PCM_SIGNED
                        && (src.getSampleSizeInBits() == 8 || src.getSampleSizeInBits() == 16)
                        && AudioSystem.isLineSupported(new DataLine.Info(Clip.class, src)))) {
            return audioStream;
        }

        // Windows does not support 32 bit little endian Mono streams natively (in Java).
        //I've got to convert to 16 bit. Luckily Java can Handle that for some reason.
        AudioFormat dst = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                src.getSampleRate(),
                16,
                src.getChannels(),
                src.getChannels() * 2,
                src.getSampleRate(),
                false);

        // Verify the target format is actually supported (paranoia).
        if (!AudioSystem.isConversionSupported(dst, src)) {
            throw new UnsupportedAudioFileException(
                    "Cannot convert from " + src + " to " + dst);
        }

        return AudioSystem.getAudioInputStream(dst, audioStream);
    }
}

