package org.kurodev.kimage.kimage.font.glyph.compound;

import org.kurodev.kimage.kimage.font.glyph.FontGlyph;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import java.util.List;

public class CompoundGlyph implements FontGlyph {
    private final List<GlyphWithFlags> glyphs;
    private final char character;
    private final int advanceWidth;

    public CompoundGlyph(char character, int advanceWidth, List<GlyphWithFlags> glyphs) {
        this.glyphs = glyphs;
        this.character = character;
        this.advanceWidth = advanceWidth;
    }

    @Override
    public char getCharacter() {
        return character;
    }

    @Override
    public Coordinate[][] getCoordinates() {
        return new Coordinate[0][];
    }

    @Override
    public int getAdvanceWidth() {
        return advanceWidth;
    }
}