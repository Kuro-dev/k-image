package org.kurodev.kimage.kimage.font.enums;


public enum HeadTable {
    VERSION(4, true),
    FONT_REVISION(4, true),
    CHECKSUM_ADJUSTMENT(4, false),
    MAGIC_NUMBER(4, false),
    FLAGS(2, false),
    UNITS_PER_EM(2, false),
    CREATED(8, false),
    MODIFIED(8, false),
    X_MIN(2, true),
    Y_MIN(2, true),
    X_MAX(2, true),
    Y_MAX(2, true),
    MAC_STYLE(2, false),
    LOWEST_RECOMMENDED_PPEM(2, false),
    FONT_DIRECTION_HINT(2, false),
    INDEX_TO_LOC_FORMAT(2, false),
    GLYPH_DATA_FORMAT(2, false);

    private final int bytes;
    private final boolean signed;

    HeadTable(int bytes, boolean signed) {
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
        for (HeadTable entry : values()) {
            if (entry == this) {
                break;
            }
            position += entry.bytes;
        }
        return position;
    }
}

