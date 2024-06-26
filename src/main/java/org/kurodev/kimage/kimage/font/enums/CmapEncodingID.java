package org.kurodev.kimage.kimage.font.enums;
/**
 * <a href="https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6cmap.html">source</a>
 */
public enum CmapEncodingID {
    ROMAN(0),
    UNICODE_2_0_BMP(3),
    UNICODE_2_0_FULL_REPERTIORE(4),
    UNICODE_BMP(1),
    UNICODE_FULL_REPERTIORE(10);
    private final int value;

    CmapEncodingID(int value) {
        this.value = value;
    }

    public static CmapEncodingID fromValue(int value) {
        for (CmapEncodingID encoding : values()) {
            if (encoding.value == value) {
                return encoding;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
