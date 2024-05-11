package org.kurodev.kimage.kimage.font.enums;

/**
 * Documentation: <a href="https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6maxp.html">Source</a>
 */
public enum MaxpTable implements FontTableEntry {
    VERSION(4, false),
    /**
     * the number of glyphs in the font
     */
    NUM_OF_GLYPHS(2, false),
    /**
     * points in non-compound glyph
     */
    MAX_POINTS(2, false),
    /**
     * contours in non-compound glyph
     */
    MAX_CONTOURS(2, false),
    /**
     * points in compound glyph
     */
    MAX_COMPONENT_POINTS(2, false),
    /**
     * contours in compound glyph
     */
    MAX_COMPONENT_CONTOURS(2, false),
    /**
     * set to 2
     */
    MAX_ZONES(2, false),
    /**
     * points used in Twilight Zone (Z0)
     */
    MAX_TWILIGHT_POINTS(2, false),
    /**
     * number of Storage Area locations
     */
    MAX_STORAGE(2, false),
    /**
     * number of FDEFs
     */
    MAX_FUNCTION_DEFS(2, false),
    /**
     * number of IDEFs
     */
    MAX_INSTRUCTION_DEFS(2, false),
    /**
     * maximum stack depth
     */
    MAX_STACK_ELEMENTS(2, false),
    /**
     * The maxSizeOfInstructions is the maximum size in bytes for all of the instructions associated with a particular glyph.
     * byte count for glyph instructions
     */
    MAX_SIZE_OF_INSTRUCTIONS(2, false),
    /**
     * The maxComponentElements field refers to the maximum number of simple glyphs that will be used to create a compound glyph.
     * <p>
     * number of glyphs referenced at top level
     */
    MAX_COMPONENT_ELEMENTS(2, false),
    /**
     * The maxComponentDepth refers to the number of levels of recursion used in constructing the most complex compound glyph.
     * The maximum legal value for maxComponentDepth is 16. If there are no components within components,
     * all compound glyphs can be deemed simple and this field can be set to the value one.
     * <p>
     * levels of recursion, set to 0 if font has only simple glyphs
     */
    MAX_COMPONENT_DEPTH(2, false);

    private final int bytes;
    private final boolean signed;

    MaxpTable(int bytes, boolean signed) {
        this.bytes = bytes;
        this.signed = signed;
    }


    @Override
    public String getTable() {
        return "maxp";
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
    public int getPosition() {
        int position = 0;
        for (MaxpTable table : values()) {
            if (table == this) {
                break;
            }
            position += table.bytes;
        }
        return position;
    }
}

