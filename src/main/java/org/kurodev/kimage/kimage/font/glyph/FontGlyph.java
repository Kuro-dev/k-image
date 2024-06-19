package org.kurodev.kimage.kimage.font.glyph;

import java.util.List;

public interface FontGlyph {

    char getCharacter();

    List<Coordinate> getCoordinates();

    int getNumberOfContours();

    int[] getEndPtsOfContours();

    int getX(int index);

    int getY(int index);

    int getAdvanceWidth();

    int getxMin();

    int getyMin();

    int getxMax();

    int getyMax();
}
