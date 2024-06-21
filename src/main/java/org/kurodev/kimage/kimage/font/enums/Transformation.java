package org.kurodev.kimage.kimage.font.enums;

import org.kurodev.kimage.kimage.font.glyph.Coordinate;

/**
 * Enum representing different types of transformations that can be applied to coordinates.
 */
public enum Transformation {

    /**
     * Translation transformation.
     * Shifts the coordinate by the specified amounts.
     * </p>
     * params: {@code tx, ty} Relative offsets
     */
    TRANSLATE {
        @Override
        public Coordinate transform(Coordinate coord, int... params) {
            if (params.length != 2) {
                throw new IllegalArgumentException("TRANSLATE transformation requires exactly 2 parameters: tx and ty.");
            }
            int tx = params[0];
            int ty = params[1];
            return new Coordinate(coord.x() + tx, coord.y() + ty);
        }
    },

    /**
     * Scaling transformation.
     * Scales the coordinate by the specified factors.
     *
     * </p> coord The coordinate to transform.
     * </p> params: {@code sx,sy} scale factors
     */
    SCALE {
        @Override
        public Coordinate transform(Coordinate coord, int... params) {
            if (params.length != 2) {
                throw new IllegalArgumentException("SCALE transformation requires exactly 2 parameters: sx and sy.");
            }
            int sx = params[0];
            int sy = params[1];
            return new Coordinate(coord.x() * sx, coord.y() * sy);
        }
    },

    /**
     * Rotation transformation.
     * Rotates the coordinate by the specified angle (in degrees).
     * </p>Param: {@code angle} in degrees.
     */
    ROTATE {
        @Override
        public Coordinate transform(Coordinate coord, int... params) {
            if (params.length != 1) {
                throw new IllegalArgumentException("ROTATE transformation requires exactly 1 parameter: the angle in degrees.");
            }
            double theta = Math.toRadians(params[0]);
            int x = coord.x();
            int y = coord.y();
            int newX = (int) (x * Math.cos(theta) - y * Math.sin(theta));
            int newY = (int) (x * Math.sin(theta) + y * Math.cos(theta));
            return new Coordinate(newX, newY);
        }
    },

    /**
     * Shear transformation.
     * Shears the coordinate by the specified factors.
     * </p>Imagine you're holding a rectangular piece of paper,
     * and you slide the top edge to the right while keeping the bottom edge fixed.
     * The paper will look like a parallelogram,

     * </p> coord The coordinate to transform.
     * </p> params: {@code shx, shy} The
     */
    SHEAR {
        @Override
        public Coordinate transform(Coordinate coord, int... params) {
            if (params.length != 2) {
                throw new IllegalArgumentException("SHEAR transformation requires exactly 2 parameters: shx and shy.");
            }
            int shx = params[0];
            int shy = params[1];
            int x = coord.x();
            int y = coord.y();
            int newX = x + shx * y;
            int newY = y + shy * x;
            return new Coordinate(newX, newY);
        }
    };

    /**
     * Abstract method to apply the transformation.
     *
     * </p> coord  The coordinate to transform.
     * </p> params The parameters for the transformation.
     *
     * @return The transformed coordinate.
     */
    public abstract Coordinate transform(Coordinate coord, int... params);
}
