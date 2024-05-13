package org.kurodev.kimage.kimage.font;

import org.kurodev.kimage.kimage.font.enums.FontTableEntry;
import org.kurodev.kimage.kimage.font.enums.HeadTable;
import org.kurodev.kimage.kimage.font.enums.HheaTable;
import org.kurodev.kimage.kimage.font.enums.MaxpTable;
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
    private int getGlyphIndex(char character) {
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

    public SimpleFontGlyph getGlyph(char character) {
        int glyphIndex = getGlyphIndex(character);
        return getGlyph(glyphIndex);
    }

    //TODO redo all of this, it does not work reliably
    private SimpleFontGlyph getGlyph(int glyphIndex) {
        ByteBuffer glyf = getTableDataUnsafe("glyf");
        ByteBuffer loca = getTableDataUnsafe("loca");
        loca.position(glyphIndex * 2); // short format offset, multiply by 2
        int glyphOffset = loca.getShort() * 2; // actual offset in the glyf table
        glyf.position(glyphOffset);
        short numberOfContours = glyf.getShort();
        if (numberOfContours >= 0) {
            var out = SimpleFontGlyph.readGlyph(glyf, numberOfContours);
            out.setAdvanceWidth(getAdvanceWidth(glyphIndex));
            out.setIndex(glyphIndex);
            return out;
        }
        logger.warn("Compound glyphs aren't supported yet");
        return null;
    }

    /**
     * @return The advanceWidth as an unsigned short value
     */
    private Integer getAdvanceWidth(int glyphIndex) {
        ByteBuffer hmtx = getTableDataUnsafe("hmtx");
        int numGlyphs = getTableValue(MaxpTable.NUM_OF_GLYPHS);

        if (glyphIndex >= numGlyphs) {
            throw new IndexOutOfBoundsException("Glyph index " + glyphIndex + " is out of bounds. Max: " + numGlyphs);
        }

        int numberOfLongHorMetrics = getTableValue(HheaTable.NUM_OF_LONG_HOR_METRICS);
        int advanceWidthPosition;

        if (glyphIndex < numberOfLongHorMetrics) {
            // If the glyph index is within the range of numberOfLongHorMetrics, use its own width
            advanceWidthPosition = glyphIndex * 2;
        } else {
            // If the glyph index is beyond the range, all glyphs use the last width
            advanceWidthPosition = (numberOfLongHorMetrics - 1) * 2;
        }

        hmtx.position(advanceWidthPosition);
        int advanceWidth = hmtx.getShort() & 0xFFFF;

        return advanceWidth;
    }


    /**
     * Gets a value from a corresponding table.
     *
     * @see FontTableEntry
     * @see HheaTable
     * @see HeadTable
     * @see MaxpTable
     *
     * @implNote This method is <b>unsafe</b> if not used with required tables.
     * @see #getTableDataUnsafe(String)
     */
    private int getTableValue(FontTableEntry val) {
        ByteBuffer hhea = getTableDataUnsafe(val.getTable());
        hhea.position(val.getPosition());
        if (val.isSigned()) {
            return hhea.getShort();
        } else {
            return hhea.getShort() & 0xFFFF;
        }
    }


    public record TableEntry(String tag, int checkSum, int offset, int length) {
    }
}
