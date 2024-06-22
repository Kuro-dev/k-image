package org.kurodev.kimage.kimage.font.glyph.simple;

import org.kurodev.kimage.kimage.font.glyph.FontGlyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFontGlyph implements FontGlyph {
    private static final Logger logger = LoggerFactory.getLogger(SimpleFontGlyph.class);
    /**
     * The character this glyph represents
     */
    private final char character;

    /**
     * int16
     */
    private final int numberOfContours;
    /**
     * int16
     */
    private final int xMin;
    private final int yMin;
    private final int xMax;
    private final int yMax;
    /**
     * uint8 || uint16
     */
    private final int[] xCoordinates, yCoordinates;
    private final byte[] flags;
    /**
     * uint16
     */
    private final int[] instructions;
    /**
     * uint16
     */
    private final int[] endPtsOfContours;
    private final int advanceWidth;

    public SimpleFontGlyph(char character, int numberOfContours, int xMin, int yMin, int xMax, int yMax, int[] xCoordinates, int[] yCoordinates, byte[] flags, int[] instructions, int[] endPtsOfContours, int advanceWidth) {
        this.character = character;
        this.numberOfContours = numberOfContours;
        //swapping ymin and ymax because of the inversion happening below the hood.
        this.xMin = xMin;
        this.yMin = -yMax;
        this.xMax = xMax;
        this.yMax = yMin;
        this.xCoordinates = xCoordinates;
        this.yCoordinates = yCoordinates;
        this.flags = flags;
        this.instructions = instructions;
        this.endPtsOfContours = endPtsOfContours;
        this.advanceWidth = advanceWidth;
    }

    @Override
    public int getYMax() {
        //this is necessary to ensure the glyph is drawn upright.
        return yMin;
    }

    @Override
    public int getXMax() {
        return xMax;
    }

    @Override
    public int getYMin() {
        //this is necessary to ensure the glyph is drawn upright.
        return yMax *-1;
    }

    @Override
    public int getXMin() {
        return xMin;
    }

    public int[] getxCoordinates() {
        return xCoordinates;
    }

    public int[] getyCoordinates() {
        return yCoordinates;
    }

    /**
     * int maxX = Arrays.stream(contour).map(Coordinate::x).max(Integer::compareTo).get();
     * int maxY = Arrays.stream(contour).map(Coordinate::y).max(Integer::compareTo).get();
     * int minX = Arrays.stream(contour).map(Coordinate::x).min(Integer::compareTo).get();
     * int minY = Arrays.stream(contour).map(Coordinate::y).min(Integer::compareTo).get();
     *
     * @return
     */
    public int getNumberOfContours() {
        return numberOfContours;
    }

    @Override
    public String toString() {
        return "SimpleFontGlyph{" +
                "character=" + character +
                ", numberOfContours=" + numberOfContours +
                ", advanceWidth=" + advanceWidth +
                '}';
    }


    @Override
    public int getAdvanceWidth() {
        return advanceWidth;
    }

    @Override
    public char getCharacter() {
        return character;
    }

    @Override
    public Coordinate[][] getCoordinates() {
        int currentX = 0;
        int currentY = 0;

        Coordinate[][] coordinates = new Coordinate[numberOfContours][];

        int pointIndex = 0;

        for (int contourIndex = 0; contourIndex < numberOfContours; contourIndex++) {
            int contourSize = (contourIndex == 0) ? endPtsOfContours[contourIndex] + 1
                    : endPtsOfContours[contourIndex] - endPtsOfContours[contourIndex - 1];

            Coordinate[] contour = new Coordinate[contourSize];

            for (int i = 0; i < contourSize; i++) {
                currentX += xCoordinates[pointIndex];
                currentY += yCoordinates[pointIndex] * -1; //inverting Y to make the glyph draw upwards instead of downwards.

                contour[i] = new Coordinate(currentX, currentY);

                pointIndex++;
            }

            coordinates[contourIndex] = contour;
        }

        return coordinates;
    }
}
