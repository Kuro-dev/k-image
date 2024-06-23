package org.kurodev.kimage.kimage.font;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.draw.DrawableImage;
import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Disabled
public class FileWritingTests {
    private static KFont jetbrainsMono;
    private static KFont catWays;

    @BeforeAll
    public static void setUp() throws IOException {
        jetbrainsMono = KFont.getFont(Files.newInputStream(Path.of("./testfonts/JetBrainsMono-Regular.ttf")));
        catWays = KFont.getFont(Files.newInputStream(Path.of("./testfonts/Catways.ttf")));

    }

    @Test
    public void jbMonoSingleLetter() throws IOException {
        KImage img = new DrawableImage(100, 60);
        img.fill(Color.WHITE);
        String str = ".";
        img.drawString(50, 55, str, Color.BLACK, jetbrainsMono, 50);
        Files.write(Path.of("./test.png"), img.encode());
    }

    @Test
    public void jbMonoSentence() throws IOException {
        KImage img = new DrawableImage(1000, 300);
        img.fill(Color.WHITE);
        String str = """
                The quick brown föx jumps över the lazy dögs.
                Waltz, nymph, for quick jigs vex Mr. Blynx's päls.
                Victor jagt zwölf Boxkämpfer quer über den großen Sylter Deich.
                $/+-*/"&@#<>123456789 :)
                """;
        img.drawString(10, 50, str, Color.BLACK, jetbrainsMono, 40);
        Files.write(Path.of("./test.png"), img.encode());
    }

    @Test
    public void catwaysSentence() throws IOException {
        KImage img = new DrawableImage(1000, 300);
        img.fill(Color.WHITE);
        String str = """
                ACBEDGFIHKJMLONQPSRUTWVYXZ
                abcdefghijklmnopqrstuvwxyz
                0123456789
                $/+-*/"&@#<>
                """;
        img.drawString(10, 50, str, Color.BLACK, catWays, 50);
        Files.write(Path.of("./test.png"), img.encode());
    }

    @Test
    public void catwaysSingleLetter() throws IOException {
        KImage img = new DrawableImage(100, 60);
        img.fill(Color.WHITE);
        String str = "h";
        img.drawString(10, 55, str, Color.BLACK, catWays, 50);
        Files.write(Path.of("./test.png"), img.encode());
    }

    @Test
    public void drawShapesTest() throws IOException {
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
}
