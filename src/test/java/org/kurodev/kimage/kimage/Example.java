package org.kurodev.kimage.kimage;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class Example {

    @Test
    public void test() throws IOException {
        decodePng(Files.readAllBytes(Path.of("./test.png")));
    }

    public void decodePng(byte[] pngData) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(pngData);

        // Skip PNG signature (8 bytes)
        stream.skip(8);

        while (stream.available() > 0) {
            // Read chunk length (4 bytes)
            byte[] lengthBytes = new byte[4];
            stream.read(lengthBytes);
            int length = ByteBuffer.wrap(lengthBytes).getInt();

            // Read chunk type (4 bytes)
            byte[] typeBytes = new byte[4];
            stream.read(typeBytes);
            String type = new String(typeBytes);
            System.out.println(type);
            if (type.equals("IHDR")) {
                System.out.println("I FOUND THE HEADER!");
            }
            if (type.equals("IDAT")){
                System.out.println("I FOUND THE IMAGE DATA");
            }
            if (type.equals("IEND")){
                System.out.println("I FOUND THE END OF THE IMAGE");
            }
            stream.skip(length); //data of this chunk
            stream.skip(4); //checksum
        }
    }
}
