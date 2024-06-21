package org.kurodev.kimage.kimage.util;

import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.glyph.FontGlyph;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

@FunctionalInterface
public interface ContourHorizontalIntersects {
    void drawPixels(KImage image, int x, int y, Color color);


    record Slice(int lowY, int highY) {}

    record Segment(Coord a, Coord b) {
        public Segment(Coord a, Coord b) {
            if (a.y < b.y) {
                this.a = a;
                this.b = b;
            } else {
                this.a = b;
                this.b = a;
            }
        }

        boolean crosses(Slice slice) {
            return a.y <= slice.highY && slice.lowY <= b.y;
        }

        boolean strictlyCrossesHorizontal(double y) {
            return a.y < y && y < b.y;
        }

        int xIntersect(double y) {
            if (a.y == b.y) {
                return a.x;
            } else {
                return (int)((y - a.y) * (b.x - a.x) / (double)(b.y - a.y) + a.x);
            }
        }
    }

    public record HorizontalSegment(int xStart, int xEnd, int y) {
        public void drawPixels(KImage image, int x, int y, Color color) {
            for(int i = xStart; i <= xEnd; i++) {
                image.drawPixel(i + x, y + this.y, color);
            }
        }
    }

    public record Coord(int x, int y) {}

    static Stream<Slice> slices(List<Segment> segments) {
        var sortedCoords = segments.stream().flatMap(
                segment -> Stream.of(segment.a, segment.b)
        ).sorted(Comparator.comparing(Coord::y)).mapToInt(Coord::y).distinct().toArray();

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


    static ContourHorizontalIntersects makeForGlyph(FontGlyph glyph, double scale) {
        if(glyph.getNumberOfContours() == 0) {
            return (image, x, y, color) -> {};
        } else {
            var segments = horizontalIntersects(segmentsOfGlyph(glyph, scale)).toList();
            return (image, x, y, color) -> {
                for(var segment: segments) {
                    segment.drawPixels(image, x, y, color);
                }
            };
        }
    }

}
