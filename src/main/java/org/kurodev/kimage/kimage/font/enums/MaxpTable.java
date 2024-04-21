package org.kurodev.kimage.kimage.font.enums;


public enum MaxpTable {
    VERSION(4, false),
    NUM_OF_GLYPHS(2, false),
    MAX_POINTS(2, false),
    MAX_CONTOURS(2, false),
    MAX_COMPONENT_POINTS(2, false),
    MAX_COMPONENT_CONTOURS(2, false),
    MAX_ZONES(2, false),
    MAX_TWILIGHT_POINTS(2, false),
    MAX_STORAGE(2, false),
    MAX_FUNCTION_DEFS(2, false),
    MAX_INSTRUCTION_DEFS(2, false),
    MAX_STACK_ELEMENTS(2, false),
    MAX_SIZE_OF_INSTRUCTIONS(2, false),
    MAX_COMPONENT_ELEMENTS(2, false),
    MAX_COMPONENT_DEPTH(2, false);

    private final int bytes;
    private final boolean signed;

    MaxpTable(int bytes, boolean signed) {
        this.bytes = bytes;
        this.signed = signed;
    }

    public int getBytes() {
        return bytes;
    }

    public boolean isSigned() {
        return signed;
    }

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

