package org.kurodev.kimage.kimage.font.glyph.compound;

import org.kurodev.kimage.kimage.font.glyph.simple.GlyphFlag;

import java.util.List;

public record FlagsWithParams(List<CompoundGlyphFlag> flags, int... params) {
    public boolean contains(CompoundGlyphFlag flag) {
        return flags.contains(flag);
    }
}
