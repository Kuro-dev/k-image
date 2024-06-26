package org.kurodev.kimage.kimage.font.glyph;

import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.Drawable;
import org.kurodev.kimage.kimage.font.KFont;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;
import org.kurodev.kimage.kimage.util.Util;

import java.awt.*;
import java.util.List;

@FunctionalInterface
public interface FontStyle {
    FontStyle UNDERLINE = (x, y, scale, glyph, img, font, color) -> {
        int advanceWidth = (int) (glyph.getAdvanceWidth() * scale);
        int adnvanceWidthFraction = (int) (advanceWidth * 0.1);
        List<Coordinate> points = Util.calculateLinePoints(x - adnvanceWidthFraction, y + 2, x + advanceWidth + adnvanceWidthFraction, y + 2);
        points.parallelStream().forEach(coordinate -> img.drawPixel(coordinate, color));
    };
    FontStyle DOUBLE_UNDERLINE = (x, y, scale, glyph, img, font, color) -> {
        UNDERLINE.apply(x, y, scale, glyph, img, font, color);
        UNDERLINE.apply(x, y + 2, scale, glyph, img, font, color);
    };


    void apply(int x, int y, double scale, FontGlyph glyph, Drawable drawable, KFont font, Color color);
}
