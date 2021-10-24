package com.workshop.translationworkshop.gms;

import com.workshop.translationworkshop.gms.chunk.TXTR;
import com.workshop.translationworkshop.gms.chunk.TPAG;
import com.workshop.translationworkshop.gms.chunk.STRG;
import com.workshop.translationworkshop.gms.chunk.FONT;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GMSDATA {

    private static ByteBuffer buffer;
    private static int fileSize = 0;

    private static List<String> names = new ArrayList<>();
    private static List<DataChunk> chunks = new ArrayList<>();

    public static STRG strg;
    public static FONT font;
    public static TPAG tpag;
    public static TXTR txtr;

    static public boolean loadFile(String path) {

        byte[] bytes;
        try {
            // todo проверить на неверном файле
            bytes = Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        fileSize = bytes.length;
        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        /// start parsing chunks
        buffer.position(0);
        int i = 8;

        while (i < fileSize - 1) {

            byte[] nameBytes = new byte[4];
            buffer.slice(i, 4).get(nameBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);
            i += 4;

            int chunkLen = buffer.getInt(i);
            System.out.println(name + " = " + chunkLen);

            names.add(name);
            chunks.add( new DataChunk(name, i + 4, chunkLen) );

            i = chunkLen + i + 4;

        }

        strg = new STRG(buffer, getChunkAddress("STRG"));
        font = new FONT(buffer, getChunkAddress("FONT"));
        tpag = new TPAG(buffer, getChunkAddress("TPAG"));
        txtr = new TXTR(buffer, getChunkAddress("TXTR"));

        return true;

    }

    static public List<FontItem> getFonts() {
        return font.fonts;
    }

    static public FontItem getFont(int index) {
        return font.getFontByIndex(index);
    }

    static public DataChunk getChunkAddress(String name) {
        return chunks.get(names.indexOf(name));
    }

}
