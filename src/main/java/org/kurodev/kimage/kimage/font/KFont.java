package org.kurodev.kimage.kimage.font;

import org.kurodev.kimage.kimage.font.enums.FontTableEntry;
import org.kurodev.kimage.kimage.font.enums.HeadTable;
import org.kurodev.kimage.kimage.font.enums.HheaTable;
import org.kurodev.kimage.kimage.font.enums.MaxpTable;
import org.kurodev.kimage.kimage.font.glyph.FontGlyph;
import org.kurodev.kimage.kimage.font.glyph.FontStyle;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface KFont {


    static KFont getFont() {
        return FontReaders.getDefaultFontReader();
    }

    static KFont getFont(InputStream in) throws IOException {
        return FontReaders.loadFont(in);
    }

    int getLowestRecommendedPPEM();

    FontGlyph getGlyph(char character);

    FontGlyph getGlyph(int index);

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

    List<FontGlyph> getGlyphs(String str);

    void drawString(Drawable drawable, int x, int y, int fontSizePx, Color color, String str, FontStyle... styles);

    void drawString(Drawable drawable, int x, int y, double scale, Color color, String str, FontStyle... styles);
}
