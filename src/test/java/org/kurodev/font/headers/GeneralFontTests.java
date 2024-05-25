package org.kurodev.font.headers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.font.FontReader;
import org.kurodev.kimage.kimage.font.FontReaders;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kurodev.font.headers.Helper.createPointArray;

public class GeneralFontTests {

    private static final LetterEntry[] letters = {
            new LetterEntry(' ', 3, 320),
            new LetterEntry('A', 4, 448),
            new LetterEntry('B', 5, 448),
            new LetterEntry('C', 6, 448),
            new LetterEntry('D', 7, 448),
            new LetterEntry('E', 8, 448),
            new LetterEntry('F', 9, 448),
            new LetterEntry('G', 10, 448),
            new LetterEntry('H', 11, 448),
            new LetterEntry('I', 12, 448),
            new LetterEntry('J', 13, 448),
            new LetterEntry('K', 14, 512),
            new LetterEntry('L', 15, 448),
            new LetterEntry('M', 16, 448),
            new LetterEntry('N', 17, 832),
            new LetterEntry('O', 18, 512),
            new LetterEntry('P', 19, 448),
            new LetterEntry('Q', 20, 576),
            new LetterEntry('R', 21, 448),
            new LetterEntry('S', 22, 448),
            new LetterEntry('T', 23, 448),
            new LetterEntry('U', 24, 448),
            new LetterEntry('V', 25, 448),
            new LetterEntry('W', 26, 768),
            new LetterEntry('X', 27, 448),
            new LetterEntry('Y', 28, 448),
            new LetterEntry('Z', 29, 448),
    };

    private static final  Point[] pointsForLetterA = createPointArray();
    private static FontReader font;

    @BeforeAll
    public static void prepare() throws IOException {
        font = (FontReader) FontReaders.loadFont(FontReaders.class.getResourceAsStream("/kimage/Pixelletters.ttf"));
    }

    @Test
    public void testAdvanceWidth() {
        //find correct index for each character in the font
        for (LetterEntry letter : letters) {
            int index = font.getGlyphIndex(letter.character);
            assertEquals(letter.index, index, "Index is wrong: " + letter);
        }
        //Check if advancewidth for every letter is as expected
        for (LetterEntry letter : letters) {
            int index = font.getGlyphIndex(letter.character);
            assertEquals(letter.advanceWidth, font.getAdvanceWidth(index), "Advancewidth is wrong " + letter);
        }
    }

    @Test
    public void testBasicFontData() {
        var glyph = font.getGlyph('A');
        var coordinates = glyph.getCoordinates();
        assertEquals(pointsForLetterA.length, coordinates.size(), "read coordinates differ in size");
        for (int i = 0; i < coordinates.size(); i++) {
            var actual = coordinates.get(i);
            var expected = pointsForLetterA[i];
            assertEquals(expected.x, actual.x(), "X coordinate for " + actual + " does not match. expected: " + expected);
            assertEquals(expected.y, actual.y(), "y coordinate for " + actual + " does not match. expected: " + expected);
        }
    }

    public record Point(int x, int y) {
    }

    private record LetterEntry(char character, int index, int advanceWidth) {
    }
}
