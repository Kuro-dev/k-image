package org.kurodev.kimage.kimage.font.glyph.simple;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum representing the various flags in a TrueType Glyph's flag array.
 * Each flag controls how the coordinate data following it should be interpreted.
 */
public enum GlyphFlag {
    /**
     * If set, the point is on the curve; otherwise, it is off the curve.
     */
    ON_CURVE(0b1),

    /**
     * If set, the corresponding x-coordinate is 1 byte long. If not set, it is 2 bytes.
     * If the x-coordinate is not set to be repeated, this flag also indicates that the
     * x-coordinate is the same as the previous x-coordinate.
     */
    X_SHORT_VECTOR(0b10),

    /**
     * If set, the corresponding y-coordinate is 1 byte long. If not set, it is 2 bytes.
     * If the y-coordinate is not set to be repeated, this flag also indicates that the
     * y-coordinate is the same as the previous y-coordinate.
     */
    Y_SHORT_VECTOR(0b100),

    /**
     * If set, the next byte specifies the number of additional times this flag is to be
     * repeated in the logical array of flags. This is used to compress the flag data.
     */
    REPEAT_FLAG(0b1000),

    /**
     * This flag has two meanings, depending on how the X_SHORT_VECTOR flag is set.
     * If X_SHORT_VECTOR is set, this flag indicates that the x-coordinate is positive;
     * if not set, the x-coordinate is negative.
     * If X_SHORT_VECTOR is not set and this flag is set, then the current x-coordinate
     * is the same as the previous x-coordinate.
     */
    X_IS_SAME_OR_POSITIVE_X_SHORT_VECTOR(0b10000),

    /**
     * This flag has two meanings, depending on how the Y_SHORT_VECTOR flag is set.
     * If Y_SHORT_VECTOR is set, this flag indicates that the y-coordinate is positive;
     * if not set, the y-coordinate is negative.
     * If Y_SHORT_VECTOR is not set and this flag is set, then the current y-coordinate
     * is the same as the previous y-coordinate.
     */
    Y_IS_SAME_OR_POSITIVE_Y_SHORT_VECTOR(0b100000);

    private final int bit;

    GlyphFlag(int bit) {
        this.bit = bit;
    }

    public static List<GlyphFlag> identify(byte flag) {
        var out = new ArrayList<GlyphFlag>();
        for (GlyphFlag value : values()) {
            if (value.check(flag)) {
                out.add(value);
            }
        }
        return out;
    }

    /**
     * Returns the bit value associated with the flag.
     *
     * @return the bit value as an integer.
     */
    public int getBit() {
        return this.bit;
    }

    /**
     * Checks if this flag is set in the given flags byte.
     *
     * @param flagsByte the byte of flags read from the glyph data.
     * @return true if this flag is set, false otherwise.
     */
    public boolean check(byte flagsByte) {
        return (flagsByte & this.bit) != 0;
    }
}
