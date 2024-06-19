package org.kurodev.kimage.kimage.font;

import org.kurodev.kimage.kimage.font.enums.FontTableEntry;
import org.kurodev.kimage.kimage.font.enums.HeadTable;
import org.kurodev.kimage.kimage.font.enums.HheaTable;
import org.kurodev.kimage.kimage.font.enums.MaxpTable;
import org.kurodev.kimage.kimage.font.glyph.FontGlyph;

import java.io.IOException;
import java.io.InputStream;

public interface KFont {


    static KFont getFont() {
        return FontReaders.getDefaultFontReader();
    }

    static KFont getFont(InputStream in) throws IOException {
        return FontReaders.loadFont(in);
    }

    int getLowestRecommendedPPEM();

    FontGlyph getGlyph(char character);

    /**
     * Gets a value from a corresponding table.
     *
     * @implNote This method is <b>unsafe</b> if not used with required tables.
     * @see FontTableEntry
     * @see HheaTable
     * @see HeadTable
     * @see MaxpTable
     */
    int getTableValue(FontTableEntry val);

    /**
     * Gets a value from the hhea table.
     *
     * @param entry The entry value to retrieve
     * @return the value from the table
     * @see HheaTable
     */
    default int getTableValue(HheaTable entry) {
        return getTableValue((FontTableEntry) entry);
    }

    /**
     * Gets a value from the head table.
     *
     * @param entry The entry value to retrieve
     * @return the value from the table
     * @see HeadTable
     */
    default int getTableValue(HeadTable entry) {
        return getTableValue((FontTableEntry) entry);
    }

    /**
     * Gets a value from the maxp table.
     *
     * @param entry The entry value to retrieve
     * @return the value from the table
     * @see MaxpTable
     */
    default int getTableValue(MaxpTable entry) {
        return getTableValue((FontTableEntry) entry);
    }
}
