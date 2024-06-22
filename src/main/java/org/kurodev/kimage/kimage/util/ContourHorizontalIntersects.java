package org.kurodev.kimage.kimage.util;

import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Integer.min;
import static java.lang.Math.max;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public abstract class ContourHorizontalIntersects {
    abstract public void drawPixels(KImage image, int x, int y, Color color);


    record Slice(int lowY, int highY) {}

    record Segment(Coordinate a, Coordinate b) {
        public Segment(Coordinate a, Coordinate b) {
            if (a.y() < b.y()) {
                this.a = a;
                this.b = b;
            } else {
                this.a = b;
                this.b = a;
            }
        }

        boolean crosses(Slice slice) {
            return a.y() <= slice.highY && slice.lowY <= b.y();
        }

        boolean strictlyCrossesHorizontal(double y) {
            return a.y() < y && y < b.y();
        }

        int xIntersect(double y) {
            if (a.y() == b.y()) {
                return a.x();
            } else {
                return (int)((y - a.y()) * (b.x() - a.x()) / (double)(b.y() - a.y()) + a.x());
            }
        }
    }

    record HorizontalSegment(int xStart, int xEnd, int y) {
        HorizontalSegment(int xStart, int xEnd, int y) {
            this.y = y;
            if(xStart < xEnd) {
                this.xStart = xStart;
                this.xEnd = xEnd;
            } else {
                this.xStart = xEnd;
                this.xEnd = xStart;
            }
        }
        public void drawPixels(KImage image, int x, int y, Color color) {
            for(int i = xStart; i <= xEnd; i++) {
                image.drawPixel(i + x, y + this.y, color);
            }
        }
    }

    static Stream<Slice> slices(List<Segment> segments) {
        var sortedCoords = segments.stream().flatMap(
                segment -> Stream.of(segment.a, segment.b)
        ).sorted(Comparator.comparing(Coordinate::y)).mapToInt(Coordinate::y).distinct().toArray();

        return IntStream.range(1, sortedCoords.length).mapToObj(
                i -> new Slice(sortedCoords[i - 1], sortedCoords[i])
        );
    }

    static List<Segment> crossingSegments(Slice slice, Iterable<Segment> allSegments) {
        var segments = new ArrayList<Segment>();
        for(var segment: allSegments) {
            if(segment.crosses(slice)) {
                segments.add(segment);
            }
        }
        return unmodifiableList(segments);
    }

    static int[] horizontalIntersections(double y, Iterable<Segment> segments) {
        var intersects = new ArrayList<Integer>();
        for(var segment: segments) {
            if (segment.strictlyCrossesHorizontal(y)) {
                intersects.add(segment.xIntersect(y));
            }
        }
        var xs = intersects.stream().mapToInt(Integer::valueOf).sorted().toArray();
        assert xs.length % 2 == 0:
                "Y = %f has %s intersects".formatted(y, Arrays.toString(xs));
        return xs;
    }

    private static double addmu(double a, double b, double c) {
        return a + (b*c);
    }

    /* TODO: This should be removed.
        I kept it because it shows a light variation in the font size
        Remove freely when we're sure we won't need it anymore.

    static List<ContourHorizontalIntersects.Segment> segmentsOfGlyph(FontGlyph glyph, double scale) {
        var endPts = glyph.getEndPtsOfContours();

        var startPt = 0;

        var currentX = 0.0;
        var currentY = addmu(0.0, glyph.getyMax(), scale);

        var segments = new ArrayList<ContourHorizontalIntersects.Segment>();

        for (var pt : endPts) {
            var endPt = pt + 1;

            currentX = addmu(currentX, glyph.getX(startPt), scale);
            currentY = addmu(currentY, glyph.getY(startPt), -scale);

            var firstCoord = new ContourHorizontalIntersects.Coord((int) currentX, (int) currentY);
            var currentCoord = firstCoord;

            for (int i = startPt + 1; i < endPt; i++) {
                var nextX = addmu(currentX, glyph.getX(i), scale);
                var nextY = addmu(currentY, glyph.getY(i), -scale);

                var nextCoord = new ContourHorizontalIntersects.Coord((int) nextX, (int) nextY);
                segments.add(new ContourHorizontalIntersects.Segment(currentCoord, nextCoord));

                currentCoord = nextCoord;
                currentX = nextX;
                currentY = nextY;
            }

            segments.add(new ContourHorizontalIntersects.Segment(currentCoord, firstCoord));

            startPt = endPt;
        }

        return unmodifiableList(segments);
    }
     */

    static List<ContourHorizontalIntersects.Segment> segmentsOfContours(Coordinate[][] contours) {
        var segments = new ArrayList<ContourHorizontalIntersects.Segment>();

        for (var contour : contours) {
            if(contour.length > 0) {

                var firstCoord = new Coordinate(
                        contour[0].x(), contour[0].y()
                );
                var currentCoord = firstCoord;

                for (int i = 1; i < contour.length; i++) {
                    var nextCoord = new Coordinate(
                            contour[i].x(), contour[i].y()
                    );
                    segments.add(new ContourHorizontalIntersects.Segment(currentCoord, nextCoord));
                    currentCoord = nextCoord;
                }

                segments.add(new ContourHorizontalIntersects.Segment(currentCoord, firstCoord));
            }
        }

        return unmodifiableList(segments);
    }

    static Stream<HorizontalSegment> horizontalIntersects(List<Segment> segments) {
        return slices(segments).flatMap(slice -> {
            var crossingSegments = crossingSegments(slice, segments);
            return IntStream.rangeClosed(slice.lowY, slice.highY).boxed().flatMap(y -> {
                double effectiveY = y;
                if (y == slice.lowY || y == slice.highY) {
                    /* TODO: This is a workaround. There should be a way to do better. */
                    effectiveY += (y == slice.lowY ? 1 : -1) * 0.001;
                }
                var xs = horizontalIntersections(effectiveY, crossingSegments);

                return IntStream.range(0, xs.length / 2).mapToObj(
                        i -> new HorizontalSegment(xs[2*i], xs[2*i+1], y)
                );
            });
        });
    }

    static boolean[][] touchMatrix(List<HorizontalSegment> segments) {
        final int yMin, yMax, xMin, xMax;
        {
            int minY = Integer.MAX_VALUE,
                    maxY = Integer.MIN_VALUE,
                    minX = Integer.MAX_VALUE,
                    maxX = Integer.MIN_VALUE;

            for(var segment: segments) {
                minY = Math.min(minY, segment.y());
                maxY = max(maxY, segment.y());
                minX = Math.min(minX, segment.xStart());
                maxX = max(maxX, segment.xEnd());
            }

            yMin = minY;
            yMax = maxY;
            xMin = minX;
            xMax = maxX;
        }

        var matrix = new boolean[yMax - yMin + 1][xMax - xMin + 1];

        for(var segment: segments) {
            var j = segment.y() - yMin;
            for(var i = 0; i < matrix[0].length; i++) {
                var k = i + segment.xStart() - xMin;
                matrix[j][k] = true;
            }
        }

        return matrix;
    }

    /* Public API */

    public static ContourHorizontalIntersects makeFromContour(Coordinate[][] contours) {
        if(contours == null || contours.length == 0) {
            return new ContourHorizontalIntersects() {
                @Override
                public void drawPixels(KImage image, int x, int y, Color color) {}
            };
        } else {
            var segments = horizontalIntersects(segmentsOfContours(contours)).toList();
            //var matrix = touchMatrix(segments);
            return new ContourHorizontalIntersects() {
                @Override
                public void drawPixels(KImage image, int x, int y, Color color) {
                    for(var segment: segments) {
                        segment.drawPixels(image, x, y, color);
                    }
                }
            };
        }
    }

}
