package org.kurodev.kimage.kimage.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import java.util.List;


public class ContourHorizontalIntersectsTest {
    @Test
    @Disabled
    public void contourTest() {

        var segments = List.of(
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

        var it = ContourHorizontalIntersects.horizontalIntersects(segments).iterator();

        while (it.hasNext()) {
            var intersect = it.next();
            System.out.printf("Intersecting y=%d, segment=[%d, %d]\n", intersect.y(), intersect.xStart(), intersect.xEnd());
        }
    }
}
