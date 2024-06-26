package org.kurodev.kimage.kimage.font.glyph;

import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;

public interface FontGlyph {

    char getCharacter();

    /**
     * @return a 2D array representing the contours and the different (absolute) points in them
     */
    Coordinate[][] getCoordinates();

    int getXMin();
    int getXMax();
    int getYMin();
    int getYMax();

    int getAdvanceWidth();


}
