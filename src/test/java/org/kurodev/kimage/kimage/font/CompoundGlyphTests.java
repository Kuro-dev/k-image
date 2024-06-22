package org.kurodev.kimage.kimage.font;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kurodev.kimage.kimage.draw.DrawableImage;
import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.glyph.compound.CompoundGlyph;
import org.kurodev.kimage.kimage.font.glyph.compound.CompoundGlyphFlag;
import org.kurodev.kimage.kimage.font.glyph.compound.GlyphWithFlags;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompoundGlyphTests {

    private static FontReader font;

    @BeforeAll
    public static void setUp() throws IOException {
        font = (FontReader) KFont.getFont(Files.newInputStream(Path.of("./testfonts/JetBrainsMono-Regular.ttf")));
        font.getFontFlags().add(FontFlag.DEBUG_DRAW_BOUNDING_BOX);
    }

    @ParameterizedTest
    @CsvSource({
            "A, 1",
            "Á, 2",
            "B, 26",
            "C, 27",
            "D, 33",
            "Ä, 16",
            "|, 1470"
    })
    public void testCompoundGlyph(char character, int index) {
        assertEquals(index, font.getGlyphIndex(character), "Incorrect glyph index for character '" + character + "'");
    }

    @Test
    public void testCompoundGlyphFlags() throws IOException {
        CompoundGlyph glyph = (CompoundGlyph) font.getGlyph('Ä');

        assertEquals(600, glyph.getAdvanceWidth());
        assertEquals('Ä', glyph.getCharacter());
        assertEquals(2, glyph.getComponents().size());
        //test data for the first compound
        GlyphWithFlags glyph1 = glyph.getComponents().getFirst();
        assertEquals('A', glyph1.glyph().getCharacter());
        //offsets should be 0,0
        assertEquals(0, glyph1.flags().params()[0]);
        assertEquals(0, glyph1.flags().params()[1]);
        //check correct flags
        List<CompoundGlyphFlag> compound1Flags = CompoundGlyphFlag.identify(0x0226);
        assertEquals(compound1Flags, glyph1.flags().flags());

        //test data for the second compound
        GlyphWithFlags glyph2 = glyph.getComponents().get(1);
        assertEquals('A', glyph1.glyph().getCharacter());
        //offsets should be 600, 0
        assertEquals(600, glyph2.flags().params()[0]);
        assertEquals(0, glyph2.flags().params()[1]);
        //check correct flags
        List<CompoundGlyphFlag> compound2Flags = CompoundGlyphFlag.identify(0x7);
        assertEquals(compound2Flags, glyph.getComponents().get(1).flags().flags());
    }

    @Test
    public void simpleTest() throws IOException {
        KImage img = new DrawableImage(100, 60);
        img.fill(Color.WHITE);
        String str = ".";
        img.drawString(50, 55, str, Color.BLACK, font, 50);
        Files.write(Path.of("./test.png"), img.encode());
    }

    @Test
    public void drawString() throws IOException {
        KImage img = new DrawableImage(1000, 300);
        img.fill(Color.WHITE);
        String str = """
                ÄÖÜiiiIII
                abcdefghijklmnopqrstuvwxyz
                0123456789
                $/+-*/"&@#<>
                """;
        img.drawString(10, 50, str, Color.BLACK, font, 50);
        Files.write(Path.of("./test.png"), img.encode());
    }
}
