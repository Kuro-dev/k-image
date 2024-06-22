package org.kurodev.kimage.kimage.font.glyph;

import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.KFont;

import java.awt.*;

@FunctionalInterface
public interface FontStyle {
    FontStyle UNDERLINE = (x, y, scale, glyph, img, font, color) -> {
        int advanceWidth = (int) (glyph.getAdvanceWidth() * scale);
        int adnvanceWidthFraction = (int) (advanceWidth * 0.1);
        img.drawLine(x - adnvanceWidthFraction, y + 2, x + advanceWidth + adnvanceWidthFraction, y + 2, color);
    };
    FontStyle DOUBLE_UNDERLINE = (x, y, scale, glyph, img, font, color) -> {
        UNDERLINE.apply(x, y, scale, glyph, img, font, color);
        UNDERLINE.apply(x, y + 2, scale, glyph, img, font, color);
    };


    void apply(int x, int y, double scale, FontGlyph glyph, KImage img, KFont font, Color color);
}
