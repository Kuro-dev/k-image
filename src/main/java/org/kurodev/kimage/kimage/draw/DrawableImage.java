package org.kurodev.kimage.kimage.draw;

import org.kurodev.kimage.kimage.font.KFont;
import org.kurodev.kimage.kimage.font.enums.HeadTable;
import org.kurodev.kimage.kimage.font.glyph.FontGlyph;
import org.kurodev.kimage.kimage.font.glyph.FontStyle;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;
import org.kurodev.kimage.kimage.img.ChunkHandler;
import org.kurodev.kimage.kimage.img.SimplePng;
import org.kurodev.kimage.kimage.img.SimplePngDecoder;
import org.kurodev.kimage.kimage.img.SimplePngEncoder;
import org.kurodev.kimage.kimage.util.ContourHorizontalIntersects;
import org.kurodev.kimage.kimage.util.Transformation;
import org.kurodev.kimage.kimage.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

import static java.lang.Math.*;

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
        List<Coordinate> points = Util.calculateLinePoints(x1, y1, x2, y2);
        for (Coordinate point : points) {
            png.writeColor(point.x(), point.y(), color);
        }
        return this;
    }


    @Override
    public DrawableImage drawRect(int x, int y, int dx, int dy, Color color) {
        List<Coordinate> points = Util.calculateRectanglePoints(x, y, dx, dy);
        points.parallelStream().forEach(c -> drawPixel(c, color));

        return this;
    }

    public DrawableImage fillRect(Coordinate corner1, Coordinate corner2, Coordinate corner3, Coordinate corner4, Color color) {
        int minX = min(min(corner1.x(), corner2.x()), min(corner3.x(), corner4.x()));
        int minY = min(min(corner1.y(), corner2.y()), min(corner3.y(), corner4.y()));
        int maxX = max(max(corner1.x(), corner2.x()), max(corner3.x(), corner4.x()));
        int maxY = max(max(corner1.y(), corner2.y()), max(corner3.y(), corner4.y()));

        return fillRect(minX, minY, maxX - minX, maxY - minY, color);
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
        return x < 0 || y < 0 || x >= png.getWidth() || y >= png.getHeight();
    }

    @Override
    public DrawableImage drawCircle(int centerX, int centerY, int radius, Color color) {
        double angleIncrement = 1.0 / radius;
        for (double angle = 0; angle < 2 * Math.PI; angle += angleIncrement) {
            int x = (int) round(centerX + radius * Math.cos(angle));
            int y = (int) round(centerY + radius * Math.sin(angle));
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

    static int roundColorComponent(float x) {
        return min(255, max(0, round(x)));
    }

    static float convexCombination(float start, float end, float t) {
        return start + t*(end - start);
    }

    static Color combineOpaque(Color newWithOpacity, Color oldOpaque) {
        var a = (255 - newWithOpacity.getAlpha())/255.0f;
        return new Color(
                roundColorComponent(convexCombination(oldOpaque.getRed(), newWithOpacity.getRed(), a)),
                roundColorComponent(convexCombination(oldOpaque.getGreen(), newWithOpacity.getGreen(), a)),
                roundColorComponent(convexCombination(oldOpaque.getBlue(), newWithOpacity.getBlue(), a))
        );
    }

    @Override
    public DrawableImage drawPixel(int x, int y, Color color) {
        if(color.getAlpha() != 255) {
            int[] rgb = png.readColor(x, y);
            color = combineOpaque(color, new Color(rgb[0], rgb[1], rgb[2]));
        } else {
            color = new Color(color.getRed(), color.getGreen(), color.getBlue());
        }
        png.writeColor(x, y, color);
        return this;
    }

    @Override
    public DrawableImage drawBezierCurve(Coordinate start, Coordinate end, Coordinate curve, Color color, int steps) {
        Set<Coordinate> points = Util.calculateBezierCurve(start, end, curve, steps);
        for (Coordinate point : points) {
            drawPixel(point, color);
        }
        return this;
    }

    @Override
    public DrawableImage fillBezierCurve(Coordinate start, Coordinate end, Coordinate curve, Color color, int steps) {
        Set<Coordinate> points = Util.calculateBezierCurve(start, end, curve, steps);
        //draw a line to connect the start and end points.
        //otherwise the fill function cannot use ray-intersect properly
        points.addAll(Util.calculateLinePoints(start, end));
        points = Util.fillBezierCurve(points);
        for (Coordinate point : points) {
            drawPixel(point, color);
        }
        return this;
    }

    public DrawableImage fillArea(Coordinate start, Color color) {
        final Color colorToOverride = getColor(start.x(), start.y());
        if (colorToOverride == null) return this;
        if (colorToOverride.equals(color)) {
            logger.warn("Cannot override color with the same color");
            return this;
        }

        drawPixel(start, color);
        Deque<Coordinate> stack = new ArrayDeque<>(checkSameColourNeighbours(start, colorToOverride));
        while (!stack.isEmpty()) {
            Coordinate point = stack.pop();
            drawPixel(point, color);
            checkSameColourNeighbours(point, colorToOverride).forEach(stack::push);
        }
        return this;
    }

    private List<Coordinate> checkSameColourNeighbours(Coordinate point, Color color) {
        List<Coordinate> out = new ArrayList<>();
        Coordinate a = new Coordinate(point.x() + 1, point.y());
        Coordinate b = new Coordinate(point.x() - 1, point.y());
        Coordinate c = new Coordinate(point.x(), point.y() + 1);
        Coordinate d = new Coordinate(point.x(), point.y() - 1);
        if (color.equals(getColor(a))) {
            out.add(a);
        }
        if (color.equals(getColor(b))) {
            out.add(b);
        }
        if (color.equals(getColor(c))) {
            out.add(c);
        }
        if (color.equals(getColor(d))) {
            out.add(d);
        }
        return out;
    }

    @Override
    public DrawableImage draw(int x, int y, KImage img) {
        for (int dx = x; dx < img.getWidth(); dx++) {
            for (int dy = y; dy < img.getHeight(); dy++) {
                if (!isOOB(dx, dy))
                    drawPixel(dx, dy, img.getColor(dx, dy));
            }
        }
        return this;
    }

    @Override
    public DrawableImage resize(int width, int height) {
        var out = new DrawableImage(width, height);
        out.draw(0, 0, this);
        return out;
    }

    @Override
    public DrawableImage resize(double scale) {
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
            return null;
        }
        final int r = 0, g = 1, b = 2, a = 3;
        var col = png.readColor(x, y);
        return new Color(col[r], col[g], col[b], col[a]);
    }

    @Override
    public KImage fillTriangle(Coordinate c1, Coordinate c2, Coordinate c3, Color color) {
        var points = Util.calculateTrianglePoints(c1, c2, c3);
        points.forEach(c -> drawPixel(c, color));
        return this;
    }

    public DrawableImage drawString(int x, int y, String str, Color color, KFont font, int fontSize, boolean antiAliasing, FontStyle... styles) {
        font.drawString(this, x, y, fontSize, color, str, antiAliasing, styles);
        return this;
    }


}
