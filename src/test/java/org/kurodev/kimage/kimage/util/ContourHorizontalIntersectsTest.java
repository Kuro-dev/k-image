package org.kurodev.kimage.kimage.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;
import org.kurodev.kimage.kimage.draw.DrawableImage;
import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.KFont;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class ContourHorizontalIntersectsTest {
    private List<ContourHorizontalIntersects.Segment> segments1() {
        return List.of(
                /* This is a non convex polygon, with different edge cases */
                new ContourHorizontalIntersects.Segment(
                        new Coordinate(2, 0),
                        new Coordinate(0, 6)
                ),
                new ContourHorizontalIntersects.Segment(
                        new Coordinate(0, 6),
                        new Coordinate(8, 6)
                ),
                new ContourHorizontalIntersects.Segment(
                        new Coordinate(8, 6),
                        new Coordinate(8, 0)
                ),
                new ContourHorizontalIntersects.Segment(
                        new Coordinate(8, 0),
                        new Coordinate(4, 2)
                ),
                new ContourHorizontalIntersects.Segment(
                        new Coordinate(4, 2),
                        new Coordinate(2, 4)
                ),
                new ContourHorizontalIntersects.Segment(
                        new Coordinate(2, 4),
                        new Coordinate(2, 0)
                ),

                /* This starts a square inside the polygon */
                new ContourHorizontalIntersects.Segment(
                        new Coordinate(6, 3),
                        new Coordinate(6, 5)
                ),
                new ContourHorizontalIntersects.Segment(
                        new Coordinate(6, 5),
                        new Coordinate(7, 5)
                ),
                new ContourHorizontalIntersects.Segment(
                        new Coordinate(7, 5),
                        new Coordinate(7, 3)
                ),
                new ContourHorizontalIntersects.Segment(
                        new Coordinate(7, 3),
                        new Coordinate(6, 3)
                )
        );
    }

    @Test
    public void contourTest() {
        var segments = segments1();

        var it = ContourHorizontalIntersects.horizontalIntersects(segments).iterator();

        while (it.hasNext()) {
            var intersect = it.next();
            System.out.printf("Intersecting y=%d, segment=[%d, %d]\n", intersect.y(), intersect.xStart(), intersect.xEnd());
        }
    }

    @Test
    @Disabled
    public void matrixTest() {
        var segments = segments1();

        var hSegments = ContourHorizontalIntersects.horizontalIntersects(segments).toList();
        var matrix = ContourHorizontalIntersects.touchMatrix(hSegments);

        assert matrix != null;
    }

    @Test
    public void simpleTest() throws IOException {
        KImage img = new DrawableImage(400, 200);
        img.fill(Color.RED);
        String str = "How is life today?";
        var font = KFont.getFont(Files.newInputStream(Path.of("./testfonts/JetBrainsMono-Regular.ttf")));
        img.drawString(30, 40, str, Color.BLACK, font, 50);
        img.drawString(30, 80, str, Color.BLACK, font, 50, false);
        Files.write(Path.of("./test.png"), img.encode());
    }
}
