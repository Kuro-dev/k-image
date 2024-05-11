package org.kurodev;

import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.font.FontReader;
import org.kurodev.kimage.kimage.font.FontReaders;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FontReaderTests {

    FontReader reader = FontReaders.getDefaultFontReader();

    @Test
    public void advanceWidth() {
        var glyph = reader.getGlyph('B');
        System.out.println(glyph);
        assertEquals(36, glyph.getIndex());
        assertEquals(0, glyph.getxMin());
        assertEquals(0, glyph.getyMin());
        assertEquals(448, glyph.getxMax());
        assertEquals(512, glyph.getyMax());
        assertEquals(6, glyph.getNumberOfContours());
        assertEquals(512, glyph.getAdvanceWidth());
    }
}
