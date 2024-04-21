package org.kurodev.kimage.kimage.font;

import org.kurodev.kimage.kimage.font.enums.GlyphFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

public class SimpleFontGlyph {
    private static final Logger logger = LoggerFactory.getLogger(SimpleFontGlyph.class);
    private short numberOfContours;
    private short xMin, yMin, xMax, yMax;
    private int[] endPtsOfContours;
    private int instructionLength;
    private byte[] instructions;
    private byte[] flags;
    private Coordinate[] coordinates;
    private int advanceWidth;
    private int index;

    /**
     * Reads glyph data from a byte buffer
     */
    public static SimpleFontGlyph readGlyph(ByteBuffer buffer, short numberOfContours) {
        if (numberOfContours < 0) {
            throw new IllegalStateException("Negative number of contours indicates composite glyph");
        }
        SimpleFontGlyph glyph = new SimpleFontGlyph();
        glyph.numberOfContours = numberOfContours;

        // Check if there's enough data to read the basic glyph header
        if (buffer.remaining() < 10) {
            throw new IllegalStateException("Not enough data to read glyph header");
        }

        glyph.xMin = buffer.getShort();
        glyph.yMin = buffer.getShort();
        glyph.xMax = buffer.getShort();
        glyph.yMax = buffer.getShort();

        // Handling of zero contours (glyphs without outlines, such as space)
        if (numberOfContours == 0) {
            logger.info("Glyph with 0 contours found, likely a space or similar glyph.");
            return glyph; // Early return as there's no further data to process for such glyphs
        } else {
            glyph.endPtsOfContours = new int[numberOfContours];
            int totalPoints = 0;

            for (int i = 0; i < numberOfContours; i++) {
                glyph.endPtsOfContours[i] = buffer.getShort() & 0xFFFF;
                totalPoints = Math.max(totalPoints, glyph.endPtsOfContours[i]);
            }

            totalPoints += 1; // Total number of points is the last endpoint index + 1

            // Ensure there's enough buffer left for instructions and points data
            if (buffer.remaining() < 2) {
                throw new IllegalStateException("Not enough data to read instruction length");
            }

            glyph.instructionLength = buffer.getShort() & 0xFFFF;
            if (buffer.remaining() < glyph.instructionLength + totalPoints) {
                throw new IllegalStateException("Not enough data to read instructions and points");
            }

            glyph.instructions = new byte[glyph.instructionLength];
            buffer.get(glyph.instructions);
            glyph.flags = readFlags(buffer, totalPoints);
            glyph.coordinates = readCoordinates(buffer, glyph.flags);
        }
        return glyph;
    }

    /**
     * Reads the flags for each point in the glyph.
     *
     * @param buffer    ByteBuffer from which to read the data.
     * @param numPoints The number of points (derived from the last end point of contours).
     * @return Array of bytes representing the flags.
     */
    private static byte[] readFlags(ByteBuffer buffer, int numPoints) {
        byte[] flags = new byte[numPoints];
        for (int i = 0; i < numPoints; i++) {
            flags[i] = buffer.get();
            // Check for repeat flag using GlyphFlags
            if (GlyphFlag.REPEAT_FLAG.check(flags[i])) {
                byte repeat = buffer.get();
                for (int j = 0; j < repeat && (i + j + 1) < numPoints; j++) {
                    flags[i + j + 1] = flags[i];
                }
                i += repeat;
            }
        }
        return flags;
    }

    /**
     * Reads the coordinates based on the flags array.
     *
     * @param buffer ByteBuffer from which to read the data.
     * @param flags  Array of flags for each point.
     * @return Array of Coordinates for each point.
     */
    private static Coordinate[] readCoordinates(ByteBuffer buffer, byte[] flags) {
        Coordinate[] coordinates = new Coordinate[flags.length];
        short x = 0, y = 0;

        for (int i = 0; i < flags.length; i++) {
            var flagsList = GlyphFlag.identify(flags[i]);
            x += getNextCoordinate(buffer, flagsList, true);
            y += getNextCoordinate(buffer, flagsList, false);
            coordinates[i] = new Coordinate(x, y, GlyphFlag.identify(flags[i]));
        }

        return coordinates;
    }

    /**
     * Helper method to read the next coordinate (x or y) from the buffer.
     *
     * @param buffer ByteBuffer from which to read the data.
     * @param flags  The flags corresponding to the current point.
     * @param isX    Flag indicating whether to read x (true) or y (false) coordinate.
     * @return The coordinate as a short.
     */
    private static short getNextCoordinate(ByteBuffer buffer, List<GlyphFlag> flags, boolean isX) {
        boolean isShortVector = (isX ? flags.contains(GlyphFlag.X_SHORT_VECTOR) : flags.contains(GlyphFlag.Y_SHORT_VECTOR));
        boolean isPositiveOrSame = (isX ? flags.contains(GlyphFlag.X_IS_SAME_OR_POSITIVE_X_SHORT_VECTOR)
                : flags.contains(GlyphFlag.Y_IS_SAME_OR_POSITIVE_Y_SHORT_VECTOR));

        if (isShortVector) {
            byte delta = buffer.get();
            if (isPositiveOrSame) {
                return (short) (delta & 0xFF);  // Positive short vector, correctly interpret as unsigned byte
            } else {
                return (short) (-delta);       // Negative short vector, correctly apply negative sign
            }
        } else {
            if (isPositiveOrSame) {
                return 0;  // Coordinate is the same as the previous
            } else {
                return buffer.getShort();  // Full 16-bit vector, potentially negative, directly returned
            }
        }
    }

    public int getAdvanceWidth() {
        return advanceWidth;
    }

    void setAdvanceWidth(int advanceWidth) {
        this.advanceWidth = advanceWidth;
    }

    @Override
    public String toString() {
        return "SimpleFontGlyph{" +
                "numberOfContours=" + numberOfContours +
                ", xMin=" + xMin +
                ", yMin=" + yMin +
                ", xMax=" + xMax +
                ", yMax=" + yMax +
                ", advanceWidth=" + advanceWidth +
                ", index=" + index +
                '}';
    }

    public short getNumberOfContours() {
        return numberOfContours;
    }

    public short getxMin() {
        return xMin;
    }

    public short getyMin() {
        return yMin;
    }

    public short getxMax() {
        return xMax;
    }

    public short getyMax() {
        return yMax;
    }

    public int[] getEndPtsOfContours() {
        return endPtsOfContours;
    }

    public int getInstructionLength() {
        return instructionLength;
    }

    public byte[] getInstructions() {
        return instructions;
    }

    public byte[] getFlags() {
        return flags;
    }

    public Coordinate[] getCoordinates() {
        return coordinates;
    }

    public int getIndex() {
        return index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    public record Coordinate(short x, short y, List<GlyphFlag> flags) {
    }
}
