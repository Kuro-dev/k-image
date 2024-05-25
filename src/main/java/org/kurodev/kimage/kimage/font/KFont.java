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

    FontGlyph getGlyph(char character);
}
