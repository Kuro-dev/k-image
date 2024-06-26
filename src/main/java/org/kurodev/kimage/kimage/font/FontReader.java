package org.kurodev.kimage.kimage.font;

import org.kurodev.kimage.kimage.draw.KImage;
import org.kurodev.kimage.kimage.font.enums.FontTableEntry;
import org.kurodev.kimage.kimage.font.enums.HeadTable;
import org.kurodev.kimage.kimage.font.enums.HheaTable;
import org.kurodev.kimage.kimage.font.enums.MaxpTable;
import org.kurodev.kimage.kimage.font.glyph.FontGlyph;
import org.kurodev.kimage.kimage.font.glyph.FontStyle;
import org.kurodev.kimage.kimage.font.glyph.GlyphFactory;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;
import org.kurodev.kimage.kimage.util.ContourHorizontalIntersects;
import org.kurodev.kimage.kimage.util.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

public class FontReader implements KFont {
    private static final Logger logger = LoggerFactory.getLogger(FontReader.class);
    private byte[] data;
    private int sfntVersion;
    private int numTables;
    private int searchRange;
    private int entrySelector;
    private int rangeShift;
    private TableEntry[] tableEntries;

    public void load(InputStream in) throws IOException {
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
        logger.debug("loading tables: {}", numTables);
        Arrays.stream(tableEntries).forEach(entry -> logger.trace("{}", entry));
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
    ByteBuffer getTableDataUnsafe(String tag) {
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
        return CmapTable.fromFontReader(this).getGlyphIndex(character);
    }


    public FontGlyph getGlyph(String character) {
        if (character.length() != 1) {
            throw new IllegalArgumentException("Can only identify one glyph at the time");
        }
        return getGlyph(character.charAt(0));
    }


    @Override
    public int getLowestRecommendedPPEM() {
        return getTableValue(HeadTable.LOWEST_RECOMMENDED_PPEM);
    }

    public FontGlyph getGlyph(char character) {
        long start = System.currentTimeMillis();
        CmapTable cmapTable = CmapTable.fromFontReader(this);
        int glyphIndex = cmapTable.getGlyphIndex(character);
        ByteBuffer glyf = getTableDataUnsafe("glyf");
        ByteBuffer loca = getTableDataUnsafe("loca");

        int indexToLocFormat = getTableValue(HeadTable.INDEX_TO_LOC_FORMAT);
        int glyphOffset;
        int nextGlyphOffset;

        if (indexToLocFormat == 0) {
            //The actual local offset divided by 2 is stored.
            glyphOffset = loca.getShort(glyphIndex * Short.BYTES) * 2;
            nextGlyphOffset = loca.getShort((glyphIndex + 1) * 2) * 2;
        } else {
            glyphOffset = loca.getInt(glyphIndex * Integer.BYTES);
            nextGlyphOffset = loca.getInt((glyphIndex + 1) * 4);
        }

        glyf.position(glyphOffset);
        return loadGlyph(glyphIndex, glyf, glyphOffset, nextGlyphOffset, character, start);
    }

    @Override
    public FontGlyph getGlyph(int glyphIndex) {
        long start = System.currentTimeMillis();
        CmapTable cmap = CmapTable.fromFontReader(this);
        ByteBuffer glyf = getTableDataUnsafe("glyf");
        ByteBuffer loca = getTableDataUnsafe("loca");
        char character = cmap.getCharacter(glyphIndex).orElse(' ');
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
        return loadGlyph(glyphIndex, glyf, glyphOffset, nextGlyphOffset, character, start);
    }

    private FontGlyph loadGlyph(int glyphIndex, ByteBuffer glyf, int glyphOffset, int nextGlyphOffset, char character, long start) {
        glyf.position(glyphOffset);
        short numberOfContours = glyf.getShort();
        FontGlyph out;
        String type; //just a marker for the log messages
        if (glyphOffset == nextGlyphOffset) {
            out = GlyphFactory.createWhitespace(character, getAdvanceWidth(glyphIndex)); // This glyph has no outline data.
            type = "SimpleGlyph  ";
        } else if (numberOfContours < 0) {
            out = GlyphFactory.readCompoundGlyph(glyf, character, getAdvanceWidth(glyphIndex), this);
            type = "CompoundGlyph";
        } else {
            out = GlyphFactory.readSimpleGlyph(glyf, numberOfContours, character, getAdvanceWidth(glyphIndex));
            type = "SimpleGlyph  ";
        }
        long end = System.currentTimeMillis();
        logger.debug("Loading {} '{}' (index: {}) took {}ms", type, character, glyphIndex, end - start);
        return out;
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
            return hmtx.getShort((numberOfHMetrics - 1) * 4) & 0xFFFF;
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

    @Override
    public void drawString(Drawable drawable, int x, int y, int fontSize, Color color, String str, FontStyle... styles) {
        int lowestPPEM = this.getLowestRecommendedPPEM();
        if (fontSize < lowestPPEM) {
            logger.debug("Provided fontSize {} pixels is less than the lowest recommended height {} pixels." +
                    " This may result in poor rendering quality.", fontSize, lowestPPEM);
        }
        if (fontSize % lowestPPEM != 0) {
            int lowerRecommendation = ((int) Math.floor(((double) fontSize / lowestPPEM)) * lowestPPEM);
            int higherRecommendation = ((int) Math.ceil(((double) fontSize / lowestPPEM)) * lowestPPEM);
            logger.debug("fontsize is not a multiple of the lowest PPEM, and might look wrong. " +
                    "Recommended alternative sizes to {}: {} or {}", fontSize, lowerRecommendation, higherRecommendation);
            logger.debug("Enforcing fontsize: {}px", lowerRecommendation);
            fontSize = lowerRecommendation;
        }
        int maxHeight = this.getTableValue(HeadTable.Y_MAX) - this.getTableValue(HeadTable.Y_MIN);
        if (maxHeight == 0) {
            logger.debug("Attempted to draw only whitespace characters");
            return;
        }
        int originalX = x;
        // Calculate the scale factor based on the target height
        double scale = (double) fontSize / maxHeight;
        for (int i = 0; i < str.length(); i++) {
            char character = str.charAt(i);

            if (character == '\n') {
                y += (int) (maxHeight * scale);
                x = originalX;
                continue;
            }
            FontGlyph glyph = this.getGlyph(character);
            drawGlyph(drawable, x, y, glyph, color, scale);
            for (FontStyle style : styles) {
                style.apply(x, y, scale, glyph, drawable, this, color);
            }
            int nextX = (int) Math.ceil(glyph.getAdvanceWidth() * scale);
            x += nextX;
        }
    }

    private void drawGlyph(Drawable drawable, int x, int y, FontGlyph glyph, Color color, double scale) {
        Coordinate[][] glyphCoords = glyph.getCoordinates();
        Coordinate[][] scaledCoords = Arrays.stream(glyphCoords).map(
                contour -> Arrays.stream(contour).map(
                        coord -> Transformation.SCALE.transform(coord, scale, scale)
                ).toArray(Coordinate[]::new)
        ).toArray(Coordinate[][]::new);
        var intersectionSegments = ContourHorizontalIntersects.makeFromContour(scaledCoords);
        intersectionSegments.drawPixels(drawable, x, y, color);
    }

    public record TableEntry(String tag, int checkSum, int offset, int length) {
    }
}
