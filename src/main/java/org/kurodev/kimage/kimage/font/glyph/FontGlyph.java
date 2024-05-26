package org.kurodev.kimage.kimage.font.glyph;

public interface FontGlyph {

    char getCharacter();

    /**
     * @return a 2D array representing the contours and the different (absolute) points in them
     */
    Coordinate[][] getCoordinates();

    int getNumberOfContours();

    int[] getEndPtsOfContours();

    int getAdvanceWidth();

    int getxMin();

    int getyMin();

    int getxMax();

    int getyMax();
}
