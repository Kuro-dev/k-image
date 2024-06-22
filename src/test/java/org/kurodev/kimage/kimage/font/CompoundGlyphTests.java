package org.kurodev.kimage.kimage.font;

import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.font.glyph.FontGlyph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompoundGlyphTests {
    @Test
    public void testCompoundGlyph() throws IOException {
        FontReader font = (FontReader) KFont.getFont(Files.newInputStream(Path.of("./testfonts/JetBrainsMono-Regular.ttf")));
        assertEquals(1, font.getGlyphIndex('A'), "Incorrect glyph index");
        FontGlyph glyph = font.getGlyph('A');
        assertEquals(600, glyph.getAdvanceWidth());
        System.out.println(glyph);
    }
}
