package org.kurodev.kimage.kimage.font.glyph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
    private final int xMin, yMin, xMax, yMax;

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
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.xCoordinates = xCoordinates;
        this.yCoordinates = yCoordinates;
        this.flags = flags;
        this.instructions = instructions;
        this.endPtsOfContours = endPtsOfContours;
        this.advanceWidth = advanceWidth;
    }

    public int getNumberOfContours() {
        return numberOfContours;
    }

    public int getxMin() {
        return xMin;
    }

    public int getyMin() {
        return yMin;
    }

    public int getxMax() {
        return xMax;
    }

    public int getyMax() {
        return yMax;
    }

    public int[] getxCoordinates() {
        return xCoordinates;
    }

    public int[] getyCoordinates() {
        return yCoordinates;
    }

    public byte[] getFlags() {
        return flags;
    }

    public int[] getInstructions() {
        return instructions;
    }

    public int[] getEndPtsOfContours() {
        return endPtsOfContours;
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
    public List<Coordinate> getCoordinates() {
        List<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < xCoordinates.length; i++) {
            List<GlyphFlag> glyphFlags = GlyphFlag.identify(flags[i]);
            coordinates.add(new Coordinate(xCoordinates[i], yCoordinates[i], glyphFlags));
        }
        return coordinates;
    }
}