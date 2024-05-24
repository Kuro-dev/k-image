package org.kurodev.kimage.kimage.font.glyph;

public class SimpleFontGlyphBuilder {
    private char character;
    private int numberOfContours;
    private int xMin;
    private int yMin;
    private int xMax;
    private int yMax;
    private int[] xCoordinates;
    private int[] yCoordinates;
    private byte[] flags;
    private int[] instructions;
    private int[] endPtsOfContours;
    private int advanceWidth;

    public SimpleFontGlyphBuilder setCharacter(char character) {
        this.character = character;
        return this;
    }

    public SimpleFontGlyphBuilder setNumberOfContours(int numberOfContours) {
        this.numberOfContours = numberOfContours;
        return this;
    }

    public SimpleFontGlyphBuilder setxMin(int xMin) {
        this.xMin = xMin;
        return this;
    }

    public SimpleFontGlyphBuilder setyMin(int yMin) {
        this.yMin = yMin;
        return this;
    }

    public SimpleFontGlyphBuilder setxMax(int xMax) {
        this.xMax = xMax;
        return this;
    }

    public SimpleFontGlyphBuilder setyMax(int yMax) {
        this.yMax = yMax;
        return this;
    }

    public SimpleFontGlyphBuilder setxCoordinates(int[] xCoordinates) {
        this.xCoordinates = xCoordinates;
        return this;
    }

    public SimpleFontGlyphBuilder setyCoordinates(int[] yCoordinates) {
        this.yCoordinates = yCoordinates;
        return this;
    }

    public SimpleFontGlyphBuilder setFlags(byte[] flags) {
        this.flags = flags;
        return this;
    }

    public SimpleFontGlyphBuilder setInstructions(int[] instructions) {
        this.instructions = instructions;
        return this;
    }

    public SimpleFontGlyphBuilder setEndPtsOfContours(int[] endPtsOfContours) {
        this.endPtsOfContours = endPtsOfContours;
        return this;
    }
    public SimpleFontGlyphBuilder setAdvanceWidth(int advanceWidth) {
        this.advanceWidth = advanceWidth;
        return this;
    }

    public SimpleFontGlyph createSimpleFontGlyph() {
        return new SimpleFontGlyph(character, numberOfContours, xMin, yMin, xMax, yMax, xCoordinates, yCoordinates, flags, instructions, endPtsOfContours, advanceWidth);
    }
}