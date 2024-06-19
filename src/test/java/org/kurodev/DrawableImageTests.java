package org.kurodev;

import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.draw.DrawableImage;
import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.KFont;
import org.kurodev.kimage.kimage.util.ContourHorizontalIntersects;
import org.kurodev.kimage.kimage.font.glyph.Coordinate;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DrawableImageTests {

    @Test
    public void testIfImageCanBeSerialisedAndDeserialised() {
        DrawableImage img = new DrawableImage(1920, 1080);
        img.fillRect(300, 300, 400, 550, Color.BLACK);
        img.drawRect(350, 350, 300, 450, Color.WHITE);
        img.drawCircle(400, 400, 25, Color.WHITE);

        DrawableImage img2 = DrawableImage.ofBytes(img.encode());
        assertArrayEquals(img.encode(), img2.encode());
    }

    @Test
    public void testIfImageMetaDataIsPersisted() {
        DrawableImage img = new DrawableImage(1920, 1080);
        img.fillRect(300, 300, 400, 550, Color.BLACK);
        img.addCustomChunk("TEST", "THIS IS A TEST");
        DrawableImage img2 = DrawableImage.ofBytes(img.encode());
        assertEquals("THIS IS A TEST", new String(img.getChunk("TEST")));
        assertArrayEquals(img.getChunk("TEST"), img2.getChunk("TEST"));
    }

    @Test
    public void drawString() throws IOException {
        KImage img = new DrawableImage(1000, 300);
        img.fill(Color.WHITE);
        KFont font = KFont.getFont(Files.newInputStream(Path.of("./testfonts/Catways.ttf")));
        String str = """
                The quick brown fox jumps over the lazy dog.
                This is one singular big string with Different characters-
                and sizes! ###
                Maybe this will work, maybe it wont! :D
                Who knows, I don't!
                """;
        img.drawString(10, 50, str, Color.BLACK, font, 50);
        Files.write(Path.of("./test.png"), img.encode());
    }


    @Test
    public void contourTest() {

        var segments = List.of(
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(2.0, 0.0),
                        new ContourHorizontalIntersects.Coord(0.0, 6.0)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(0.0, 6.0),
                        new ContourHorizontalIntersects.Coord(8.0, 6.0)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(8.0, 6.0),
                        new ContourHorizontalIntersects.Coord(8.0, 0.0)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(8.0, 0.0),
                        new ContourHorizontalIntersects.Coord(4.0, 2.0)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(4.0, 2.0),
                        new ContourHorizontalIntersects.Coord(2.0, 4.0)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(2.0, 4.0),
                        new ContourHorizontalIntersects.Coord(2.0, 0.0)
                )
        );

        var it = ContourHorizontalIntersects.horizontalIntersects(segments);

        while (it.hasNext()) {
            var intersect = it.next();
            System.out.printf("Intersecting (%s, %s)\n", intersect.y(), Arrays.toString(intersect.xs()));
        }
    }

    public void drawTest() throws IOException {
        KImage img = new DrawableImage(200, 100);
        img.fill(Color.WHITE);
        Coordinate start = new Coordinate(120, 30);
        Coordinate end = new Coordinate(120, 90);
        Coordinate curve = new Coordinate(190, 60);
        img.fillBezierCurve(start, end, curve, Color.BLACK);

        start = new Coordinate(60, 20);
        end = new Coordinate(110, 90);
        curve = new Coordinate(30, 45);
        img.fillBezierCurve(start, end, curve, Color.BLUE);
        Files.write(Path.of("./test.png"), img.encode());
    }

    @Test
    public void getColorTest() {
        KImage img = new DrawableImage(100, 100);
        img.fill(Color.WHITE);
        img.drawPixel(50, 50, Color.GREEN);
        img.drawPixel(50, 51, new Color(30, 50, 60, 70));
        assertEquals(Color.GREEN, img.getColor(50, 50));
        assertEquals(new Color(30, 50, 60, 70), img.getColor(50, 51));
    }
}
