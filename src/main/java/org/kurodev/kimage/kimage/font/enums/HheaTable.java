package org.kurodev.kimage.kimage.font.enums;

public enum HheaTable {
    VERSION(4, false),
    ASCENT(2, true),
    DESCENT(2, true),
    LINE_GAP(2, true),
    ADVANCE_WIDTH_MAX(2, false),
    MIN_LEFT_SIDE_BEARING(2, true),
    MIN_RIGHT_SIDE_BEARING(2, true),
    X_MAX_EXTENT(2, true),
    CARET_SLOPE_RISE(2, true),
    CARET_SLOPE_RUN(2, true),
    CARET_OFFSET(2, true),
    RESERVED1(2, true),
    RESERVED2(2, true),
    RESERVED3(2, true),
    RESERVED4(2, true),
    METRIC_DATA_FORMAT(2, true),
    NUM_OF_LONG_HOR_METRICS(2, false);

    private final int bytes;
    private final boolean signed;

    HheaTable(int bytes, boolean signed) {
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
        for (HheaTable table : values()) {
            if (table == this) {
                break;
            }
            position += table.bytes;
        }
        return position;
    }
}
