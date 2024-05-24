package org.kurodev.kimage.kimage.font;

import org.kurodev.kimage.kimage.font.enums.FontTableEntry;
import org.kurodev.kimage.kimage.font.enums.HeadTable;
import org.kurodev.kimage.kimage.font.enums.HheaTable;
import org.kurodev.kimage.kimage.font.enums.MaxpTable;
import org.kurodev.kimage.kimage.font.glyph.FontGlyph;
import org.kurodev.kimage.kimage.font.glyph.GlyphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class FontReader {
    private static final Logger logger = LoggerFactory.getLogger(FontReader.class);

    private byte[] data;
    private int sfntVersion;
    private int numTables;
    private int searchRange;
    private int entrySelector;
    private int rangeShift;
    private TableEntry[] tableEntries;


    public void load(DataInputStream in) throws IOException {
        data = in.readAllBytes();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        // Read the offset table
        sfntVersion = dis.readInt();
        numTables = dis.readUnsignedShort();
        searchRange = dis.readUnsignedShort();
        entrySelector = dis.readUnsignedShort();
        rangeShift = dis.readUnsignedShort();

        // Read the table directory entries
        tableEntries = new TableEntry[numTables];
        for (int i = 0; i < numTables; i++) {
            byte[] tagBytes = new byte[4];
            dis.readFully(tagBytes);
            String tag = new String(tagBytes, StandardCharsets.UTF_8);
            int checkSum = dis.readInt();
            int offset = dis.readInt();
            int length = dis.readInt();
            tableEntries[i] = new TableEntry(tag, checkSum, offset, length);
        }
        logger.debug("tables: {}", numTables);
        for (TableEntry entry : tableEntries) {
            logger.debug("{}", entry);
        }
    }

    private Optional<TableEntry> getTableEntry(String tag) {
        for (TableEntry entry : tableEntries) {
            if (entry.tag.equals(tag)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    private Optional<ByteBuffer> getTableData(String tag) {
        var optional = getTableEntry(tag);
        if (optional.isPresent()) {
            var entry = optional.get();
            byte[] data = new byte[entry.length];
            System.arraycopy(this.data, entry.offset, data, 0, entry.length);
            ByteBuffer buf = ByteBuffer.wrap(data);
            buf.order(ByteOrder.BIG_ENDIAN);
            return Optional.of(buf);
        }
        return Optional.empty();
    }

    /**
     * Tables that must always exist:
     * "cmap", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "name", "post"
     * Some of these have been converted to Enum maps for better accessibility
     *
     * @see HeadTable
     * @see HheaTable
     * @see MaxpTable
     */
    private ByteBuffer getTableDataUnsafe(String tag) {
        var optional = getTableEntry(tag);
        if (optional.isPresent()) {
            var entry = optional.get();
            byte[] data = new byte[entry.length];
            System.arraycopy(this.data, entry.offset, data, 0, entry.length);
            ByteBuffer buf = ByteBuffer.wrap(data);
            buf.order(ByteOrder.BIG_ENDIAN);
            return buf;
        }
        throw new IllegalStateException("Required table " + tag + " not present in file");
    }

    /**
     * <a href="https://developer.apple.com/fonts/TrueType-Reference-Manual/RM07/appendixB.html">Documentation</a>
     */
    public int getGlyphIndex(char character) {
        ByteBuffer cmap = getTableDataUnsafe("cmap");
        // Skipping the cmap table header and subtable headers to focus on a simple format 4 subtable
        cmap.position(cmap.position() + 2); // skip version
        int numTables = cmap.getShort();
        int unicodeCmapOffset = -1;
        for (int i = 0; i < numTables; i++) {
            int platformID = cmap.getShort();
            int encodingID = cmap.getShort();
            int offset = cmap.getInt();
            if (platformID == 0 || (platformID == 3 && encodingID == 1)) { // Unicode or Microsoft Unicode format
                unicodeCmapOffset = offset;
                break;
            }
        }
        if (unicodeCmapOffset == -1) {
            logger.error("Suitable cmap subtable not found");
            return -1;
        }
        cmap.position(unicodeCmapOffset);
        int format = cmap.getShort();
        if (format != 4) {
            logger.error("Unsupported cmap format {}", format);
            return -1;
        }
        // Reading specific format 4
        cmap.getShort(); // length
        cmap.getShort(); // language
        int segCountX2 = cmap.getShort();
        int segCount = segCountX2 / 2;
        cmap.getShort(); // searchRange
        cmap.getShort(); // entrySelector
        cmap.getShort(); // rangeShift
        int[] endCounts = new int[segCount];
        for (int i = 0; i < segCount; i++) {
            endCounts[i] = cmap.getShort();
        }
        cmap.getShort(); // reservedPad
        int[] startCounts = new int[segCount];
        int[] idDeltas = new int[segCount];
        int[] idRangeOffsets = new int[segCount];
        for (int i = 0; i < segCount; i++) {
            startCounts[i] = cmap.getShort();
        }
        for (int i = 0; i < segCount; i++) {
            idDeltas[i] = cmap.getShort();
        }
        for (int i = 0; i < segCount; i++) {
            idRangeOffsets[i] = cmap.getShort();
        }
        // Now i just need to find the character index
        for (int i = 0; i < segCount; i++) {
            if (character >= startCounts[i] && character <= endCounts[i]) {
                int index;
                if (idRangeOffsets[i] == 0) {
                    index = (character + idDeltas[i]) % 0x10000;
                } else {
                    int offset = (character - startCounts[i]) * 2 + idRangeOffsets[i] + (i - segCount) * 2;
                    cmap.position(unicodeCmapOffset + offset);
                    index = cmap.getShort() + idDeltas[i];
                    index = index % 0x10000;
                }
                return index;
            }
        }
        return 0; // not found, returning .notdef index
    }

    public FontGlyph getGlyph(String character) {
        if (character.length() != 1) {
            throw new IllegalArgumentException("Can only identify one glyph at the time");
        }
        return getGlyph(character.charAt(0));
    }

    public FontGlyph getGlyph(char character) {
        int glyphIndex = getGlyphIndex(character);
        ByteBuffer glyf = getTableDataUnsafe("glyf");
        ByteBuffer loca = getTableDataUnsafe("loca");

        int indexToLocFormat = getTableValue(HeadTable.INDEX_TO_LOC_FORMAT);
        int glyphOffset;
        int nextGlyphOffset;

        if (indexToLocFormat == 0) {
            glyphOffset = loca.getShort(glyphIndex * 2) * 2;
            nextGlyphOffset = loca.getShort((glyphIndex + 1) * 2) * 2;
        } else {
            glyphOffset = loca.getInt(glyphIndex * 4);
            nextGlyphOffset = loca.getInt((glyphIndex + 1) * 4);
        }

        if (glyphOffset == nextGlyphOffset) {
            return null; // This glyph has no outline data.
        }

        glyf.position(glyphOffset);
        short numberOfContours = glyf.getShort();

        if (numberOfContours < 0) {
            throw new IllegalStateException("Composite glyphs are not supported yet.");
        }

        return GlyphFactory.readSimpleGlyph(glyf, numberOfContours, glyphOffset, character, getAdvanceWidth(glyphIndex));
    }

    /**
     * @return The advanceWidth as an unsigned short value
     */
    public int getAdvanceWidth(int glyphIndex) {
        ByteBuffer hmtx = getTableDataUnsafe("hmtx");
        int numberOfHMetrics = getTableValue(HheaTable.NUM_OF_LONG_HOR_METRICS);

        if (glyphIndex < numberOfHMetrics) {
            return hmtx.getShort(glyphIndex * 4) & 0xFFFF;
        } else {
            int lastAdvanceWidth = hmtx.getShort((numberOfHMetrics - 1) * 4) & 0xFFFF;
            return lastAdvanceWidth;
        }
    }


    /**
     * Gets a value from a corresponding table.
     *
     * @implNote This method is <b>unsafe</b> if not used with required tables.
     * @see FontTableEntry
     * @see HheaTable
     * @see HeadTable
     * @see MaxpTable
     * @see #getTableDataUnsafe(String)
     */
    public int getTableValue(FontTableEntry val) {
        ByteBuffer table = getTableDataUnsafe(val.getTable());
        table.position(val.getPosition());
        switch (val.getBytes()) {
            case 1 -> {
                return table.get() & 0xFF;
            }
            case 2 -> {
                if (val.isSigned()) {
                    return table.getShort();
                } else {
                    return table.getShort() & 0xFFFF;
                }
            }
            case 4 -> {
                if (val.isSigned()) {
                    return table.getInt();
                } else {
                    return (int) (table.getInt() & 0xFFFFFFFFL);
                }
            }
            default -> throw new RuntimeException("unsupported length");
        }
    }

    public record TableEntry(String tag, int checkSum, int offset, int length) {
    }
}
