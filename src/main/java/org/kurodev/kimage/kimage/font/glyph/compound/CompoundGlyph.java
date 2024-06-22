package org.kurodev.kimage.kimage.font.glyph.compound;

import org.kurodev.kimage.kimage.font.glyph.FontGlyph;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import java.util.List;

public class CompoundGlyph implements FontGlyph {
    private final List<GlyphWithFlags> components;
    private final char character;
    private final int advanceWidth;
    private final int xMin;
    private final int xMax;
    private final int yMin;
    private final int yMax;


    public CompoundGlyph(char character, int advanceWidth, int xMin, int yMin, int xMax, int yMax, List<GlyphWithFlags> components) {
        this.components = components;
        this.character = character;
        this.advanceWidth = advanceWidth;
        this.xMin = xMin;
        this.xMax = xMax;
        //this is necessary to ensure the glyph is drawn upright.
        this.yMin = -yMax;
        this.yMax = yMin;
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

    public List<GlyphWithFlags> getComponents() {
        return components;
    }

    @Override
    public int getYMax() {
        return yMax;
    }

    @Override
    public int getXMax() {
        return xMax;
    }

    @Override
    public int getYMin() {
        return yMin;
    }

    @Override
    public int getXMin() {
        return xMin;
    }
}
