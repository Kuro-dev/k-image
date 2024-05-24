package org.kurodev.kimage.kimage.img;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class SimplePng {
    private static final Logger logger = LoggerFactory.getLogger(SimplePng.class);
    private int height;
    private int width;
    private byte[] imageData;
    private byte[] alphaChannel;

    // Constructor
    public SimplePng(int width, int height) {
        this.width = width;
        this.height = height;

        this.imageData = new byte[width * height * 4];
        this.alphaChannel = new byte[width * height];
    }

    @Override
    public String toString() {
        return "SimplePng{" +
                "height=" + height +
                ", width=" + width +
                ", imageData=" + imageData.length +
                ", alphaChannel=" + alphaChannel.length +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePng simplePng = (SimplePng) o;
        return height == simplePng.height &&
                width == simplePng.width &&
                Arrays.equals(imageData, simplePng.imageData) &&
                Arrays.equals(alphaChannel, simplePng.alphaChannel);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(height, width);
        result = 31 * result + Arrays.hashCode(imageData);
        result = 31 * result + Arrays.hashCode(alphaChannel);
        return result;
    }

    /**
     * Writes a pixel in the image
     *
     * @param x     The X position in the image
     * @param y     The Y position in the image
     * @param red   The red value (0-255)
     * @param green The green value (0-255)
     * @param blue  The blue value (0-255)
     * @param alpha The Alpha value (0-255)
     *              0 = transparent
     *              255 = opaque
     */
    public void writeColor(int x, int y, int red, int green, int blue, int alpha) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            logger.warn("Attempted to draw out of bounds [{}|{}] for image of size [{}|{}]", x, y, width, height);
            return;
        }

        int index = (y * width + x) * 4; // 4 bytes per pixel (RGBA)
        imageData[index] = (byte) red;
        imageData[index + 1] = (byte) green;
        imageData[index + 2] = (byte) blue;
        imageData[index + 3] = (byte) alpha;

        // Store alpha channel data
        alphaChannel[y * width + x] = (byte) alpha;
    }

    /**
     * @see #writeColor(int, int, int, int, int, int)
     */
    public void writeColor(int x, int y, Color color) {
        writeColor(x, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    // Read a color from a specific position
    public int[] readColor(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }

        int index = (y * width + x) * 4;
        int red = imageData[index] & 0xFF;
        int green = imageData[index + 1] & 0xFF;
        int blue = imageData[index + 2] & 0xFF;
        int alpha = imageData[index + 3] & 0xFF;

        return new int[]{red, green, blue, alpha};
    }

    // Check if the image has an alpha channel
    boolean hasAlphaChannel() {
        for (byte pixel : alphaChannel) {
            if (pixel != (byte) 255) {
                return true;
            }
        }
        return false;
    }

    // Get the alpha channel data
    public byte[] getAlphaChannel() {
        return alphaChannel;
    }

    void setAlphaChannel(byte[] alphaChannel) {
        this.alphaChannel = alphaChannel;
    }

    // Getters and Setters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getImageData() {
        return imageData;
    }

    void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public void override(SimplePng other) {
        this.imageData = other.imageData;
        this.alphaChannel = other.alphaChannel;
        this.height = other.height;
        this.width = other.width;
    }
}
