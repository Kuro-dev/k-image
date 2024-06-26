package org.kurodev.kimage.kimage.util;

import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import java.util.*;


public class Util {
    private static final double KILOBYTE = 1024;
    private static final double MEGABYTE = 1024 * 1024;
    private static final double GIGABYTE = 1024 * 1024 * 1024;

    /**
     * Turns bytes into a nicer String representing size.
     * <p>convertBytes(500);         // 500B</p>
     * <p>convertBytes(2048);        // 2.00KB</p>
     * <p>convertBytes(500000);      // 488.28KB</p>
     * <p>convertBytes(10485760);    // 10.00MB</p>
     * <p>convertBytes(1073741824);  // 1.00GB</p>
     */
    public static String bytesToString(int bytes) {
        if (bytes < KILOBYTE) {
            return bytes + "B";
        } else if (bytes < MEGABYTE) {
            return String.format("%.2fKB", bytes / KILOBYTE);
        } else if (bytes < GIGABYTE) {
            return String.format("%.2fMB", bytes / (MEGABYTE));
        } else {
            return String.format("%.2fGB", bytes / (GIGABYTE));
        }
    }

    public static Set<Coordinate> calculateBezierCurve(Coordinate start, Coordinate end, Coordinate curve, int steps) {
        Set<Coordinate> points = new HashSet<>();
        double stepSize = 1.0 / steps;

        Coordinate prevPoint = start;
        points.add(prevPoint);

        for (int i = 1; i <= steps; i++) {
            double t = stepSize * i;
            double x = Math.pow(1 - t, 2) * start.x() + 2 * t * (1 - t) * curve.x() + Math.pow(t, 2) * end.x();
            double y = Math.pow(1 - t, 2) * start.y() + 2 * t * (1 - t) * curve.y() + Math.pow(t, 2) * end.y();
            Coordinate currentPoint = new Coordinate((int) Math.round(x), (int) Math.round(y));

            points.addAll(calculateLinePoints(prevPoint, currentPoint));
            prevPoint = currentPoint;
        }

        return points;
    }

    /**
     * scan from max Y to min Y and find all the points where the different X values intersect with the outline of the boundary.
     * Fill them in.
     *
     * @return An updated set of Coordinates (in a new set object).
     * @implNote does not mutate the original set.
     */
    public static Set<Coordinate> fillBezierCurve(Set<Coordinate> boundaryPoints) {
        Set<Coordinate> filledPoints = new HashSet<>();
        int minY = boundaryPoints.stream().mapToInt(Coordinate::y).min().orElseThrow();
        int maxY = boundaryPoints.stream().mapToInt(Coordinate::y).max().orElseThrow();

        for (int y = minY; y <= maxY; y++) {
            Set<Integer> intersections = new HashSet<>();
            for (Coordinate point : boundaryPoints) {
                if (point.y() == y) {
                    intersections.add(point.x());
                }
            }

            if (intersections.isEmpty()) continue;

            int minX = intersections.stream().mapToInt(Integer::intValue).min().orElseThrow();
            int maxX = intersections.stream().mapToInt(Integer::intValue).max().orElseThrow();

            for (int x = minX; x <= maxX; x++) {
                filledPoints.add(new Coordinate(x, y));
            }
        }

        return filledPoints;
    }

    public static Collection<Coordinate> calculateLinePoints(Coordinate c1, Coordinate c2) {
        if (c1.equals(c2)) return Collections.emptyList();
        return calculateLinePoints(c1.x(), c1.y(), c2.x(), c2.y());
    }

    public static List<Coordinate> calculateLinePoints(int x1, int y1, int x2, int y2) {
        List<Coordinate> points = new ArrayList<>();
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (x1 != x2 || y1 != y2) {
            points.add(new Coordinate(x1, y1));
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
        return points;
    }

    public static List<Coordinate> calculateTrianglePoints(Coordinate a, Coordinate b, Coordinate c) {
        List<Coordinate> edgePoints = new ArrayList<>();

        // Calculate line points for each edge of the triangle
        edgePoints.addAll(calculateLinePoints(a, b));
        edgePoints.addAll(calculateLinePoints(b, c));
        edgePoints.addAll(calculateLinePoints(c, a));

        // Use a set to avoid duplicates and convert back to list for sorting
        List<Coordinate> points = new ArrayList<>(edgePoints);

        points.sort((p1, p2) -> {
            if (p1.y() == p2.y()) {
                return Integer.compare(p1.x(), p2.x());
            }
            return Integer.compare(p1.y(), p2.y());
        });

        List<Coordinate> filledPoints = new ArrayList<>();
        int currentY = points.getFirst().y();
        List<Integer> currentXPoints = new ArrayList<>();

        for (Coordinate point : points) {
            if (point.y() != currentY) {
                // Fill the horizontal line
                Collections.sort(currentXPoints);
                for (int x = currentXPoints.getFirst(); x <= currentXPoints.getLast(); x++) {
                    filledPoints.add(new Coordinate(x, currentY));
                }
                currentXPoints.clear();
                currentY = point.y();
            }
            currentXPoints.add(point.x());
        }

        // Fill the last horizontal line
        Collections.sort(currentXPoints);
        for (int x = currentXPoints.getFirst(); x <= currentXPoints.getLast(); x++) {
            filledPoints.add(new Coordinate(x, currentY));
        }

        return filledPoints;
    }

    public static boolean isPointInBezierCurve(Coordinate point, Coordinate start, Coordinate end, Coordinate curve, int steps) {
        // Check if the point is within the bounding box of the Bézier curve
        int minX = Math.min(start.x(), Math.min(end.x(), curve.x()));
        int maxX = Math.max(start.x(), Math.max(end.x(), curve.x()));
        int minY = Math.min(start.y(), Math.min(end.y(), curve.y()));
        int maxY = Math.max(start.y(), Math.max(end.y(), curve.y()));

        if (point.x() < minX || point.x() > maxX || point.y() < minY || point.y() > maxY) {
            return false;
        }

        // For a given x, calculate the corresponding y value on the Bézier curve
        double tStep = 1.0 / steps;
        for (double t = 0; t <= 1; t += tStep) {
            double x = Math.pow(1 - t, 2) * start.x() + 2 * t * (1 - t) * curve.x() + Math.pow(t, 2) * end.x();
            if (Math.abs(point.x() - x) < 0.5) { // Check if the x value matches the point's x within a small margin
                double y = Math.pow(1 - t, 2) * start.y() + 2 * t * (1 - t) * curve.y() + Math.pow(t, 2) * end.y();
                // Check if the point is below the curve if it curves upwards or above if it curves downwards
                if (start.y() <= end.y() ? point.y() <= y : point.y() >= y) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Coordinate calculateCenterPoint(Coordinate... points) {
        if (points.length == 0) throw new IllegalArgumentException("points must not be empty");
        int x = 0;
        int y = 0;
        for (Coordinate point : points) {
            x += point.x();
            y += point.y();
        }
        return new Coordinate(x / points.length, y / points.length);
    }

    public static boolean isPointInsideContour(int x, int y, Coordinate[] contour, double scale) {
        int intersections = 0;
        int numPoints = contour.length;

        for (int i = 0; i < numPoints; i++) {
            Coordinate p1 = contour[i];
            Coordinate p2 = contour[(i + 1) % numPoints]; //next coordinate or wrap around to index 0

            if (doesRayIntersectEdge(x, y, p1, p2, scale)) {
                intersections++;
            }
        }
        //odd intersections means the point is inside the shape.
        return (intersections & 1) == 1;
    }

    private static boolean doesRayIntersectEdge(int px, int py, Coordinate p1, Coordinate p2, double scale) {
        //multiply values by the scale factor for accurate measurements.
        int x1 = (int) (p1.x() * scale);
        int y1 = (int) (p1.y() * scale);
        int x2 = (int) (p2.x() * scale);
        int y2 = (int) (p2.y() * scale);

        // Ensure y1 <= y2
        if (y1 > y2) {
            int tempX = x1, tempY = y1;
            x1 = x2;
            y1 = y2;
            x2 = tempX;
            y2 = tempY;
        }

        if (py == y1 || py == y2) {
            py += 1;
        }

        if (py < y1 || py > y2) {
            return false;
        }

        // Check if the point is to the right of the edge
        if (px >= Math.max(x1, x2)) {
            return false;
        }

        // Calculate intersection of the ray with the edge
        if (px < Math.min(x1, x2)) {
            return true;
        }

        // Calculate intersection x-coordinate
        double xIntersection = (double) (px - x1) / (x2 - x1) * (y2 - y1) + y1;
        return py < xIntersection;
    }

    public static List<Coordinate> calculateRectanglePoints(int x, int y, int dx, int dy) {
        List<Coordinate> out = new ArrayList<>();
        out.addAll(calculateLinePoints(x, y, x + dx, y));                       //horizontal top line
        out.addAll(calculateLinePoints(x, y + dy, x + dx, y + dy));     //horizontal bottom line
        out.addAll(calculateLinePoints(x, y, x, y + dy));                       //vertical left line
        out.addAll(calculateLinePoints(x + dx, y, x + dx, y + dy + 1)); //vertical right line
        return out;
    }
}
