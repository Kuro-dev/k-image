package org.kurodev.kimage.kimage.font;

import org.kurodev.kimage.kimage.font.glyph.FontGlyph;

import java.io.IOException;
import java.io.InputStream;

public interface KFont {

    static KFont getFont() {
        return FontReaders.getDefaultFontReader();
    }

    static KFont getFont(InputStream in) throws IOException {
        return FontReaders.loadFont(in);
    }

    int getLowestRecommendedPPEM();

    FontGlyph getGlyph(char character);

    /**
     * Calculates the number of pixels per em for a given font size in points.
     *
     * @param fontSizeInPoints the font size in points
     * @return the number of pixels per em
     */
    default double calculatePixelsPerEm(int fontSizeInPoints) {
        // Assume standard screen resolution of 96 DPI
        double dpi = 96.0;
        // There are 72 points per inch
        double pointsPerInch = 72.0;
        // Calculate pixels per em
        return fontSizeInPoints * (dpi / pointsPerInch);
    }
}
