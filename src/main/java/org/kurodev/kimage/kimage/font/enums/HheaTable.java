package org.kurodev.kimage.kimage.font.enums;

/**
 * Documentation <a href="https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6hhea.html">Source</a>
 */
public enum HheaTable implements FontTableEntry {
    VERSION(4, false),
    /**
     * The values for {@link #ASCENT ascent}, {@link #DESCENT descent} and {@link #LINE_GAP lineGap} represent the design intentions of the font's creator rather than any computed value,
     * and individual glyphs may well exceed the limits they represent.
     * The values for the {@link #ADVANCE_WIDTH_MAX advanceWidthMax}, {@link #MIN_LEFT_SIDE_BEARING minLeftSideBearing} and {@link #MIN_RIGHT_SIDE_BEARING minRightSideBearing} are computed values
     * and must be consistent with whatever values appear in the 'hmtx' table.
     * <p>
     * These values are as their names imply, the actual maximum advance width for any glyph in the font,
     * the minimum left side bearing for any glyph and the minimum right side bearing for any glyph.
     * Similarly, the xMin, yMin, xMax, and yMax, fields in the 'head' represent the actual extrema for the glyphs in the font.
     * <p>
     * Distance from baseline of highest ascender
     */
    ASCENT(2, true),
    /**
     * Distance from baseline of lowest descender
     *
     * @see #ASCENT
     */
    DESCENT(2, true),
    /**
     * typographic line gap
     *
     * @see #ASCENT
     */
    LINE_GAP(2, true),
    /**
     * must be consistent with horizontal metrics
     *
     * @see #ASCENT
     */
    ADVANCE_WIDTH_MAX(2, false),
    /**
     * must be consistent with horizontal metrics
     *
     * @see #ASCENT
     */
    MIN_LEFT_SIDE_BEARING(2, true),
    /**
     * must be consistent with horizontal metrics
     *
     * @see #ASCENT
     */
    MIN_RIGHT_SIDE_BEARING(2, true),
    /**
     * max(lsb + (xMax-xMin))
     */
    X_MAX_EXTENT(2, true),
    /**
     * The {@code caretSlopeRise} and {@code caretSlopeRun} are used to specify the mathematical slope of the proper angle for the caret when displayed with this font.
     * The slope is equal to {@code caretSlopeRise} divided by {@code caretSlopeRun}.
     * Thus, a rise of 1 and run of 0 will specify a vertical caret (infinite slope).
     * A rise of 0 and run of 1 will specify a horizontal caret (zero slope).
     * Something in-between will be desirable for fonts whose glyphs are obliqued or italic.
     * For example, one could use a rise of 2048 and a run of 270 for a slope of 7.6, which corresponds to an angle of 82.5°. (tan(82.5°) = 7.6)
     * <p>
     * Note that since the slope is a ratio, values may be used which simplify or speed up calculations.
     * A {@code caretSlopeRise} of 8 and {@code caretSlopeRun} of 6 means the same thing as {@code caretSlopeRise} of 4 and {@code caretSlopeRun} of 3.
     * As such, a run of 0 is sufficient to specify a vertical caret; the rise is superfluous.
     * Similarly, a rise of 0 is sufficient to specify a horizontal caret.
     * <p>
     * used to calculate the slope of the caret (rise/run) set to 1 for vertical caret
     *
     * @apiNote {@code caretSlopeRise} and {@code caretSlopeRun} may not both be zero.
     */
    CARET_SLOPE_RISE(2, true),
    /**
     * 0 for vertical
     *
     * @see #CARET_SLOPE_RISE
     */
    CARET_SLOPE_RUN(2, true),
    /**
     * The {@code caretOffset} value is the amount by which a slanted highlight on a glyph needs to be shifted to produce the best appearance.
     * Since {@code caretOffset} is a signed FUnit value, it will scale.
     * set value to 0 for non-slanted fonts
     */
    CARET_OFFSET(2, true),
    RESERVED1(2, true),
    RESERVED2(2, true),
    RESERVED3(2, true),
    RESERVED4(2, true),
    /**
     * 0 for current format
     */
    METRIC_DATA_FORMAT(2, true),
    /**
     * The value numOfLongHorMetrics is used by the 'hmtx' table.
     * number of advance widths in metrics table
     */
    NUM_OF_LONG_HOR_METRICS(2, false);

    private final int bytes;
    private final boolean signed;

    HheaTable(int bytes, boolean signed) {
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
        return "hhea";
    }

    @Override
    public int getPosition() {
        int position = 0;
        for (HheaTable table : values()) {
            if (table == this) {
                break;
            }
            position += table.bytes;
        }
        return position;
    }
}
