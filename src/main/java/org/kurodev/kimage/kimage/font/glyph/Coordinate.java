package org.kurodev.kimage.kimage.font.glyph;

import java.util.Collections;
import java.util.List;

public record Coordinate(int x, int y, List<GlyphFlag> flags) {
    public Coordinate(int x, int y) {
        this(x, y, Collections.emptyList());
    }
}
