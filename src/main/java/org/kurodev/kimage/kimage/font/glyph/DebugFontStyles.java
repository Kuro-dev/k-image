package org.kurodev.kimage.kimage.font.glyph;

import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;
import org.kurodev.kimage.kimage.util.Util;

import java.awt.*;
import java.util.List;

public class DebugFontStyles {
    /**
     * Draw the edge points of the outline
     *
     * @implNote Does *not* draw the entire outline
     */
    public static FontStyle debugDrawEdges(Color color) {
        return (x, y, scale, glyph, img, font, _ignored) -> {
            Coordinate[][] outline = glyph.getCoordinates();
            for (Coordinate[] contour : outline) {
                for (Coordinate p : contour) {
                    img.drawPixel((int) (Math.floor(x + p.x() * scale)), (int) (Math.round(y + p.y() * scale)), color);
                }
            }
        };
    }

    /**
     * Draws the actual computed bounding box of the glyph.
     * optional parameter "color" to make differentiating easier
     */
    public static FontStyle debugDrawBoundingBox() {
        return debugDrawBoundingBox(null);
    }

    public static FontStyle debugDrawBoundingBox(Color color) {
        return (x, y, scale, glyph, img, font, fallBack) -> {
            Coordinate[][] coordinates = glyph.getCoordinates();
            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
            if (coordinates.length == 0) {
                return;
            }
            for (Coordinate[] contour : coordinates) {
                for (Coordinate point : contour) {
                    double px = point.x() * scale;
                    double py = point.y() * scale;
                    if (px < minX) minX = px;
                    if (px > maxX) maxX = px;
                    if (py < minY) minY = py;
                    if (py > maxY) maxY = py;
                }
            }

            int rectX = (int) Math.floor(x + minX);
            int rectY = (int) Math.floor(y + minY);
            int rectWidth = (int) Math.ceil(maxX - minX);
            int rectHeight = (int) Math.ceil(maxY - minY);
            Color drawColor = color == null ? fallBack : color;
            Util.calculateRectanglePoints(rectX - 1, rectY - 1, rectWidth + 2, rectHeight + 2)
                    .forEach(c -> img.drawPixel(c, drawColor));
        };
    }
}
