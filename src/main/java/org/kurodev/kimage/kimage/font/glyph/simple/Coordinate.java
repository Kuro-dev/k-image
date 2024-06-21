package org.kurodev.kimage.kimage.font.glyph.simple;

import org.kurodev.kimage.kimage.font.enums.Transformation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record Coordinate(int x, int y, List<GlyphFlag> flags) {
    public Coordinate(int x, int y) {
        this(x, y, Collections.emptyList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public String toString() {
        return "[" + x + "|" + y + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int distance(Coordinate other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    public Coordinate transform(Transformation transformation, double... params) {
        return transformation.transform(this, params);
    }
}
