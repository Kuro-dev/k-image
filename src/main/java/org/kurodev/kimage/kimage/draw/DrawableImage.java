package org.kurodev.kimage.kimage.draw;

import org.kurodev.kimage.kimage.font.FontReader;
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
import java.util.List;
import java.util.*;

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

        while (x1 != x2 || y1 != y2) {
            png.writeColor(x1, y1, color); // Draw pixel at (x1, y1)
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
        this.png.override(img.getPng());
        this.customChunks.clear();
        this.customChunks.putAll(img.customChunks);
    }

    @Override
    public SimplePng getPng() {
        return png;
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
        fillRect(0, 0, getPng().getWidth(), getPng().getHeight(), color);
        return this;
    }

    @Override
    public DrawableImage drawPixel(int x, int y, Color color) {
        png.writeColor(x, y, color);
        return this;
    }

    private KImage drawGlyph(int x, int y, FontGlyph glyph, Color color) {
        if (glyph.getNumberOfContours() == 0) {
            // ignore empty glyphs such as .notdef and spaces etc.
            return this;
        }

        List<Coordinate> coordinates = glyph.getCoordinates();
        int[] endPts = glyph.getEndPtsOfContours();
        int startPt = 0;
        int currentX = x;
        int currentY = y;

        for (int contour = 0; contour < glyph.getNumberOfContours(); contour++) {
            int endPt = endPts[contour];
            logger.info("Drawing contour: {}", contour);
            Coordinate firstPointOfContour = null;
            // Store starting point of the contour
            int startPointX = currentX;
            int startPointY = currentY;

            for (int pt = startPt; pt <= endPt; pt++) {
                Coordinate offset = coordinates.get(pt);
                if (firstPointOfContour == null) {
                    firstPointOfContour = offset;
                }
                int nextX = currentX + offset.x();
                int nextY = currentY + offset.y();

                if (pt > startPt) {
                    drawLine(currentX, currentY, nextX, nextY, color, 5);
                }

                currentX = nextX;
                currentY = nextY;
            }

            assert firstPointOfContour != null;
            // Draw closing line from last point to the first point in the contour
            drawLine(currentX, currentY, startPointX + firstPointOfContour.x(), startPointY + firstPointOfContour.y(), color, 5);

            startPt = endPt + 1;
        }

        return this;
    }

    public KImage drawString(int x, int y, String str, Color color, FontReader font) {
        for (int i = 0; i < str.length(); i++) {
            var glyph = font.getGlyph(str.charAt(i));
            logger.info("Drawing glyph {} at {}x{}", glyph, x, y);
            drawGlyph(x, y, glyph, color);
            x += glyph.getAdvanceWidth();
        }
        return this;
    }

}
