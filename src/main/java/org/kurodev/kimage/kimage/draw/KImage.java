package org.kurodev.kimage.kimage.draw;

import org.kurodev.kimage.kimage.font.FontReader;
import org.kurodev.kimage.kimage.font.FontReaders;
import org.kurodev.kimage.kimage.img.SimplePng;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface KImage {
    static KImage empty(int width, int height) {
        return new DrawableImage(width, height);
    }

    static KImage ofBytes(byte[] data) {
        return DrawableImage.ofBytes(data);
    }

    default KImage drawString(int x, int y, String str) {
        return drawString(x, y, str, Color.BLACK, FontReaders.getDefaultFontReader());
    }

    default KImage drawString(int x, int y, String str, Color color) {
        return drawString(x, y, str, color, FontReaders.getDefaultFontReader());
    }

    KImage drawString(int x, int y, String str, Color color, FontReader font);

    KImage drawPixel(int x, int y, Color color);

    KImage drawLine(int x1, int y1, int x2, int y2, Color color);

    default KImage drawLine(int x1, int y1, int x2, int y2, Color color, int thickness) {
        for (int i = 0; i < thickness; i++) {
            drawLine(x1 + i, y1, x2 + i, y2, color);
            drawLine(x1, y1 + i, x2, y2 + i, color);
        }
        return this;
    }

    KImage drawRect(int x, int y, int dx, int dy, Color color);

    KImage fillRect(int x, int y, int dx, int dy, Color color);

    KImage fillCircle(int centerX, int centerY, int radius, Color color);

    KImage drawCircle(int centerX, int centerY, int radius, Color color);

    /**
     * Encodes the image to PNG format
     *
     * @return a byte array representing the entire image file
     */
    byte[] encode();

    SimplePng getPng();

    /**
     * Adds a new custom chunk to the image data.
     */
    void addCustomChunk(String type, byte[] data);

    void addCustomChunk(String type, String data);

    String getChunkString(String type);

    byte[] getChunk(String type);

    /**
     * Fills the entire image with a specific color.
     */
    KImage fill(Color color);

}
