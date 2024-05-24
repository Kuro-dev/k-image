package org.kurodev.font.headers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kurodev.kimage.kimage.draw.DrawableImage;
import org.kurodev.kimage.kimage.font.FontReader;
import org.kurodev.kimage.kimage.font.FontReaders;
import org.kurodev.kimage.kimage.font.enums.HeadTable;
import org.kurodev.kimage.kimage.font.enums.HheaTable;
import org.kurodev.kimage.kimage.font.enums.MaxpTable;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FontReaderHeadersTests {

    private static FontReader small;
    private static FontReader full;

    @BeforeAll
    public static void prepare() throws IOException {
        small = FontReaders.loadFont(FontReaders.class.getResourceAsStream("/kimage/Pixelletters.ttf"));
        full = FontReaders.loadFont(FontReaders.class.getResourceAsStream("/kimage/Pixellettersfull.ttf"));
    }

    @Test
    public void testHeadValues() {
        assertEquals(0x00010000, small.getTableValue(HeadTable.VERSION));
        assertEquals(0x00010000, small.getTableValue(HeadTable.FONT_REVISION));
        assertEquals(11, small.getTableValue(HeadTable.FLAGS));
        assertEquals(1024, small.getTableValue(HeadTable.UNITS_PER_EM));
        assertEquals(0, small.getTableValue(HeadTable.X_MIN));
        assertEquals(0, small.getTableValue(HeadTable.Y_MIN));
        assertEquals(768, small.getTableValue(HeadTable.X_MAX));
        assertEquals(768, small.getTableValue(HeadTable.Y_MAX));
        assertEquals(0, small.getTableValue(HeadTable.MAC_STYLE));
        assertEquals(8, small.getTableValue(HeadTable.LOWEST_RECOMMENDED_PPEM));
        assertEquals(2, small.getTableValue(HeadTable.FONT_DIRECTION_HINT));
        assertEquals(0, small.getTableValue(HeadTable.INDEX_TO_LOC_FORMAT));
        assertEquals(0, small.getTableValue(HeadTable.GLYPH_DATA_FORMAT));
        assertEquals(0x5f0f3cf5, small.getTableValue(HeadTable.MAGIC_NUMBER), "Magic number is wrong");
        assertEquals(1697301736, small.getTableValue(HeadTable.CHECKSUM_ADJUSTMENT), "Checksum is wrong");
    }

    @Test
    public void testHheaValues() {
        assertEquals(0x00010000, small.getTableValue(HheaTable.VERSION));
        assertEquals(768, small.getTableValue(HheaTable.ASCENT));
        assertEquals(0, small.getTableValue(HheaTable.DESCENT));
        assertEquals(92, small.getTableValue(HheaTable.LINE_GAP));
        assertEquals(832, small.getTableValue(HheaTable.ADVANCE_WIDTH_MAX));
        assertEquals(0, small.getTableValue(HheaTable.MIN_LEFT_SIDE_BEARING));
        assertEquals(0, small.getTableValue(HheaTable.MIN_RIGHT_SIDE_BEARING));
        assertEquals(768, small.getTableValue(HheaTable.X_MAX_EXTENT));
        assertEquals(1, small.getTableValue(HheaTable.CARET_SLOPE_RISE));
        assertEquals(0, small.getTableValue(HheaTable.CARET_SLOPE_RUN));
        assertEquals(0, small.getTableValue(HheaTable.METRIC_DATA_FORMAT));
        assertEquals(31, small.getTableValue(HheaTable.NUM_OF_LONG_HOR_METRICS));
    }

    @Test
    public void testMaxpValues() {
        assertEquals(0x00010000, small.getTableValue(MaxpTable.VERSION));
        assertEquals(31, small.getTableValue(MaxpTable.NUM_OF_GLYPHS));
        assertEquals(94, small.getTableValue(MaxpTable.MAX_POINTS));
        assertEquals(11, small.getTableValue(MaxpTable.MAX_CONTOURS));
        assertEquals(0, small.getTableValue(MaxpTable.MAX_COMPONENT_POINTS));
        assertEquals(0, small.getTableValue(MaxpTable.MAX_COMPONENT_CONTOURS));
        assertEquals(2, small.getTableValue(MaxpTable.MAX_ZONES));
        assertEquals(0, small.getTableValue(MaxpTable.MAX_TWILIGHT_POINTS));
        assertEquals(1, small.getTableValue(MaxpTable.MAX_STORAGE));
        assertEquals(1, small.getTableValue(MaxpTable.MAX_FUNCTION_DEFS));
        assertEquals(0, small.getTableValue(MaxpTable.MAX_INSTRUCTION_DEFS));
        assertEquals(64, small.getTableValue(MaxpTable.MAX_STACK_ELEMENTS));
        assertEquals(46, small.getTableValue(MaxpTable.MAX_SIZE_OF_INSTRUCTIONS));
        assertEquals(0, small.getTableValue(MaxpTable.MAX_COMPONENT_ELEMENTS));
        assertEquals(0, small.getTableValue(MaxpTable.MAX_COMPONENT_DEPTH));
    }

    @Test
    public void testHeadValuesFull() {
        assertEquals(0x00010000, full.getTableValue(HeadTable.VERSION));
        assertEquals(0x00010000, full.getTableValue(HeadTable.FONT_REVISION));
        assertEquals(11, full.getTableValue(HeadTable.FLAGS));
        assertEquals(1024, full.getTableValue(HeadTable.UNITS_PER_EM));
        assertEquals(0, full.getTableValue(HeadTable.X_MIN));
        assertEquals(-192, full.getTableValue(HeadTable.Y_MIN));
        assertEquals(576, full.getTableValue(HeadTable.X_MAX));
        assertEquals(682, full.getTableValue(HeadTable.Y_MAX));
        assertEquals(0, full.getTableValue(HeadTable.MAC_STYLE));
        assertEquals(8, full.getTableValue(HeadTable.LOWEST_RECOMMENDED_PPEM));
        assertEquals(2, full.getTableValue(HeadTable.FONT_DIRECTION_HINT));
        assertEquals(0, full.getTableValue(HeadTable.INDEX_TO_LOC_FORMAT));
        assertEquals(0, full.getTableValue(HeadTable.GLYPH_DATA_FORMAT));
        assertEquals(0x5f0f3cf5, full.getTableValue(HeadTable.MAGIC_NUMBER), "Magic number is wrong");
        assertEquals(Integer.parseUnsignedInt("2240484246"), full.getTableValue(HeadTable.CHECKSUM_ADJUSTMENT), "Checksum is wrong");
    }

    @Test
    public void testHheaValuesFull() {
        assertEquals(0x00010000, full.getTableValue(HheaTable.VERSION));
        assertEquals(682, full.getTableValue(HheaTable.ASCENT));
        assertEquals(-192, full.getTableValue(HheaTable.DESCENT));
        assertEquals(92, full.getTableValue(HheaTable.LINE_GAP));
        assertEquals(640, full.getTableValue(HheaTable.ADVANCE_WIDTH_MAX));
        assertEquals(0, full.getTableValue(HheaTable.MIN_LEFT_SIDE_BEARING));
        assertEquals(0, full.getTableValue(HheaTable.MIN_RIGHT_SIDE_BEARING));
        assertEquals(576, full.getTableValue(HheaTable.X_MAX_EXTENT));
        assertEquals(1, full.getTableValue(HheaTable.CARET_SLOPE_RISE));
        assertEquals(0, full.getTableValue(HheaTable.CARET_SLOPE_RUN));
        assertEquals(0, full.getTableValue(HheaTable.METRIC_DATA_FORMAT));
        assertEquals(99, full.getTableValue(HheaTable.NUM_OF_LONG_HOR_METRICS));
    }

    @Test
    public void testMaxpValuesFull() {
        assertEquals(0x00010000, full.getTableValue(MaxpTable.VERSION));
        assertEquals(99, full.getTableValue(MaxpTable.NUM_OF_GLYPHS));
        assertEquals(82, full.getTableValue(MaxpTable.MAX_POINTS));
        assertEquals(12, full.getTableValue(MaxpTable.MAX_CONTOURS));
        assertEquals(0, full.getTableValue(MaxpTable.MAX_COMPONENT_POINTS));
        assertEquals(0, full.getTableValue(MaxpTable.MAX_COMPONENT_CONTOURS));
        assertEquals(2, full.getTableValue(MaxpTable.MAX_ZONES));
        assertEquals(0, full.getTableValue(MaxpTable.MAX_TWILIGHT_POINTS));
        assertEquals(1, full.getTableValue(MaxpTable.MAX_STORAGE));
        assertEquals(1, full.getTableValue(MaxpTable.MAX_FUNCTION_DEFS));
        assertEquals(0, full.getTableValue(MaxpTable.MAX_INSTRUCTION_DEFS));
        assertEquals(64, full.getTableValue(MaxpTable.MAX_STACK_ELEMENTS));
        assertEquals(46, full.getTableValue(MaxpTable.MAX_SIZE_OF_INSTRUCTIONS));
        assertEquals(0, full.getTableValue(MaxpTable.MAX_COMPONENT_ELEMENTS));
        assertEquals(0, full.getTableValue(MaxpTable.MAX_COMPONENT_DEPTH));
    }

    @Test
    public void test() throws IOException {
        DrawableImage img = new DrawableImage(1000, 1000);
        img.drawString(0,0,"B", Color.BLACK);
        Files.write(Path.of("./test.png"),img.encode());
    }
}
