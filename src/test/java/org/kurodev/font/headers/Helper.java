package org.kurodev.font.headers;

/**
 * generated using ChatGPT with data coordinate extracted using FontDrop.
 * All the points for the letter "A"
 */
public class Helper {
    public static GeneralFontTests.Point[] createPointArray() {
        int[][] coordinates = {
                {320, 0}, {320, 64}, {320, 128}, {320, 192}, {320, 256}, {320, 320},
                {256, 320}, {192, 320}, {128, 320}, {64, 320}, {64, 256}, {64, 192},
                {64, 128}, {64, 64}, {64, 0}, {0, 0}, {0, 64}, {0, 128}, {0, 192},
                {0, 256}, {0, 320}, {0, 384}, {0, 448}, {0, 512}, {0, 576}, {0, 640},
                {0, 704}, {64, 704}, {64, 640}, {64, 576}, {64, 512}, {64, 448},
                {64, 384}, {128, 384}, {192, 384}, {256, 384}, {320, 384}, {320, 448},
                {320, 512}, {320, 576}, {320, 640}, {320, 704}, {384, 704}, {384, 640},
                {384, 576}, {384, 512}, {384, 448}, {384, 384}, {384, 320}, {384, 256},
                {384, 192}, {384, 128}, {384, 64}, {384, 0}, {320, 704}, {256, 704},
                {192, 704}, {128, 704}, {64, 704}, {64, 768}, {128, 768}, {192, 768},
                {256, 768}, {320, 768}
        };

        GeneralFontTests.Point[] points = new GeneralFontTests.Point[coordinates.length];
        int prevX = 0;
        int prevY = 0;

        for (int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0] - prevX;
            int y = coordinates[i][1] - prevY;
            points[i] = new GeneralFontTests.Point(x, y);
            prevX = coordinates[i][0];
            prevY = coordinates[i][1];
        }

        return points;
    }
}
