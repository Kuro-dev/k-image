package org.kurodev.sound.wav;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Slf4j
public final class WavDecoder {
    private WavDecoder() {
        throw new IllegalStateException("Factory class cannot be instantiated");
    }
//      [Master RIFF chunk]
//        FileTypeBlocID  (4 bytes) : Identifier « RIFF »  (0x52, 0x49, 0x46, 0x46)
//        FileSize        (4 bytes) : Overall file size minus 8 bytes
//        FileFormatID    (4 bytes) : Format = « WAVE »  (0x57, 0x41, 0x56, 0x45)
//
//      [Chunk describing the data format]
//        FormatBlocID    (4 bytes) : Identifier « fmt␣ »  (0x66, 0x6D, 0x74, 0x20)
//        BlocSize        (4 bytes) : Chunk size minus 8 bytes, which is 16 bytes here  (0x10)
//        AudioFormat     (2 bytes) : Audio format (1: PCM integer, 3: IEEE 754 float)
//        NbrChannels     (2 bytes) : Number of channels
//        Frequency       (4 bytes) : Sample rate (in hertz)
//        BytePerSec      (4 bytes) : Number of bytes to read per second (Frequency * BytePerBloc).
//        BytePerBloc     (2 bytes) : Number of bytes per block (NbrChannels * BitsPerSample / 8).
//        BitsPerSample   (2 bytes) : Number of bits per sample
//
//      [Chunk containing the sampled data]
//        DataBlocID      (4 bytes) : Identifier « data »  (0x64, 0x61, 0x74, 0x61)
//        DataSize        (4 bytes) : SampledData size
//        SampledData

    private static int readInt(int len, InputStream in) throws IOException {
        return switch (len) {
            case 4 -> ByteBuffer.wrap(in.readNBytes(len)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            case 3 -> {
                byte[] read = in.readNBytes(3);
                byte[] padded = new byte[4];
                System.arraycopy(padded, 0, read, 1, 3);
                yield ByteBuffer.wrap(padded).order(ByteOrder.LITTLE_ENDIAN).getInt() >> 8;
            }
            case 2 -> ByteBuffer.wrap(in.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
            case 1 -> ByteBuffer.wrap(in.readNBytes(1)).order(ByteOrder.LITTLE_ENDIAN).get();
            default -> throw new IllegalArgumentException("Invalid Integer Size, must be 1-4 but is: " + len);
        };
    }

    public static Track decode(InputStream wav) throws IOException {
        wav = new BufferedInputStream(wav, 1024);

        //[Master RIFF chunk]
        String blocId = new String(wav.readNBytes(4));
        if (!"RIFF".equals(blocId)) {
            throw new WavFormatException("Invalid blocId");
        }
        int fileSize = readInt(4, wav);
        String fileFormatId = new String(wav.readNBytes(4));
        if (!"WAVE".equals(fileFormatId)) {
            throw new WavFormatException("Invalid fileFormatId");
        }

        //[Chunk describing the data format]
        String formatBlocId = new String(wav.readNBytes(4));
        if (!"fmt ".equals(formatBlocId)) {
            throw new WavFormatException("Invalid formatBlocId");
        }
        int blocSize = readInt(4, wav);
        int audioFormat = readInt(2, wav); //Audio format (1: PCM integer, 3: IEEE 754 float)

        if (audioFormat != 1) {
            throw new UnsupportedEncodingException("float encoding not supported yet");
        }

        int numberOfChannels = readInt(2, wav);
        int sampleRate = readInt(4, wav);
        int bytePerSecond = readInt(4, wav);
        int bytePerBloc = readInt(2, wav);
        int bitsPerSample = readInt(2, wav);

        //[Chunk containing the sampled data]
        String dataBlocId = new String(wav.readNBytes(4));
        int dataSize = readInt(4, wav);
        while (!"data".equals(dataBlocId)) { //Skip blocks that aren't data, for now. Maybe i'll support them later
            wav.skip(dataSize);
            dataBlocId = new String(wav.readNBytes(4));
            dataSize = readInt(4, wav);
        }


        double[][] data = new double[numberOfChannels][];
        int bytesPerSample = bitsPerSample / 8;
        int channelSize = dataSize / (bytesPerSample * numberOfChannels); //4320000
        Arrays.fill(data, new double[channelSize]);
        for (int i = 0; i < channelSize; i++) {
            for (int n = 0; n < numberOfChannels; n++) {
                data[n][i] = readInt(bytesPerSample, wav) / Math.pow(2, bitsPerSample - 1);
            }
        }
        return new Track(sampleRate, data);
    }

    private static void write(OutputStream out, String value) throws IOException {
        out.write(value.getBytes());
    }

    private static void write(OutputStream out, int value) throws IOException {
        out.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array());
    }

    private static void writeShort(OutputStream out, int value) throws IOException {
        out.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) (value & 0xFFFF)).array());
    }


    public static void encode(Track track, OutputStream out) throws IOException {
        encode(track, out, 4);
    }

    public static void encode(Track track, OutputStream out, int byteDepth) throws IOException {
        out = new BufferedOutputStream(out, 1024);
        if (byteDepth > 4 || byteDepth < 1) {
            throw new IllegalArgumentException("Byte depths exceeds allowed value 1-4. Actual: " + byteDepth);
        }

        //[Master RIFF chunk]
        write(out, "RIFF");
        write(out, track.getNumOfChannels() * track.getNumOfSamples() * byteDepth + 36);
        write(out, "WAVE");

        //[Chunk describing the data format]
        write(out, "fmt ");
        write(out, 0x10);
        writeShort(out, 0x01);
        writeShort(out, track.getNumOfChannels());
        write(out, track.getSampleRate());
        int bytesPerBloc = track.getNumOfChannels() * byteDepth;
        write(out, bytesPerBloc * track.getSampleRate());
        writeShort(out, bytesPerBloc);
        writeShort(out, byteDepth * 8);

        //[Chunk containing the sampled data]
        write(out, "data");
        write(out, byteDepth * track.getNumOfSamples() * track.getNumOfChannels());
        for (int i = 0; i < track.getNumOfSamples(); i++) {
            for (int ch = 0; ch < track.getNumOfChannels(); ch++) {
                write(out, (int) (Math.clamp(track.getSample(ch, i), -1.0, 1.0) * Math.pow(2, byteDepth * 8 - 1)));
            }
        }
    }
}
