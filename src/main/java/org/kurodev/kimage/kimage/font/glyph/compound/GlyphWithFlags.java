package org.kurodev.kimage.kimage.font.glyph.compound;

import org.kurodev.kimage.kimage.font.glyph.simple.SimpleFontGlyph;

import java.util.Objects;

public final class GlyphWithFlags {
    private final SimpleFontGlyph glyph;
    private final FlagsWithParams flags;

    public GlyphWithFlags(SimpleFontGlyph glyph, FlagsWithParams flags) {
        this.glyph = glyph;
        this.flags = flags;
    }

    public boolean contains(CompoundGlyphFlag flag) {
        return flags.flags().contains(flag);
    }

    public SimpleFontGlyph glyph() {
        return glyph;
    }

    public FlagsWithParams flags() {
        return flags;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GlyphWithFlags) obj;
        return Objects.equals(this.glyph, that.glyph) &&
                Objects.equals(this.flags, that.flags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(glyph, flags);
    }

    @Override
    public String toString() {
        return "GlyphWithFlags[" +
                "glyph=" + glyph + ", " +
                "flags=" + flags + ']';
    }

}


