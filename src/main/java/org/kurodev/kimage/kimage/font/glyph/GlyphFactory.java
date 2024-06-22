package org.kurodev.kimage.kimage.font.glyph;

import org.kurodev.kimage.kimage.font.KFont;
import org.kurodev.kimage.kimage.font.glyph.compound.*;
import org.kurodev.kimage.kimage.font.glyph.simple.SimpleFontGlyph;
import org.kurodev.kimage.kimage.font.glyph.simple.SimpleFontGlyphBuilder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GlyphFactory {


    public static SimpleFontGlyph readSimpleGlyph(ByteBuffer glyph, short numberOfContours, char character, int advanceWidth) {
        short xMin = glyph.getShort();
        short yMin = glyph.getShort();
        short xMax = glyph.getShort();
        short yMax = glyph.getShort();

        int[] endPtsOfContours = new int[numberOfContours];
        for (int i = 0; i < numberOfContours; i++) {
            endPtsOfContours[i] = glyph.getShort() & 0xFFFF;
        }

        int instructionLength = glyph.getShort() & 0xFFFF;
        byte[] instructions = new byte[instructionLength];
        glyph.get(instructions);

        int numPoints = (numberOfContours == 0) ? 0 : (endPtsOfContours[numberOfContours - 1] + 1);
        byte[] flags = new byte[numPoints];
        int flagCount = 0;
        while (flagCount < numPoints) {
            byte flag = glyph.get();
            flags[flagCount++] = flag;
            if ((flag & 8) != 0) {
                int repeatCount = glyph.get() & 0xFF;
                for (int i = 0; i < repeatCount; i++) {
                    flags[flagCount++] = flag;
                }
            }
        }

        int[] xCoordinates = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            if ((flags[i] & 2) != 0) {
                int value = glyph.get() & 0xFF;
                xCoordinates[i] = (flags[i] & 16) != 0 ? value : -value;
            } else if ((flags[i] & 16) == 0) {
                xCoordinates[i] = glyph.getShort();
            }
        }

        int[] yCoordinates = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            if ((flags[i] & 4) != 0) {
                int value = glyph.get() & 0xFF;
                yCoordinates[i] = (flags[i] & 32) != 0 ? value : -value;
            } else if ((flags[i] & 32) == 0) {
                yCoordinates[i] = glyph.getShort();
            }
        }

        return new SimpleFontGlyphBuilder()
                .setNumberOfContours(numberOfContours)
                .setxMin(xMin)
                .setyMin(yMin)
                .setxMax(xMax)
                .setyMax(yMax)
                .setEndPtsOfContours(endPtsOfContours)
                .setxCoordinates(xCoordinates)
                .setyCoordinates(yCoordinates)
                .setFlags(flags)
                .setInstructions(yCoordinates)
                .setCharacter(character)
                .setAdvanceWidth(advanceWidth)
                .createSimpleFontGlyph();
    }

    public static CompoundGlyph readCompoundGlyph(ByteBuffer glyph, char character, int advanceWidth, KFont font) {
        int xMin = glyph.getShort();
        int yMin = glyph.getShort();
        int xMax = glyph.getShort();
        int yMax = glyph.getShort();
        List<GlyphWithFlags> glyphs = new ArrayList<>();
        do {
            glyphs.add(readCompoundSimpleGlyph(glyph, font));
        } while (glyphs.getLast().contains(CompoundGlyphFlag.MORE_COMPONENTS));
        return new CompoundGlyph(character, advanceWidth, xMin, yMin, xMax, yMax, glyphs);
    }

    public static FontGlyph createWhitespace(char character, int advanceWidth) {
        return new SimpleFontGlyphBuilder()
                .setNumberOfContours(0)
                .setxMin(0)
                .setyMin(0)
                .setxMax(0)
                .setyMax(0)
                .setCharacter(character)
                .setAdvanceWidth(advanceWidth)
                .createSimpleFontGlyph();
    }

    private static GlyphWithFlags readCompoundSimpleGlyph(ByteBuffer glyphBuffer, KFont font) {
        int bitmapFlags = glyphBuffer.getShort() & 0xFFFF;
        int glyphIndex = glyphBuffer.getShort() & 0xFFFF;
        List<CompoundGlyphFlag> flags = CompoundGlyphFlag.identify(bitmapFlags);
        SimpleFontGlyph glyph = (SimpleFontGlyph) font.getGlyph(glyphIndex);
//        assert !flags.contains(CompoundGlyphFlag.OBSOLETE) : "obsolete flag should never be set";
        GlyphWithFlagsBuilder builder = new GlyphWithFlagsBuilder();
        int arg1, arg2;
        if (flags.contains(CompoundGlyphFlag.ARG_1_AND_2_ARE_WORDS)) {
            arg1 = glyphBuffer.getShort() & 0xFFFF;
            arg2 = glyphBuffer.getShort() & 0xFFFF;
        } else {
            arg1 = glyphBuffer.get() & 0xFF;
            arg2 = glyphBuffer.get() & 0xFF;
        }
        if (flags.contains(CompoundGlyphFlag.ARGS_ARE_XY_VALUES)) {
            //arg 1 and 2 are direct coordinates
            builder.setFlags(new FlagsWithParams(flags, arg1, arg2));
        } else {
            //arg1 and arg2 are indizes
            int x = glyph.getxCoordinates()[arg1];
            int y = glyph.getyCoordinates()[arg2];
            builder.setFlags(new FlagsWithParams(flags, x, y));
        }
        builder.setGlyph(glyph);
        return builder.createGlyphWithFlags();
    }
}
