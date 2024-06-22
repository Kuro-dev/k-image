package org.kurodev.kimage.kimage.util;

import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordinateTransformationTests {
    @Test
    void testTranslate() {
        Coordinate coord = new Coordinate(10, 20);
        Coordinate translated = coord.transform(Transformation.TRANSLATE, 5, 7);
        assertEquals(new Coordinate(15, 27), translated);

        translated = coord.transform(Transformation.TRANSLATE, -10, -20);
        assertEquals(new Coordinate(0, 0), translated);

        translated = coord.transform(Transformation.TRANSLATE, 0, 0);
        assertEquals(coord, translated);
    }

    @Test
    void testScale() {
        Coordinate coord = new Coordinate(10, 20);
        Coordinate scaled = coord.transform(Transformation.SCALE, 2, 2);
        assertEquals(new Coordinate(20, 40), scaled);

        scaled = coord.transform(Transformation.SCALE, -1, 1);
        assertEquals(new Coordinate(-10, 20), scaled);

        scaled = coord.transform(Transformation.SCALE, 1, -1);
        assertEquals(new Coordinate(10, -20), scaled);

        scaled = coord.transform(Transformation.SCALE, 1.5, -1);
        assertEquals(new Coordinate(15, -20), scaled);
    }

    @Test
    void testRotate() {
        Coordinate coord = new Coordinate(10, 0);
        Coordinate rotated = coord.transform(Transformation.ROTATE, 90);
        assertEquals(new Coordinate(0, 10), rotated);

        rotated = coord.transform(Transformation.ROTATE, 180);
        assertEquals(new Coordinate(-10, 0), rotated);

        rotated = coord.transform(Transformation.ROTATE, 270);
        assertEquals(new Coordinate(0, -10), rotated);
    }

    @Test
    void testShear() {
        Coordinate coord = new Coordinate(10, 20);
        Coordinate sheared = coord.transform(Transformation.SHEAR, 1, 0);
        assertEquals(new Coordinate(30, 20), sheared);

        sheared = coord.transform(Transformation.SHEAR, 0, 1);
        assertEquals(new Coordinate(10, 30), sheared);

        sheared = coord.transform(Transformation.SHEAR, 1, 1);
        assertEquals(new Coordinate(30, 30), sheared);
    }
}
