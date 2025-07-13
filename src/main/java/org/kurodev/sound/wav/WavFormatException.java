package org.kurodev.sound.wav;

import java.io.IOException;

public class WavFormatException extends IOException {
    public WavFormatException() {
    }

    public WavFormatException(String message) {
        super(message);
    }

    public WavFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public WavFormatException(Throwable cause) {
        super(cause);
    }
}
