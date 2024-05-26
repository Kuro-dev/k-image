package org.kurodev.kimage.kimage.draw;

import org.kurodev.kimage.kimage.font.KFont;
import org.kurodev.kimage.kimage.font.glyph.Coordinate;
import org.kurodev.kimage.kimage.font.glyph.FontGlyph;
import org.kurodev.kimage.kimage.img.ChunkHandler;
import org.kurodev.kimage.kimage.img.SimplePng;
import org.kurodev.kimage.kimage.img.SimplePngDecoder;
import org.kurodev.kimage.kimage.img.SimplePngEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DrawableImage implements KImage {
    private static final Logger logger = LoggerFactory.getLogger(DrawableImage.class);
    private final Map<String, byte[]> customChunks;
    private final SimplePngDecoder decoder = new SimplePngDecoder();
    private final SimplePng png;

    public DrawableImage(int width, int height) {
        png = new SimplePng(width, height);
        customChunks = new HashMap<>();
    }

    public DrawableImage(SimplePng pngHandler, Map<String, byte[]> chunks) {
        png = pngHandler;
        customChunks = chunks;
    }

    public static DrawableImage ofBytes(byte[] bytes) {
        var out = new DrawableImage(0, 0);
        try {
            out.decode(bytes);
            return out;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrawableImage that = (DrawableImage) o;
        return Objects.equals(png, that.png) &&
                customChunks.entrySet().stream().allMatch(entry -> {
                    if (that.customChunks.containsKey(entry.getKey())) {
                        return Arrays.equals(entry.getValue(), that.customChunks.get(entry.getKey()));
                    }
                    return false;
                });
    }

    @Override
    public int hashCode() {
        return Objects.hash(customChunks, png);
    }

    @Override
    public KImage drawLine(int x1, int y1, int x2, int y2, Color color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            png.writeColor(x1, y1, color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
        return this;
    }


    @Override
    public DrawableImage drawRect(int x, int y, int dx, int dy, Color color) {
        drawLine(x, y, x + dx, y, color); //horizontal top line
        drawLine(x, y + dy, x + dx, y + dy, color); //horizontal bottom line
        drawLine(x, y, x, y + dy, color); //vertical left line
        drawLine(x + dx, y, x + dx, y + dy + 1, color); //vertical right line
        return this;
    }

    @Override
    public DrawableImage fillRect(int x, int y, int dx, int dy, Color color) {
        int xPos = x + dx;
        int yPos = y + dy;
        for (int writeX = x; writeX < xPos; writeX++) {
            for (int writeY = y; writeY < yPos; writeY++) {
                png.writeColor(writeX, writeY, color);
            }
        }
        return this;
    }

    @Override
    public DrawableImage fillCircle(int centerX, int centerY, int radius, Color color) {
        int x = radius;
        int y = 0;
        int radiusError = 1 - x;

        while (x >= y) {
            drawLine(centerX + x, centerY + y, centerX - x, centerY + y, color);
            drawLine(centerX + y, centerY + x, centerX - y, centerY + x, color);
            drawLine(centerX - x, centerY - y, centerX + x, centerY - y, color);
            drawLine(centerX - y, centerY - x, centerX + y, centerY - x, color);
            y++;
            if (radiusError < 0) {
                radiusError += 2 * y + 1;
            } else {
                x--;
                radiusError += 2 * (y - x + 1);
            }
        }
        return this;
    }

    private boolean isOOB(int x, int y) {
        return x < 0 || y < 0 || x > png.getWidth() || y > png.getHeight();
    }

    @Override
    public DrawableImage drawCircle(int centerX, int centerY, int radius, Color color) {
        double angleIncrement = 1.0 / radius;
        for (double angle = 0; angle < 2 * Math.PI; angle += angleIncrement) {
            int x = (int) Math.round(centerX + radius * Math.cos(angle));
            int y = (int) Math.round(centerY + radius * Math.sin(angle));
            if (isOOB(x, y)) {
                continue;
            }
            png.writeColor(x, y, color);
        }
        return this;
    }

    /**
     * Encodes the image to PNG format
     *
     * @return a byte array representing the entire image file
     */
    @Override
    public byte[] encode() {
        SimplePngEncoder encoder = new SimplePngEncoder(png, customChunks);
        try {
            return encoder.encodeToPng();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A adds handler for a specific chunktype in the image file.
     * The handler will be called when the specific chunk is being encountered.
     */
    public void addChunkHandler(String chunkType, ChunkHandler handler) {
        decoder.addChunkHandler(chunkType, handler);
    }

    /**
     * decodes a PNG image and puts it into this object
     */
    private void decode(byte[] png) throws IOException {
        decoder.decodePng(png);
        var img = decoder.getImage();
        this.png.override(img.png);
        this.customChunks.clear();
        this.customChunks.putAll(img.customChunks);
    }

    /**
     * Adds a new custom chunk to the image data.
     */
    @Override
    public void addCustomChunk(String type, byte[] data) {
        customChunks.put(type, data);
    }

    @Override
    public void addCustomChunk(String type, String data) {
        customChunks.put(type, data.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getChunkString(String type) {
        return new String(customChunks.get(type), StandardCharsets.UTF_8);
    }

    @Override
    public byte[] getChunk(String type) {
        return customChunks.get(type);
    }

    /**
     * Fills the entire image with a specific color.
     */
    @Override
    public DrawableImage fill(Color color) {
        fillRect(0, 0, getWidth(), getHeight(), color);
        return this;
    }

    @Override
    public DrawableImage drawPixel(int x, int y, Color color) {
        png.writeColor(x, y, color);
        return this;
    }

    private KImage drawGlyph(int x, int y, FontGlyph glyph, Color color, double scale) {
        if (glyph.getNumberOfContours() == 0) {
            // ignore empty glyphs such as spaces etc.
            return this;
        }

        Coordinate[][] contours = glyph.getCoordinates();
        Coordinate prev = null;
        for (Coordinate[] contour : contours) {
            prev = contour[0];
            for (int i = 1; i < contour.length; i++) {
                Coordinate point = contour[i];
                int prevX = (int) Math.round(x + (prev.x() * scale));
                int prevY = (int) Math.round(y + (prev.y() * scale));
                int nextX = (int) Math.round(x + (point.x() * scale));
                int nextY = (int) Math.round(y + (point.y() * scale));
                drawLine(prevX, prevY, nextX, nextY, color);
                prev = point;
            }
            //connect first and last point of the contour
            int firstX = (int) Math.round(x + (contour[0].x() * scale));
            int firstY = (int) Math.round(y + (contour[0].y() * scale));
            int lastX = (int) Math.round(x + (contour[contour.length - 1].x() * scale));
            int lastY = (int) Math.round(y + (contour[contour.length - 1].y() * scale));
            drawLine(firstX, firstY, lastX, lastY, color);
        }
        return this;
    }


    public DrawableImage drawBezierCurve(Coordinate start, Coordinate end, Coordinate curve, Color color, int steps) {
        double stepSize = 1.0 / steps;
        Coordinate previous = start;
        for (int i = 1; i <= steps; i++) {
            double t = stepSize * i;
            double x = Math.pow(1 - t, 2) * start.x() + 2 * t * (1 - t) * curve.x() + Math.pow(t, 2) * end.x();
            double y = Math.pow(1 - t, 2) * start.y() + 2 * t * (1 - t) * curve.y() + Math.pow(t, 2) * end.y();
            drawLine(previous.x(), previous.y(), (int) x, (int) y, color);
            previous = new Coordinate((int) x, (int) y); // Update previous point
        }
        return this;
    }

    @Override
    public KImage draw(int x, int y, KImage img) {
        for (int dx = x; dx < img.getWidth(); dx++) {
            for (int dy = y; dy < img.getHeight(); dy++) {
                if (!isOOB(dx, dy))
                    drawPixel(dx, dy, img.getColor(dx, dy));
            }
        }
        return this;
    }

    @Override
    public KImage resize(int width, int height) {
        var out = new DrawableImage(width, height);
        out.draw(0, 0, this);
        return out;
    }

    @Override
    public KImage resize(double scale) {
        var out = new DrawableImage((int) (getWidth() * scale), (int) (getHeight() * scale));
        out.draw(0, 0, this);
        return out;
    }

    @Override
    public int getWidth() {
        return png.getWidth();
    }

    @Override
    public int getHeight() {
        return png.getHeight();
    }

    @Override
    public Color getColor(int x, int y) {
        if (isOOB(x, y)) {
            logger.warn("Target coordinate is out of bounds [{}|{}]", x, y);
            return null;
        }
        final int r = 0, g = 1, b = 2, a = 3;
        var col = png.readColor(x, y);
        return new Color(col[r], col[g], col[b], col[a]);
    }

    public KImage drawString(int x, int y, String str, Color color, KFont font, int fontSize) {
        int lowestPPEM = font.getLowestRecommendedPPEM();
        if (fontSize < lowestPPEM) {
            logger.warn("Provided fontSize {} pixels is less than the lowest recommended height {} pixels." +
                    " This may result in poor rendering quality.", fontSize, lowestPPEM);
        }
        if (fontSize % lowestPPEM != 0) {
            int lowerRecommendation = ((int) Math.floor(((double) fontSize / lowestPPEM)) * lowestPPEM);
            int higherRecommendation = ((int) Math.ceil(((double) fontSize / lowestPPEM)) * lowestPPEM);
            logger.warn("fontsize is not a multiple of the lowest PPEM, and might look wrong. " +
                    "Recommended alternative sizes to {}: {} or {}", fontSize, lowerRecommendation, higherRecommendation);
            logger.warn("Enforcing fontsize: {}px", lowerRecommendation);
            fontSize = lowerRecommendation;
        }
        for (int i = 0; i < str.length(); i++) {
            var glyph = font.getGlyph(str.charAt(i));
            logger.info("Drawing glyph {} at {}x{}", glyph, x, y);
            int glyphHeight = glyph.getyMax() - glyph.getyMin();
            // Calculate the scale factor based on the target height
            double scale = (double) fontSize / glyphHeight;
            drawGlyph(x, y, glyph, color, scale);
            x += (int) Math.ceil(glyph.getAdvanceWidth() * scale);
        }
        return this;
    }

}
