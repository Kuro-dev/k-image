package org.kurodev.kimage.kimage.font;

import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.font.glyph.FontGlyph;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kurodev.kimage.kimage.font.Helper.coordinatesForLetterA;

public class GeneralFontTests {

    @Test
    public void testBasicFontData() throws IOException {
        KFont font = FontReaders.loadFont(FontReaders.class.getResourceAsStream("/kimage/Pixellettersfull.ttf"));
        var glyph = font.getGlyph('A');
        var coordinates = glyph.getCoordinates();
        assertEquals(coordinatesForLetterA.length, coordinates.length, "read coordinates differ in size");
        for (int contourIndex = 0; contourIndex < coordinatesForLetterA.length; contourIndex++) {
            Coordinate[] expectedContour = coordinatesForLetterA[contourIndex];
            Coordinate[] actualContour = coordinates[contourIndex];
            assertEquals(expectedContour.length, actualContour.length, "read contour differs in size in contour " + contourIndex);
            for (int coordinate = 0; coordinate < expectedContour.length; coordinate++) {
                Coordinate expected = expectedContour[coordinate];
                Coordinate actual = actualContour[coordinate];
                actual.flags().clear(); //the expected values don't have flags, we care strictly about coordinates right now.
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    public void advanceWidthVerification() throws IOException {
        KFont font = KFont.getFont(Files.newInputStream(Path.of("./testfonts/Catways.ttf")));
        FontGlyph glyph = font.getGlyph(' ');
        assertEquals(250, glyph.getAdvanceWidth(), "Advancewidth should be 250");
    }

    public record Point(int x, int y) {
    }

    private record LetterEntry(char character, int index, int advanceWidth) {
    }
}
