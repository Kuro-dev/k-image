package org.kurodev.kimage.kimage.util;

public class Util {
    private static final double KILOBYTE = 1024;
    private static final double MEGABYTE = 1024 * 1024;
    private static final double GIGABYTE = 1024 * 1024 * 1024;

    /**
     * Turns bytes into a nicer String representing size.
     * <p>convertBytes(500);         // 500B</p>
     * <p>convertBytes(2048);        // 2.00KB</p>
     * <p>convertBytes(500000);      // 488.28KB</p>
     * <p>convertBytes(10485760);    // 10.00MB</p>
     * <p>convertBytes(1073741824);  // 1.00GB</p>
     */
    public static String bytesToString(int bytes) {
        if (bytes < KILOBYTE) {
            return bytes + "B";
        } else if (bytes < MEGABYTE) {
            return String.format("%.2fKB", bytes / KILOBYTE);
        } else if (bytes < GIGABYTE) {
            return String.format("%.2fMB", bytes / (MEGABYTE));
        } else {
            return String.format("%.2fGB", bytes / (GIGABYTE));
        }
    }

}
