package org.kurodev.kimage.kimage.font.glyph;

import java.nio.ByteBuffer;

public class GlyphFactory {

    public static FontGlyph getGlyph() {
        return null;
    }

    public static SimpleFontGlyph readSimpleGlyph(ByteBuffer glyf, short numberOfContours, int glyphOffset, char character, int advanceWidth) {
        short xMin = glyf.getShort();
        short yMin = glyf.getShort();
        short xMax = glyf.getShort();
        short yMax = glyf.getShort();

        int[] endPtsOfContours = new int[numberOfContours];
        for (int i = 0; i < numberOfContours; i++) {
            endPtsOfContours[i] = glyf.getShort() & 0xFFFF;
        }

        int instructionLength = glyf.getShort() & 0xFFFF;
        byte[] instructions = new byte[instructionLength];
        glyf.get(instructions);

        int numPoints = (numberOfContours == 0) ? 0 : (endPtsOfContours[numberOfContours - 1] + 1);
        byte[] flags = new byte[numPoints];
        int flagCount = 0;
        while (flagCount < numPoints) {
            byte flag = glyf.get();
            flags[flagCount++] = flag;
            if ((flag & 8) != 0) {
                int repeatCount = glyf.get() & 0xFF;
                for (int i = 0; i < repeatCount; i++) {
                    flags[flagCount++] = flag;
                }
            }
        }

        int[] xCoordinates = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            if ((flags[i] & 2) != 0) {
                int value = glyf.get() & 0xFF;
                xCoordinates[i] = (flags[i] & 16) != 0 ? value : -value;
            } else if ((flags[i] & 16) == 0) {
                xCoordinates[i] = glyf.getShort();
            }
        }

        int[] yCoordinates = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            if ((flags[i] & 4) != 0) {
                int value = glyf.get() & 0xFF;
                yCoordinates[i] = (flags[i] & 32) != 0 ? value : -value;
            } else if ((flags[i] & 32) == 0) {
                yCoordinates[i] = glyf.getShort();
            }
        }

        return new SimpleFontGlyphBuilder()
                .setNumberOfContours(numberOfContours)
                .setxMin(xMin)
                .setyMin(yMin)
                .setxMax(xMax)
                .setyMax(yMax)
                .setEndPtsOfContours(endPtsOfContours)
                .setxCoordinates(xCoordinates)
                .setyCoordinates(yCoordinates)
                .setFlags(flags)
                .setInstructions(yCoordinates)
                .setCharacter(character)
                .setAdvanceWidth(advanceWidth)
                .createSimpleFontGlyph();
    }
}
