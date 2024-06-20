package org.kurodev.kimage.kimage.util;

import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.glyph.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContourHorizontalIntersects {
    private static final Logger logger = LoggerFactory.getLogger(ContourHorizontalIntersects.class);

    public static List<Coordinate> horizontalIntersects(List<Coordinate> shape, KImage img) {
        assert shape != null : "shape is null";
        assert !shape.isEmpty() : "Shape must not be empty";
        int yMax = shape.stream().mapToInt(Coordinate::y).max().getAsInt();
        int yMin = shape.stream().mapToInt(Coordinate::y).min().getAsInt();
        int xMax = shape.stream().mapToInt(Coordinate::x).max().getAsInt();
        int xMin = shape.stream().mapToInt(Coordinate::x).min().getAsInt();
        List<Coordinate> result = new ArrayList<>();
        for (int y = yMin - 1; y < yMax + 1; y++) {
            int intersections = 0;
            boolean justIntersected = false;
            for (int x = xMin; x < xMax; x++) {
                img.drawPixel(x,y, Color.LIGHT_GRAY);
                Coordinate c = new Coordinate(x, y);
                if (shape.contains(c) && !justIntersected) {
                    intersections++;
                    justIntersected = true;
                } else if ((intersections & 1) == 1) {
                    result.add(c);
                    justIntersected = false;
                }

            }
            System.out.println("intersections: " + intersections);
        }
        return result;
    }

}