package org.kurodev.kimage.kimage.font.glyph;

import org.kurodev.kimage.kimage.font.enums.GlyphFlag;

import java.util.List;

public record Coordinate(short x, short y, List<GlyphFlag> flags) {
}
