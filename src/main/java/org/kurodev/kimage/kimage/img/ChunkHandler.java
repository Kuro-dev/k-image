package org.kurodev.kimage.kimage.img;

import java.io.IOException;

public interface ChunkHandler {
    void handleChunk(byte[] data) throws IOException;
}
