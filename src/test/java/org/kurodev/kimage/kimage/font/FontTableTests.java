package org.kurodev.kimage.kimage.font;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.font.enums.FontTableEntry;
import org.kurodev.kimage.kimage.font.enums.HeadTable;
import org.kurodev.kimage.kimage.font.enums.HheaTable;
import org.kurodev.kimage.kimage.font.enums.MaxpTable;

import java.io.IOException;

public class FontTableTests {
    private static KFont font;

    @BeforeAll
    public static void prepare() throws IOException {
        font = KFont.getFont(FontTableTests.class.getResourceAsStream("/kimage/Pixellettersfull.ttf"));
    }

    private static void assertEquals(int expected, FontTableEntry entry) {
        Assertions.assertEquals(expected, font.getTableValue(entry), entry.toString());
    }

    @Test
    public void headTable() {
        assertEquals(0x0001_0000, HeadTable.VERSION);
        assertEquals(0x0001_0000, HeadTable.FONT_REVISION);
        assertEquals(Integer.parseUnsignedInt("2240484246"), HeadTable.CHECKSUM_ADJUSTMENT);
        assertEquals(0x5f0f3cf5, HeadTable.MAGIC_NUMBER);
        assertEquals(11, HeadTable.FLAGS);
        assertEquals(1024, HeadTable.UNITS_PER_EM);
        assertEquals(0, HeadTable.X_MIN);
        assertEquals(-192, HeadTable.Y_MIN);
        assertEquals(576, HeadTable.X_MAX);
        assertEquals(682, HeadTable.Y_MAX);
        assertEquals(0, HeadTable.MAC_STYLE);
        assertEquals(8, HeadTable.LOWEST_RECOMMENDED_PPEM);
        assertEquals(2, HeadTable.FONT_DIRECTION_HINT);
        assertEquals(0, HeadTable.INDEX_TO_LOC_FORMAT);
        assertEquals(0, HeadTable.GLYPH_DATA_FORMAT);
    }

    @Test
    public void hheaTable() {
        assertEquals(0x0001_0000, HheaTable.VERSION);
        assertEquals(682, HheaTable.ASCENT);
        assertEquals(-192, HheaTable.DESCENT);
        assertEquals(92, HheaTable.LINE_GAP);
        assertEquals(640, HheaTable.ADVANCE_WIDTH_MAX);
        assertEquals(0, HheaTable.MIN_LEFT_SIDE_BEARING);
        assertEquals(0, HheaTable.MIN_RIGHT_SIDE_BEARING);
        assertEquals(576, HheaTable.X_MAX_EXTENT);
        assertEquals(1, HheaTable.CARET_SLOPE_RISE);
        assertEquals(0, HheaTable.CARET_SLOPE_RUN);
        assertEquals(0, HheaTable.CARET_OFFSET);
        assertEquals(0, HheaTable.METRIC_DATA_FORMAT);
        assertEquals(99, HheaTable.NUM_OF_LONG_HOR_METRICS);
    }

    @Test
    public void maxpTable() {
        assertEquals(0x0001_0000, MaxpTable.VERSION);
        assertEquals(99, MaxpTable.NUM_OF_GLYPHS);
        assertEquals(82, MaxpTable.MAX_POINTS);
        assertEquals(12, MaxpTable.MAX_CONTOURS);
        assertEquals(0, MaxpTable.MAX_COMPONENT_POINTS);
        assertEquals(0, MaxpTable.MAX_COMPONENT_CONTOURS);
        assertEquals(2, MaxpTable.MAX_ZONES);
        assertEquals(0, MaxpTable.MAX_TWILIGHT_POINTS);
        assertEquals(1, MaxpTable.MAX_STORAGE);
        assertEquals(1, MaxpTable.MAX_FUNCTION_DEFS);
        assertEquals(0, MaxpTable.MAX_INSTRUCTION_DEFS);
        assertEquals(64, MaxpTable.MAX_STACK_ELEMENTS);
        assertEquals(0, MaxpTable.MAX_COMPONENT_ELEMENTS);
        assertEquals(0, MaxpTable.MAX_COMPONENT_DEPTH);
    }
}
