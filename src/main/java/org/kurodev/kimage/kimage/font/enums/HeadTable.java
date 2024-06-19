package org.kurodev.kimage.kimage.font.enums;

/**
 * Stores pointers to all values of the required "head" table.
 * Documentation <a href="https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6head.html">Source</a>
 */
public enum HeadTable implements FontTableEntry {
    VERSION(4, true),
    /**
     * set by font manufacturer
     */
    FONT_REVISION(4, true),
    /**
     * To compute: set it to 0, calculate the checksum for the 'head' table and put it in the table directory,
     * sum the entire font as a uint32_t, then store 0xB1B0AFBA - sum.
     * (The checksum for the 'head' table will be wrong as a result. That is OK; do not reset it.)
     */
    CHECKSUM_ADJUSTMENT(4, false),
    /**
     * set to 0x5F0F3CF5
     */
    MAGIC_NUMBER(4, false),
    /**
     * bit 0 - y value of 0 specifies baseline
     * bit 1 - x position of left most black bit is LSB
     * bit 2 - scaled point size and actual point size will differ (i.e. 24 point glyph differs from 12 point glyph scaled by factor of 2)
     * bit 3 - use integer scaling instead of fractional
     * bit 4 - (used by the Microsoft implementation of the TrueType scaler)
     * bit 5 - This bit should be set in fonts that are intended to e laid out vertically, and in which the glyphs have been drawn such that an x-coordinate of 0 corresponds to the desired vertical baseline.
     * bit 6 - This bit must be set to zero.
     * bit 7 - This bit should be set if the font requires layout for correct linguistic rendering (e.g. Arabic fonts).
     * bit 8 - This bit should be set for an AAT font which has one or more metamorphosis effects designated as happening by default.
     * bit 9 - This bit should be set if the font contains any strong right-to-left glyphs.
     * bit 10 - This bit should be set if the font contains Indic-style rearrangement effects.
     * bits 11-13 - Defined by Adobe.
     * bit 14 - This bit should be set if the glyphs in the font are simply generic symbols for code point ranges, such as for a last resort font.
     */
    FLAGS(2, false),
    /**
     * range from 64 to 16384
     */
    UNITS_PER_EM(2, false),
    /**
     * international date
     */
    CREATED(8, false),
    /**
     * international date
     */
    MODIFIED(8, false),
    /**
     * for all glyph bounding boxes
     */
    X_MIN(2, true),
    /**
     * for all glyph bounding boxes
     */
    Y_MIN(2, true),
    /**
     * for all glyph bounding boxes
     */
    X_MAX(2, true),
    /**
     * for all glyph bounding boxes
     */
    Y_MAX(2, true),
    /**
     * bit 0 bold
     * bit 1 italic
     * bit 2 underline
     * bit 3 outline
     * bit 4 shadow
     * bit 5 condensed (narrow)
     * bit 6 extended
     */
    MAC_STYLE(2, false),
    /**
     * smallest readable size in pixels
     */
    LOWEST_RECOMMENDED_PPEM(2, false),
    /**
     * 0 Mixed directional glyphs
     * 1 Only strongly left to right glyphs
     * 2 Like 1 but also contains neutrals
     * -1 Only strongly right to left glyphs
     * -2 Like -1 but also contains neutrals
     */
    FONT_DIRECTION_HINT(2, false),
    /**
     * 0 for short offsets, 1 for long
     */
    INDEX_TO_LOC_FORMAT(2, false),
    /**
     * 0 for current format
     */
    GLYPH_DATA_FORMAT(2, false);

    private final int bytes;
    private final boolean signed;

    HeadTable(int bytes, boolean signed) {
        this.bytes = bytes;
        this.signed = signed;
    }

    @Override
    public int getBytes() {
        return bytes;
    }

    @Override
    public boolean isSigned() {
        return signed;
    }

    @Override
    public String getTable() {
        return "head";
    }

    @Override
    public int getPosition() {
        int position = 0;
        for (HeadTable entry : values()) {
            if (entry == this) {
                break;
            }
            position += entry.bytes;
        }
        return position;
    }


    @Override
    public String toString() {
        return getTable() + "." + name() + "(" + getBytes() + ")";
    }
}

