package org.kurodev.kimage.kimage.util;


import org.kurodev.kimage.kimage.font.Drawable;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Integer.min;
import static java.lang.Math.max;
import static java.util.Collections.unmodifiableList;

public abstract class ContourHorizontalIntersects {
    abstract public void drawPixels(Drawable image, int x, int y, Color color, boolean antiAliasing);


    record Slice(int lowY, int highY) {
    }

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
                return (int)Math.round((y - a.y()) * (b.x() - a.x()) / (double)(b.y() - a.y()) + a.x());
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
        public void drawPixels(Drawable image, int x, int y, Color color) {
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
        for (var segment : allSegments) {
            if (segment.crosses(slice)) {
                segments.add(segment);
            }
        }
        return unmodifiableList(segments);
    }

    static int[] horizontalIntersections(double y, Iterable<Segment> segments) {
        var intersects = new ArrayList<Integer>();
        for (var segment : segments) {
            if (segment.strictlyCrossesHorizontal(y)) {
                intersects.add(segment.xIntersect(y));
            }
        }
        var xs = intersects.stream().mapToInt(Integer::valueOf).sorted().toArray();
        assert xs.length % 2 == 0 :
                "Y = %f has %s intersects".formatted(y, Arrays.toString(xs));
        return xs;
    }

    static List<ContourHorizontalIntersects.Segment> segmentsOfContours(Coordinate[][] contours) {
        var segments = new ArrayList<ContourHorizontalIntersects.Segment>();

        for (var contour : contours) {
            if (contour.length > 0) {

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

    static Stream<HorizontalSegment> horizontalBoundaryIntersects(int y, List<Segment> segments) {
        return segments.stream().filter(
                segment -> segment.a().y() == y && segment.b().y() == y
        ).map(
                segment -> {
                    var xStart = min(segment.a().x(), segment.b().x());
                    var xEnd = max(segment.a().x(), segment.b().x());
                    return new HorizontalSegment(xStart, xEnd, y);
                }
        );
    }

    static Stream<HorizontalSegment> horizontalIntersects(List<Segment> segments) {
        return slices(segments).flatMap(slice -> {
            var crossingSegments = crossingSegments(slice, segments);
            return IntStream.rangeClosed(slice.lowY, slice.highY).boxed().flatMap(y -> {
                var effectiveY = y == slice.lowY ? (y + 0.001) : (y == slice.highY) ? (y - 0.001) : y;
                var xs = horizontalIntersections(effectiveY, crossingSegments);

                var base = IntStream.range(0, xs.length / 2).mapToObj(
                        i -> new HorizontalSegment(xs[2 * i], xs[2 * i + 1], y)
                );
                if(y == slice.lowY || y == slice.highY) {
                    return Stream.concat(
                            base,
                            horizontalBoundaryIntersects(y, segments)
                    );
                } else {
                    return base;
                }
            });
        });
    }

    static ContourHorizontalIntersects touchMatrix(List<HorizontalSegment> segments) {
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
            for(var x = segment.xStart; x <= segment.xEnd; x++) {
                var i = x - xMin;
                matrix[j][i] = true;
            }
        }

        return new ContourHorizontalIntersects() {
            Color workingColor;

            float getEntryAsFloat(int j, int i) {
                return j >= 0
                        && j < matrix.length
                        && i >= 0
                        && i < matrix[j].length
                        && matrix[j][i]
                        ? 1.0f : 0.0f;
            }

            Color getColorOfScore(float score) {
                int alpha = 255-min(255, max(0, Math.round(score)));
                return new Color(
                        workingColor.getRed(),
                        workingColor.getGreen(),
                        workingColor.getBlue(),
                        alpha
                );
            }

            @Override
            public void drawPixels(Drawable image, int x, int y, Color color, boolean antiAliasing) {
                if(antiAliasing) {
                    this.workingColor = color;
                    var cache = new HashMap<Float, Color>();

                    for (int j = 0; j < matrix.length; j++) {
                        for (int i = 0; i < matrix[j].length; i++) {
                            float score = 128 * getEntryAsFloat(j, i) + 127 * (
                                    getEntryAsFloat(j - 1, i - 1) + getEntryAsFloat(j, i - 1) + getEntryAsFloat(j + 1, i + 1)
                                            + getEntryAsFloat(j - 1, i) + getEntryAsFloat(j + 1, i)
                                            + getEntryAsFloat(j - 1, i + 1) + getEntryAsFloat(j, i + 1) + getEntryAsFloat(j + 1, i + 1)
                            ) / 8.0f;
                            if(score > 1.0f) {
                                var effectiveColor = cache.computeIfAbsent(score, this::getColorOfScore);

                                image.drawPixel(i + xMin + x, j + yMin + y, effectiveColor);
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < matrix.length; j++) {
                        for (int i = 0; i < matrix[j].length; i++) {
                            if(matrix[j][i]) {
                                image.drawPixel(i + xMin + x, j + yMin + y, color);
                            }
                        }
                    }
                }
            }
        };
    }

    /* Public API */

    public static ContourHorizontalIntersects makeFromContour(Coordinate[][] contours) {
        if (contours == null || contours.length == 0) {
            return new ContourHorizontalIntersects() {
                @Override
                public void drawPixels(Drawable image, int x, int y, Color color, boolean antiAliasing) {
                }
            };
        } else {
            var segments = horizontalIntersects(segmentsOfContours(contours)).toList();
            var matrix = touchMatrix(segments);
            return new ContourHorizontalIntersects() {
                @Override
                public void drawPixels(Drawable image, int x, int y, Color color, boolean antiAliasing) {
                    matrix.drawPixels(image, x, y, color, antiAliasing);
                }
            };
        }
    }

}
