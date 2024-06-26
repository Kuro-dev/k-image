package org.kurodev.kimage.kimage.font.table;

import org.kurodev.kimage.kimage.font.enums.CmapEncodingID;
import org.kurodev.kimage.kimage.font.enums.CmapPlatformID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

/**
 * This class is NOT thread safe
 */
public class CmapTable {
    private static final Logger logger = LoggerFactory.getLogger(CmapTable.class);
    private ByteBuffer cmapBuffer;
    private int unicodeCmapOffset = -1;

    public CmapTable(ByteBuffer cmapBuffer) {
        this.cmapBuffer = cmapBuffer;
        findUnicodeCmapSubtable();
    }

    private void findUnicodeCmapSubtable() {
        int version = cmapBuffer.getShort() & 0xFFFF;
        logger.trace("Found Unicode Cmap Subtable version {}", version);
        int numTables = cmapBuffer.getShort() & 0xFFFF;
        logger.trace("Found {} tables", numTables);

        tryFindEncoding(numTables, CmapEncodingID.UNICODE_2_0_FULL_REPERTIORE, CmapPlatformID.UNICODE, CmapPlatformID.MICROSOFT);
        tryFindEncoding(numTables, CmapEncodingID.UNICODE_FULL_REPERTIORE, CmapPlatformID.UNICODE, CmapPlatformID.MICROSOFT);
        tryFindEncoding(numTables, CmapEncodingID.UNICODE_BMP, CmapPlatformID.UNICODE, CmapPlatformID.MICROSOFT);
        tryFindEncoding(numTables, CmapEncodingID.UNICODE_2_0_BMP, CmapPlatformID.UNICODE, CmapPlatformID.MICROSOFT);
        if (unicodeCmapOffset == -1) {
            logger.error("Suitable cmap subtable not found");
            throw new IllegalStateException("Suitable cmap subtable not found");
        }

    }

    private void tryFindEncoding(int numTables, CmapEncodingID search, CmapPlatformID platform, CmapPlatformID... platforms) {
        if (unicodeCmapOffset != -1) {
            return;
        }
        int bufferPosition = cmapBuffer.position();
        for (int i = 0; i < numTables; i++) {
            int platformID = cmapBuffer.getShort() & 0xFFFF;
            int encodingID = cmapBuffer.getShort() & 0xFFFF;
            int offset = cmapBuffer.getInt();
            int pos = cmapBuffer.position();
            int format = getFormat(offset);
            logger.trace("platformID: {}, encodingID: {}, offset: {}, format: {}", platformID, encodingID, offset, format);
            cmapBuffer.position(pos);
            CmapEncodingID encoding = CmapEncodingID.fromValue(encodingID);
            CmapPlatformID encodingPlatform = CmapPlatformID.fromValue(platformID);
            if (List.of(platform, platforms).contains(encodingPlatform) && encoding == search) {
                unicodeCmapOffset = offset;
                logger.trace("encoding: {}", encoding);
                logger.trace("Found suitable cmap subtable with offset {} and format {}", offset, format);
                return;
            }
        }
        cmapBuffer.position(bufferPosition);
    }

    public int getGlyphIndex(char character) {
        int format = getFormat(unicodeCmapOffset);
        logger.trace("cmap format: {}", format);

        switch (format) {
            case 4:
                return getGlyphIndexFormat4(character);
            case 12:
                return getGlyphIndexFormat12(character);
            default:
                logger.error("Unsupported cmap format {}", format);
                return -1;
        }
    }

    private int getGlyphIndexFormat4(char character) {
        int length = cmapBuffer.getShort(); // length
        int language = cmapBuffer.getShort(); // language
        int segCountX2 = cmapBuffer.getShort();
        int segCount = segCountX2 / 2;
        int searchRange = cmapBuffer.getShort(); // searchRange
        int entrySelector = cmapBuffer.getShort(); // entrySelector
        int rangeShift = cmapBuffer.getShort(); // rangeShift

        logger.trace("length: {}, language: {}, segCount: {}, searchRange: {}, entrySelector: {}, rangeShift: {}",
                length, language, segCount, searchRange, entrySelector, rangeShift);

        int[] endCode = new int[segCount];
        for (int i = 0; i < segCount; i++) {
            endCode[i] = cmapBuffer.getShort();
        }

         cmapBuffer.getShort(); // reservedPad, just ignore it, it's not used as of now.

        int[] startCode = new int[segCount];
        int[] idDelta = new int[segCount];
        int[] idRangeOffset = new int[segCount];
        for (int i = 0; i < segCount; i++) {
            startCode[i] = cmapBuffer.getShort();
        }
        for (int i = 0; i < segCount; i++) {
            idDelta[i] = cmapBuffer.getShort();
        }
        for (int i = 0; i < segCount; i++) {
            idRangeOffset[i] = cmapBuffer.getShort();
        }

        for (int i = 0; i < segCount; i++) {
            if (character >= startCode[i] && character <= endCode[i]) {
                int index;
                if (idRangeOffset[i] == 0) {
                    index = (character + idDelta[i]) & 0xFFFF;
                } else {
                    int glyphIndexOffset = (idRangeOffset[i] / 2 + (character - startCode[i]) - (segCount - i));
                    int glyphPosition = unicodeCmapOffset + 14 + segCount * 8 + glyphIndexOffset * 2;
                    cmapBuffer.position(glyphPosition);
                    index = cmapBuffer.getShort() & 0xFFFF;
                    if (index != 0) { // Ensure it's not the missing glyph
                        index = (index + idDelta[i]) & 0xFFFF;
                    }
                    logger.trace("Character '{}' found in segment {}: glyphIndexOffset: {}, glyphPosition: {}, index: {}",
                            character, i, glyphIndexOffset, glyphPosition, index);
                }
                logger.trace("Character '{}' -> Glyph Index: {}", character, index);
                return index;
            }
        }

        return 0; // not found, returning .notdef index
    }

    private int getGlyphIndexFormat12(char character) {
        int reserved = cmapBuffer.getShort(); // reserved
        int length = cmapBuffer.getInt(); // length
        int language = cmapBuffer.getInt(); // language
        int numGroups = cmapBuffer.getInt(); // numGroups

        logger.trace("reserved: {}, length: {}, language: {}, numGroups: {}", reserved, length, language, numGroups);

        // Read each group
        for (int i = 0; i < numGroups; i++) {
            int startCharCode = cmapBuffer.getInt();
            int endCharCode = cmapBuffer.getInt();
            int startGlyphID = cmapBuffer.getInt();

            logger.trace("Group {}: startCharCode: {}, endCharCode: {}, startGlyphID: {}", i, startCharCode, endCharCode, startGlyphID);

            if (character >= startCharCode && character <= endCharCode) {
                int glyphIndex = startGlyphID + (character - startCharCode);
                logger.trace("Character '{}' found in group {}: glyphIndex: {}", character, i, glyphIndex);
                return glyphIndex;
            }
        }

        logger.trace("Character '{}' not found, returning .notdef index", character);
        return 0; // not found, returning .notdef index
    }

    private int getFormat(int offset) {
        cmapBuffer.position(offset);
        return cmapBuffer.getShort() & 0xFFFF;
    }

    public Optional<Character> getCharacter(int glyphIndex) {
        int format = getFormat(unicodeCmapOffset);

        switch (format) {
            case 4:
                return getCharacterFormat4(glyphIndex);
            case 12:
                return getCharacterFormat12(glyphIndex);
            default:
                logger.error("Unsupported cmap format {}", format);
                return Optional.empty();
        }
    }

    private Optional<Character> getCharacterFormat4(int glyphIndex) {
        int length = cmapBuffer.getShort(); // length
        int language = cmapBuffer.getShort(); // language
        int segCountX2 = cmapBuffer.getShort();
        int segCount = segCountX2 / 2;
        int searchRange = cmapBuffer.getShort(); // searchRange
        int entrySelector = cmapBuffer.getShort(); // entrySelector
        int rangeShift = cmapBuffer.getShort(); // rangeShift

        logger.trace("length: {}, language: {}, segCount: {}, searchRange: {}, entrySelector: {}, rangeShift: {}",
                length, language, segCount, searchRange, entrySelector, rangeShift);

        int[] endCode = new int[segCount];
        for (int i = 0; i < segCount; i++) {
            endCode[i] = cmapBuffer.getShort();
        }

       cmapBuffer.getShort(); // reservedPad

        int[] startCode = new int[segCount];
        int[] idDelta = new int[segCount];
        int[] idRangeOffset = new int[segCount];
        for (int i = 0; i < segCount; i++) {
            startCode[i] = cmapBuffer.getShort();
        }
        for (int i = 0; i < segCount; i++) {
            idDelta[i] = cmapBuffer.getShort();
        }
        for (int i = 0; i < segCount; i++) {
            idRangeOffset[i] = cmapBuffer.getShort();
        }

        for (int i = 0; i < segCount; i++) {
            if (glyphIndex >= idDelta[i] && glyphIndex <= idDelta[i] + endCode[i] - startCode[i]) {
                int glyphIdArrayIndex = idRangeOffset[i] / 2 + (glyphIndex - idDelta[i]) - (segCount - i);
                int charCode = startCode[i] + glyphIdArrayIndex;
                logger.trace("Glyph index '{}' corresponds to character '{}'", glyphIndex, (char) charCode);
                return Optional.of((char) charCode);
            }
        }

        logger.trace("Glyph index '{}' does not correspond to any character", glyphIndex);
        return Optional.empty();
    }

    private Optional<Character> getCharacterFormat12(int glyphIndex) {
        int reserved = cmapBuffer.getShort(); // reserved
        int length = cmapBuffer.getInt(); // length
        int language = cmapBuffer.getInt(); // language
        int numGroups = cmapBuffer.getInt(); // numGroups

        logger.trace("reserved: {}, length: {}, language: {}, numGroups: {}", reserved, length, language, numGroups);

        // Read each group
        for (int i = 0; i < numGroups; i++) {
            int startCharCode = cmapBuffer.getInt();
            int endCharCode = cmapBuffer.getInt();
            int startGlyphID = cmapBuffer.getInt();

            logger.trace("Group {}: startCharCode: {}, endCharCode: {}, startGlyphID: {}", i, startCharCode, endCharCode, startGlyphID);

            if (glyphIndex >= startGlyphID && glyphIndex <= startGlyphID + (endCharCode - startCharCode)) {
                int charCode = startCharCode + (glyphIndex - startGlyphID);
                logger.trace("Glyph index '{}' corresponds to character '{}'", glyphIndex, (char) charCode);
                return Optional.of((char) charCode);
            }
        }

        logger.trace("Glyph index '{}' does not correspond to any character", glyphIndex);
        return Optional.empty();
    }

}

