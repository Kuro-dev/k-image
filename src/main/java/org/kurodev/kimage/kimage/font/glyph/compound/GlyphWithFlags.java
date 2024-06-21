package org.kurodev.kimage.kimage.font.glyph.compound;

import org.kurodev.kimage.kimage.font.glyph.FontGlyph;

import java.util.List;

public record GlyphWithFlags(FontGlyph glyph, List<CompoundGlyphFlag> flags) {
}
