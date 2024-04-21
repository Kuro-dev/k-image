package org.kurodev.kimage.kimage.img;

import org.kurodev.kimage.kimage.util.DeflateCompression;
import org.kurodev.kimage.kimage.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class SimplePngEncoder {
    private static final Logger logger = LoggerFactory.getLogger(SimplePngEncoder.class);

    private final Map<String, byte[]> customChunks;
    private final SimplePng simplePng;

    public SimplePngEncoder(SimplePng simplePng, Map<String, byte[]> customChunks) {
        this.simplePng = simplePng;
        this.customChunks = customChunks;
    }

    public SimplePngEncoder(SimplePng simplePng) {
        this(simplePng, new HashMap<>());
    }

    public void addCustomChunk(String type, byte[] data) {
        customChunks.put(type, data);
    }

    public void addCustomChunk(String type, String data) {
        customChunks.put(type, data.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] encodeToPng() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Write PNG signature
        baos.write(new byte[]{(byte) 137, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10});

        // Write IHDR chunk
        ByteBuffer ihdr = ByteBuffer.allocate(13);
        ihdr.putInt(simplePng.getWidth());
        ihdr.putInt(simplePng.getHeight());
        ihdr.put((byte) 8); // Bit depth
        ihdr.put((byte) 6); // Color type: 6 - Truecolor with alpha (RGBA)
        ihdr.put((byte) 0); // Compression method
        ihdr.put((byte) 0); // Filter method
        ihdr.put((byte) 0); // Interlace method
        writeChunk(baos, "IHDR", ihdr.array());

        // Write tRNS chunk if alpha channel is present
        if (simplePng.hasAlphaChannel()) {
            byte[] alphaData = simplePng.getAlphaChannel();
            writeChunk(baos, "tRNS", alphaData);
        }

        // Write custom chunks
        for (Map.Entry<String, byte[]> entry : customChunks.entrySet()) {
            writeChunk(baos, entry.getKey(), entry.getValue());
        }

        // Process and Write IDAT chunk (uncompressed image data with filter bytes)
        byte[] idatData = processImageData(simplePng.getImageData());
        byte[] compressedIdatData = DeflateCompression.compress(idatData);
        writeChunk(baos, "IDAT", compressedIdatData);
        logger.info("Compressed image data from {} to {}",
                Util.bytesToString(idatData.length),
                Util.bytesToString(compressedIdatData.length));

        // Write IEND chunk
        writeChunk(baos, "IEND", new byte[]{});

        return baos.toByteArray();
    }

    private byte[] processImageData(byte[] imageData) {
        ByteArrayOutputStream processedData = new ByteArrayOutputStream();
        int bytesPerRow = simplePng.getWidth() * 4; // 4 bytes per pixel (RGBA)

        for (int row = 0; row < simplePng.getHeight(); row++) {
            processedData.write(0); // Filter type byte for this scanline (None)
            processedData.write(imageData, row * bytesPerRow, bytesPerRow);
        }

        return processedData.toByteArray();
    }

    private void writeChunk(ByteArrayOutputStream stream, String type, byte[] data) throws IOException {
        logger.info("Writing chunk: {}, Length: {}", type, Util.bytesToString(data.length));

        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + data.length + 4);
        buffer.putInt(data.length);
        buffer.put(type.getBytes());
        buffer.put(data);

        CRC32 crc = new CRC32();
        crc.update(type.getBytes());
        crc.update(data);
        buffer.putInt((int) crc.getValue());

        stream.write(buffer.array());
    }
}
