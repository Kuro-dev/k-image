package org.kurodev;

import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.draw.DrawableImage;
import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.KFont;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        assertEquals(img.getPng(), img2.getPng());
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
        KImage img = new DrawableImage(1500, 800);
        img.fill(Color.WHITE);
        KFont font = KFont.getFont();
        double scale = 0.1;
        img.drawString(0, 10, "ABCDEFGHIJ", Color.BLACK, font, scale);
        img.drawString(0, 210, "KLMNOPQRS", Color.BLACK, font, scale);
        img.drawString(0, 410, "TUVWXYZ", Color.BLACK, font, scale);
        img.drawString(0, 610, "!\"$%&/()=", Color.BLACK, font, scale);
//        img.drawBezierCurve(new Coordinate(0, 800), new Coordinate(1500, 0), new Coordinate(1500, 800), Color.BLACK, 100);
        Files.write(Path.of("./test.png"), img.encode());
    }
}
