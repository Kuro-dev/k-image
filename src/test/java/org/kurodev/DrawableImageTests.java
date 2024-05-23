package org.kurodev;

import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.draw.DrawableImage;

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
        DrawableImage img = new DrawableImage(800, 800);
        for (int i = 0; i < 2000; i++) {
            img.fillRect(0, 0, 800, 800, Color.WHITE);
            img.drawString(0, 0, "B", Color.BLACK);
        }

        Files.write(Path.of("./test.png"), img.encode());
    }
}
