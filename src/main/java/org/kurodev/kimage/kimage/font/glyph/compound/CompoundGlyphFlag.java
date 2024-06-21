package org.kurodev.kimage.kimage.font.glyph.compound;

import java.util.ArrayList;
import java.util.List;

public enum CompoundGlyphFlag {
    ARG_1_AND_2_ARE_WORDS(0b1),
    ARGS_ARE_XY_VALUES(0b10),
    ROUND_XY_TO_GRID(0b100),
    WE_HAVE_A_SCALE(0b1_000),
    OBSOLETE(0b10_000),
    MORE_COMPONENTS(0b100_000),
    WE_HAVE_AN_X_AND_Y_SCALE(0b1_000_000),
    WE_HAVE_A_TWO_BY_TWO(0b10_000_000),
    WE_HAVE_INSTRUCTIONS(0b100_000_000),
    USE_MY_METRICS(0b1_000_000_000),
    OVERLAP_COMPOUND(0b10_000_000_000),
    ;
    private final int bit;

    CompoundGlyphFlag(int bit) {
        this.bit = bit;
    }

    public static List<CompoundGlyphFlag> identify(byte flag) {
        var out = new ArrayList<CompoundGlyphFlag>();
        for (CompoundGlyphFlag value : values()) {
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
