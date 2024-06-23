package org.kurodev.kimage.kimage;

import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.draw.DrawableImage;
import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.KFont;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

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
    public void getColorTest() {
        KImage img = new DrawableImage(100, 100);
        img.fill(Color.WHITE);
        img.drawPixel(50, 50, Color.GREEN);
        img.drawPixel(50, 51, new Color(30, 50, 60, 70));
        assertEquals(Color.GREEN, img.getColor(50, 50));
        assertEquals(new Color(30, 50, 60, 70), img.getColor(50, 51));
    }
}
