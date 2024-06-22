package org.kurodev.kimage.kimage.font.glyph.compound;

import org.kurodev.kimage.kimage.font.glyph.FontGlyph;
import org.kurodev.kimage.kimage.font.glyph.simple.Coordinate;
import org.kurodev.kimage.kimage.util.Transformation;
import org.kurodev.kimage.kimage.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompoundGlyph implements FontGlyph {
    /**
     * The numbers stored as shorts are treated as signed fixed binary point numbers
     * with one bit to the left of the binary point and 14 to the right.
     */
    private static final double FIXED_POINT_SCALE = 0b100_0000_0000_0000;
    private static final Logger logger = LoggerFactory.getLogger(CompoundGlyph.class);

    private final List<GlyphWithFlags> components;
    private final char character;
    private final int advanceWidth;
    private final int xMin;
    private final int xMax;
    private final int yMin;
    private final int yMax;


    public CompoundGlyph(char character, int advanceWidth, int xMin, int yMin, int xMax, int yMax, List<GlyphWithFlags> components) {
        this.components = components;
        this.character = character;
        this.advanceWidth = advanceWidth;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    @Override
    public char getCharacter() {
        return character;
    }

    @Override
    public Coordinate[][] getCoordinates() {
        // Transform all component glyphs' coordinates
        List<Coordinate[]> transformedCoords = new ArrayList<>();
        for (GlyphWithFlags component : components) {
            Coordinate[][] transformed = transformCoordinates(component);
            transformedCoords.addAll(Arrays.asList(transformed));
        }
        return transformedCoords.toArray(new Coordinate[0][]);
    }

    private Coordinate[][] transformCoordinates(GlyphWithFlags component) {
        Coordinate[][] coordinates = component.glyph().getCoordinates();
        FlagsWithParams flagsWithParams = component.flags();
        Coordinate[][] out = new Coordinate[coordinates.length][];
        for (int contour = 0; contour < coordinates.length; contour++) {
            out[contour] = new Coordinate[coordinates[contour].length];
            for (int i = 0; i < coordinates[contour].length; i++) {
                Coordinate coord = coordinates[contour][i];
                int[] params = flagsWithParams.params();
                if (flagsWithParams.contains(CompoundGlyphFlag.WE_HAVE_A_SCALE)) {
                    double scale = params[0] / FIXED_POINT_SCALE;
                    coord = Transformation.SCALE.transform(coord, scale, scale);
                } else if (flagsWithParams.contains(CompoundGlyphFlag.WE_HAVE_AN_X_AND_Y_SCALE)) {
                    double scaleX = params[0] / FIXED_POINT_SCALE;
                    double scaleY = params[1] / FIXED_POINT_SCALE;
                    coord = Transformation.SCALE.transform(coord, scaleX, scaleY);
                } else if (flagsWithParams.contains(CompoundGlyphFlag.WE_HAVE_A_TWO_BY_TWO)) {
                    double scaleX = params[0] / FIXED_POINT_SCALE;
                    double scale01 = params[1] / FIXED_POINT_SCALE;
                    double scale10 = params[2] / FIXED_POINT_SCALE;
                    double scaleY = params[3] / FIXED_POINT_SCALE;
                    int newX = (int) (coord.x() * scaleX + coord.y() * scale01);
                    int newY = (int) (coord.x() * scale10 + coord.y() * scaleY);
                    coord = new Coordinate(newX, newY);
                }
                if (params.length > 1) {
                    out[contour][i] = Transformation.TRANSLATE.transform(coord, params[0], params[1]);
                } else {
                    out[contour][i] = coord;
                }
            }
        }
        return out;
    }

    @Override
    public int getAdvanceWidth() {
        return advanceWidth;
    }

    public List<GlyphWithFlags> getComponents() {
        return components;
    }

    @Override
    public int getYMax() {
        return yMax;
    }

    @Override
    public int getXMax() {
        return xMax;
    }

    @Override
    public int getYMin() {
        return yMin;
    }

    @Override
    public int getXMin() {
        return xMin;
    }
}
