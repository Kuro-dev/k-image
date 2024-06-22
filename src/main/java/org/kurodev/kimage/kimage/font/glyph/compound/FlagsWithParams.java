package org.kurodev.kimage.kimage.font.glyph.compound;

import java.util.List;

public record FlagsWithParams(List<CompoundGlyphFlag> flags, int... params) {
}
