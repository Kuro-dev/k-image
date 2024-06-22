package org.kurodev.kimage.kimage.font.enums;

/**
 * <a href="https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6cmap.html">source</a>
 */
public enum CmapPlatformID {
    UNICODE(0),
    MACINTOSH(1),
    RESERVED(2),
    MICROSOFT(3);

    private final int value;

    CmapPlatformID(int value) {
        this.value = value;
    }

    public static CmapPlatformID fromValue(int value) {
        for (CmapPlatformID encoding : values()) {
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
