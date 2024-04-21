package org.kurodev.kimage.kimage.font;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FontReaders {
    private static FontReader defaultFontReader;

    public static FontReader getDefaultFontReader() {
        if (defaultFontReader == null) {
            try {
                defaultFontReader = loadFont(FontReaders.class.getResourceAsStream("/kimage/Pixellettersfull.ttf"));
            } catch (IOException e) {
                //should never happen
                throw new RuntimeException(e);
            }
        }
        return defaultFontReader;
    }

    /**
     * @param in non-null datasource of the font. .TTF format
     * @throws IOException when an I/O error occurs
     * @throws NullPointerException if {@code in} is null
     */
    public static FontReader loadFont(InputStream in) throws IOException {
        Objects.requireNonNull(in);
        FontReader reader = new FontReader();
        reader.load(new DataInputStream(in));
        return reader;
    }
}
