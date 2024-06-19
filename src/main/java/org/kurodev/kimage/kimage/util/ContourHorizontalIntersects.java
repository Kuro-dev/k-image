package org.kurodev.kimage.kimage.util;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ContourHorizontalIntersects {
    record Slice(double lowY, double highY) {}
    public record Segment(Coord a, Coord b) {
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
            return a.y < slice.highY && slice.lowY < b.y;
        }

        double xIntersect(double y) {
            if (a.y == b.y) {
                return a.x;
            } else {
                return (y - a.y) * (b.x - a.x) / (b.y - a.y) + a.x;
            }
        }
    }
    public record Coord(double x, double y) {}

    public record HorizontalIntersects(double y, double[] xs) {}

    public static Iterator<HorizontalIntersects> horizontalIntersects(List<Segment> segments) {
        // Find vertical slices, that is: group the coords 2 by 2

        // For each slice, find the relevant segment crossing the slice

        final Iterator<Slice> slices;
        {
            var sortedCoords = Stream.concat(
                    segments.stream().map(Segment::a),
                    segments.stream().map(Segment::b)
            ).sorted(Comparator.comparing(Coord::y).thenComparing(Coord::x)).toList();
            slices = IntStream.range(1, sortedCoords.size()).mapToObj(
                    i -> new Slice(sortedCoords.get(i - 1).y, sortedCoords.get(i).y)
            ).distinct().iterator();
        }

        return new Iterator<>() {
            List<Segment> crossingSegments = null;
            int yMax, yMin;
            int cursor = -1;

            @Override
            public boolean hasNext() {
                if(cursor == -1) {
                    assert crossingSegments == null;
                    return slices.hasNext();
                } else {
                    assert crossingSegments != null;
                    assert cursor + yMin <= yMax;
                    assert cursor >= 0;
                    return true;
                }
            }

            @Override
            public HorizontalIntersects next() {
                if(!hasNext()) throw new NoSuchElementException();

                if(cursor == -1) {
                    var slice = slices.next();
                    crossingSegments = segments.stream()
                            .filter(segment -> segment.crosses(slice))
                            .toList();
                    {
                        /*
                        { // first segment goes from end to 0
                            var segment = new Segment(coords.getLast(), coords.getFirst());
                            if (segment.crosses(slice)) {
                                crossingSegments.add(segment);
                            }
                        }
                        for (int i = 1; i < coords.size(); i++) {
                            var segment = new Segment(coords.get(i - 1), coords.get(i));
                            if (segment.crosses(slice)) {
                                crossingSegments.add(segment);
                            }
                        }

                         */
                    }

                    yMax = (int) slice.highY;
                    yMin = (int) slice.lowY;
                    cursor = 0;
                    assert yMin <= yMax;
                }

                var y = cursor + yMin;
                try {
                    double[] xs = new double[crossingSegments.size()];
                    for(int i = 0; i < xs.length; i++) {
                        var segment = crossingSegments.get(i);
                        xs[i] = segment.xIntersect(y);
                    }
                    Arrays.sort(xs);
                    //assert xs.length % 2 == 0;
                    return new HorizontalIntersects(y, xs);
                } finally {
                    cursor++;
                    if(yMin + cursor > yMax) {
                        cursor = -1;
                        crossingSegments = null;
                    }
                }
            }
        };
    }

}
