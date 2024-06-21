package org.kurodev.kimage.kimage.util;

import org.junit.jupiter.api.Test;

import java.util.List;


public class ContourHorizontalIntersectsTest {
    @Test
    public void contourTest() {

        var segments = List.of(
                /* This is a non convex polygon, with different edge cases */
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(2, 0),
                        new ContourHorizontalIntersects.Coord(0, 6)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(0, 6),
                        new ContourHorizontalIntersects.Coord(8, 6)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(8, 6),
                        new ContourHorizontalIntersects.Coord(8, 0)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(8, 0),
                        new ContourHorizontalIntersects.Coord(4, 2)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(4, 2),
                        new ContourHorizontalIntersects.Coord(2, 4)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(2, 4),
                        new ContourHorizontalIntersects.Coord(2, 0)
                ),

                /* This starts a square inside the polygon */
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(6, 3),
                        new ContourHorizontalIntersects.Coord(6, 5)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(6, 5),
                        new ContourHorizontalIntersects.Coord(7, 5)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(7, 5),
                        new ContourHorizontalIntersects.Coord(7, 3)
                ),
                new ContourHorizontalIntersects.Segment(
                        new ContourHorizontalIntersects.Coord(7, 3),
                        new ContourHorizontalIntersects.Coord(6, 3)
                )
        );

        var it = ContourHorizontalIntersects.horizontalIntersects(segments).iterator();

        while (it.hasNext()) {
            var intersect = it.next();
            System.out.printf("Intersecting y=%d, segment=[%d, %d]\n", intersect.y(), intersect.xStart(), intersect.xEnd());
        }
    }
}
