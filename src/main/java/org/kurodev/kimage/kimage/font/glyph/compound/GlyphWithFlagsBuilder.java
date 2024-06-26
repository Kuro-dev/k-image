package org.kurodev.kimage.kimage.font.glyph.compound;

import org.kurodev.kimage.kimage.font.glyph.simple.SimpleFontGlyph;

import java.util.List;

public class GlyphWithFlagsBuilder {
    private SimpleFontGlyph glyph;
    private FlagsWithParams flags;

    public GlyphWithFlagsBuilder setGlyph(SimpleFontGlyph glyph) {
        this.glyph = glyph;
        return this;
    }

    public GlyphWithFlagsBuilder setFlags(FlagsWithParams flags) {
        this.flags = flags;
        return this;
    }

    public GlyphWithFlags createGlyphWithFlags() {
        return new GlyphWithFlags(glyph, flags);
    }
}