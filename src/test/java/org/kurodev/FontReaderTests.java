package org.kurodev;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.font.FontReader;
import org.kurodev.kimage.kimage.font.FontReaders;
import org.kurodev.kimage.kimage.font.enums.HeadTable;
import org.kurodev.kimage.kimage.font.enums.HheaTable;
import org.kurodev.kimage.kimage.font.enums.MaxpTable;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FontReaderTests {

    private static FontReader reader;

    @BeforeAll
    public static void prepare() throws IOException {
        reader = FontReaders.loadFont(FontReaders.class.getResourceAsStream("/kimage/Pixelletters.ttf"));
    }

    @Test
    public void testHeadValues() {
        assertEquals(0x00010000, reader.getTableValue(HeadTable.VERSION));
        assertEquals(0x00010000, reader.getTableValue(HeadTable.FONT_REVISION));
        assertEquals(11, reader.getTableValue(HeadTable.FLAGS));
        assertEquals(1024, reader.getTableValue(HeadTable.UNITS_PER_EM));
        assertEquals(0, reader.getTableValue(HeadTable.X_MIN));
        assertEquals(0, reader.getTableValue(HeadTable.Y_MIN));
        assertEquals(768, reader.getTableValue(HeadTable.X_MAX));
        assertEquals(768, reader.getTableValue(HeadTable.Y_MAX));
        assertEquals(0, reader.getTableValue(HeadTable.MAC_STYLE));
        assertEquals(8, reader.getTableValue(HeadTable.LOWEST_RECOMMENDED_PPEM));
        assertEquals(2, reader.getTableValue(HeadTable.FONT_DIRECTION_HINT));
        assertEquals(0, reader.getTableValue(HeadTable.INDEX_TO_LOC_FORMAT));
        assertEquals(0, reader.getTableValue(HeadTable.GLYPH_DATA_FORMAT));
        assertEquals(0x5f0f3cf5, reader.getTableValue(HeadTable.MAGIC_NUMBER), "Magic number is wrong");
        assertEquals(1697301736, reader.getTableValue(HeadTable.CHECKSUM_ADJUSTMENT), "Checksum is wrong");
    }

    @Test
    public void testHheaValues() {
        assertEquals(0x00010000, reader.getTableValue(HheaTable.VERSION));
        assertEquals(768, reader.getTableValue(HheaTable.ASCENT));
        assertEquals(0, reader.getTableValue(HheaTable.DESCENT));
        assertEquals(92, reader.getTableValue(HheaTable.LINE_GAP));
        assertEquals(832, reader.getTableValue(HheaTable.ADVANCE_WIDTH_MAX));
        assertEquals(0, reader.getTableValue(HheaTable.MIN_LEFT_SIDE_BEARING));
        assertEquals(0, reader.getTableValue(HheaTable.MIN_RIGHT_SIDE_BEARING));
        assertEquals(768, reader.getTableValue(HheaTable.X_MAX_EXTENT));
        assertEquals(1, reader.getTableValue(HheaTable.CARET_SLOPE_RISE));
        assertEquals(0, reader.getTableValue(HheaTable.CARET_SLOPE_RUN));
        assertEquals(0, reader.getTableValue(HheaTable.METRIC_DATA_FORMAT));
        assertEquals(31, reader.getTableValue(HheaTable.NUM_OF_LONG_HOR_METRICS));
    }

    @Test
    public void testMaxpValues() {
        assertEquals(0x00010000, reader.getTableValue(MaxpTable.VERSION));
        assertEquals(31, reader.getTableValue(MaxpTable.NUM_OF_GLYPHS));
        assertEquals(94, reader.getTableValue(MaxpTable.MAX_POINTS));
        assertEquals(11, reader.getTableValue(MaxpTable.MAX_CONTOURS));
        assertEquals(0, reader.getTableValue(MaxpTable.MAX_COMPONENT_POINTS));
        assertEquals(0, reader.getTableValue(MaxpTable.MAX_COMPONENT_CONTOURS));
        assertEquals(2, reader.getTableValue(MaxpTable.MAX_ZONES));
        assertEquals(0, reader.getTableValue(MaxpTable.MAX_TWILIGHT_POINTS));
        assertEquals(1, reader.getTableValue(MaxpTable.MAX_STORAGE));
        assertEquals(1, reader.getTableValue(MaxpTable.MAX_FUNCTION_DEFS));
        assertEquals(0, reader.getTableValue(MaxpTable.MAX_INSTRUCTION_DEFS));
        assertEquals(64, reader.getTableValue(MaxpTable.MAX_STACK_ELEMENTS));
        assertEquals(46, reader.getTableValue(MaxpTable.MAX_SIZE_OF_INSTRUCTIONS));
        assertEquals(0, reader.getTableValue(MaxpTable.MAX_COMPONENT_ELEMENTS));
        assertEquals(0, reader.getTableValue(MaxpTable.MAX_COMPONENT_DEPTH));

    }
}
