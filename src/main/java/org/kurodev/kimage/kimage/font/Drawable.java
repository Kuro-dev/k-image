package org.kurodev.kimage.kimage.font;

import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

import java.awt.*;

public interface Drawable {
    Drawable drawPixel(int x, int y, Color color);

    default Drawable drawPixel(Coordinate point, Color color) {
        return drawPixel(point.x(), point.y(), color);
    }

}
