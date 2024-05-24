package org.kurodev.kimage.kimage.font.glyph;

import java.util.List;

public record Coordinate(int x, int y, List<GlyphFlag> flags) {
}
