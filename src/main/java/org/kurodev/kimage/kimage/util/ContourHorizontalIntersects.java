package org.kurodev.kimage.kimage.util;

import java.util.*;
import java.util.stream.DoubleStream;
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
            return a.y <= slice.highY && slice.lowY <= b.y;
        }

        boolean strictlyCrossesHorizontal(double y) {
            return a.y < y && y < b.y;
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
            var sortedCoords = segments.stream().flatMap(
                    segment -> Stream.of(segment.a, segment.b)
            ).sorted(Comparator.comparing(Coord::y).thenComparing(Coord::x)).distinct().toList();
            var fixedSlices = IntStream.range(1, sortedCoords.size()).mapToObj(
                    i -> new Slice(sortedCoords.get(i - 1).y, sortedCoords.get(i).y)
            ).filter(
                    slice -> slice.highY > slice.lowY
            ).distinct().toList();
            slices = fixedSlices.iterator();
        }

        return new Iterator<>() {
            List<Integer> crossingSegmentIndices = null;
            int yMax, yMin;
            int cursor = -1;

            @Override
            public boolean hasNext() {
                if(cursor == -1) {
                    assert crossingSegmentIndices == null;
                    return slices.hasNext();
                } else {
                    assert crossingSegmentIndices != null;
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
                    crossingSegmentIndices = new ArrayList<>();
                    {
                        var it = segments.iterator();
                        var index = 0;
                        while(it.hasNext()) {
                            var segment = it.next();
                            if(segment.crosses(slice)) {
                                crossingSegmentIndices.add(index);
                            }
                            index++;
                        }
                    }

                    yMax = (int) slice.highY;
                    yMin = (int) slice.lowY;
                    cursor = 0;
                    assert yMin <= yMax;
                }

                double y = cursor + yMin;
                try {
                    ArrayList<Double> intersects = new ArrayList<>();
                    var it = crossingSegmentIndices.iterator();
                    if(y == yMin || y == yMax) {
                        /* TODO: The current segment crosses the boundary on a point.
                            The goal is to decide whether or not we should count that point
                            as an intersection point, or just skip it.
                            This depends on the angle the polygon is making w.r.t. the horizontal line.
                            There are some edge cases to consider, whether or not the segment is horizontal
                            itself.
                            It is not clear to me whether or not the cases yMin and yMax can be treated
                            in one shot.
                         */
                        var workAroundY = y + (yMin == y ? 1 : -1) * Math.min(1, (yMax-yMin)) * 0.001;
                        while (it.hasNext()) {
                            int index = it.next();
                            var segment = segments.get(index);
                            if (segment.strictlyCrossesHorizontal(workAroundY)) {
                                intersects.add(segment.xIntersect(workAroundY));
                            }
                        }
                    } else {
                        while(it.hasNext()) {
                            int index = it.next();
                            var segment = segments.get(index);
                            if(segment.strictlyCrossesHorizontal(y)) {
                                intersects.add(segment.xIntersect(y));
                            }
                        }
                    }

                    var xs = intersects.stream().mapToDouble(Double::valueOf).toArray();
                    Arrays.sort(xs);
                    //assert xs.length % 2 == 0;
                    return new HorizontalIntersects(y, xs);
                } finally {
                    cursor++;
                    if(yMin + cursor >= yMax) {
                        cursor = -1;
                        crossingSegmentIndices = null;
                    }
                }
            }
        };
    }

}
