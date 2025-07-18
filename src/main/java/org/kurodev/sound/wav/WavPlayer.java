package org.kurodev.sound.wav;

import lombok.SneakyThrows;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class WavPlayer {

    private final Clip clip;
    private CompletableFuture<Void> playing;

    public WavPlayer(InputStream audio) throws IOException {
        this(Track.of(audio));
    }

    /**
     * Returns the length of the track in milliseconds
     */
    public int getTrackLength() {
        return Math.toIntExact(TimeUnit.MICROSECONDS.toMillis(clip.getMicrosecondLength()));
    }

    /**
     * Sets the timer to play from the given millisecond position
     *
     * @param targetTimeMillis The timestamp in the Track.
     */
    public void setTrackPos(int targetTimeMillis) {
        clip.setMicrosecondPosition(TimeUnit.MILLISECONDS.toMicros(targetTimeMillis));
    }

    @SneakyThrows
    public WavPlayer(Track track) {
        //encoding to .WAV format
        AudioInputStream audioInputStream = AudioUtil.ensurePlayable(track);
        this.clip = AudioSystem.getClip();
        clip.open(audioInputStream);
    }

    public void playAndWait() {
        play();
        await();
    }

    public void play() {
        stop();
        CompletableFuture<Void> future = new CompletableFuture<>();
        clip.setFramePosition(0);
        future.completeAsync(() -> {
            clip.start();
            try {
                Thread.sleep(TimeUnit.MICROSECONDS.toMillis(clip.getMicrosecondLength()));
            } catch (Exception e) {
                clip.stop();
            }
            return null;
        });
        playing = future;
    }

    public void stop() {
        if (playing != null && !playing.isDone()) {
            playing.cancel(true);
        }
    }

    public void await() {
        if (playing != null && !playing.isDone()) {
            try {
                playing.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
