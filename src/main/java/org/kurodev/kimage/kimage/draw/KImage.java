package org.kurodev.kimage.kimage.draw;

import org.kurodev.kimage.kimage.font.Drawable;
import org.kurodev.kimage.kimage.font.FontReaders;
import org.kurodev.kimage.kimage.font.KFont;
import org.kurodev.kimage.kimage.font.glyph.FontStyle;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import java.awt.*;

public interface KImage extends Drawable {
    static KImage empty(int width, int height) {
        return new DrawableImage(width, height);
    }

    static KImage ofBytes(byte[] data) {
        return DrawableImage.ofBytes(data);
    }

    default KImage drawString(int x, int y, String str) {
        return drawString(x, y, str, 32);
    }

    default KImage drawString(int x, int y, String str, int fontSize) {
        return drawString(x, y, str, Color.BLACK, FontReaders.getDefaultFontReader(), fontSize);
    }

    default KImage drawString(int x, int y, String str, Color color, int fontSize) {
        return drawString(x, y, str, color, FontReaders.getDefaultFontReader(), fontSize);
    }

    KImage drawString(int x, int y, String str, Color color, KFont font, int fontSize, FontStyle... styles);

    KImage drawPixel(int x, int y, Color color);

    default Drawable drawPixel(Coordinate point, Color color) {
        return drawPixel(point.x(), point.y(), color);
    }

    KImage drawLine(int x1, int y1, int x2, int y2, Color color);

    default KImage drawLine(int x1, int y1, int x2, int y2, Color color, int thickness) {
        for (int i = 0; i < thickness; i++) {
            drawLine(x1 + i, y1, x2 + i, y2, color);
            drawLine(x1, y1 + i, x2, y2 + i, color);
        }
        return this;
    }

    default KImage drawLine(Coordinate a, Coordinate b, Color color) {
        return drawLine(a.x(), a.y(), b.x(), b.y(), color);
    }

    /**
     * Draws an outline of a rectangle
     *
     * @param x     Anchor point X of the rectangle
     * @param y     Anchor point Y of the rectangle
     * @param dx    Width of the rectangle
     * @param dy    Height of the rectangle
     * @param color Color of the rectangle
     * @return This object
     */
    KImage drawRect(int x, int y, int dx, int dy, Color color);

    /**
     * Draws a solid rectangle
     *
     * @param x     Anchor point X of the rectangle
     * @param y     Anchor point Y of the rectangle
     * @param dx    Width of the rectangle
     * @param dy    Height of the rectangle
     * @param color Color of the rectangle
     * @return This object
     */
    KImage fillRect(int x, int y, int dx, int dy, Color color);

    KImage fillCircle(int centerX, int centerY, int radius, Color color);

    KImage drawCircle(int centerX, int centerY, int radius, Color color);

    /**
     * Encodes the image to PNG format
     *
     * @return a byte array representing the entire image file
     */
    byte[] encode();

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

    /**
     * Draws a Bézier curve based on three control points.
     *
     * @param start marks the start of the curve
     * @param end   End point, the curve ends here :)
     * @param curve This is the curve itself, the point that "drags" the line between start and end towards itself
     * @param color The color of the curve.
     * @param steps The number of steps to use for drawing the curve (higher values result in smoother curves).
     */
    KImage drawBezierCurve(Coordinate start, Coordinate end, Coordinate curve, Color color, int steps);

    KImage fillBezierCurve(Coordinate start, Coordinate end, Coordinate curve, Color color, int steps);

    default KImage drawBezierCurve(Coordinate start, Coordinate end, Coordinate curve, Color color) {
        return drawBezierCurve(start, end, curve, color, 5000);
    }

    default KImage fillBezierCurve(Coordinate start, Coordinate end, Coordinate curve, Color color) {
        return fillBezierCurve(start, end, curve, color, 5000);
    }

    /**
     * @param x   The anchor point (top left corner)
     * @param y   The anchor point (top left corner)
     * @param img the image to draw.
     */
    KImage draw(int x, int y, KImage img);

    /**
     * Resizes the image to the given width and height
     *
     * @param width  new width
     * @param height new height
     * @return
     */
    KImage resize(int width, int height);

    /**
     * Resizes the image by the given scale with the results being (floored)
     */
    KImage resize(double scale);

    int getWidth();

    int getHeight();

    /**
     * @param x x coordinate of the target
     * @param y y coordinate of the target
     * @return an int[4] with RGBA values
     */
    Color getColor(int x, int y);

    default Color getColor(Coordinate point) {
        return getColor(point.x(), point.y());
    }

    KImage fillTriangle(Coordinate c1, Coordinate c2, Coordinate c3, Color color);
}
