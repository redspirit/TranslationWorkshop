package com.workshop.translationworkshop.gms;

import com.workshop.translationworkshop.gms.chunk.*;
import com.workshop.translationworkshop.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
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

    public static GEN8 gen8;
    public static OPTN optn;
    public static LANG lang;
    public static EXTN extn;
    public static SOND sond;
    public static AGRP agrp;
    public static SPRT sprt;
    public static BGND bgnd;
    public static PATH path;
    public static SCPT scpt;
    public static GLOB glob;
    public static SHDR shdr;
    public static FONT font;
    public static TMLN tmln;
    public static OBJT objt;
    public static ACRV acrv;
    public static SEQN seqn;
    public static TAGS tags;
    public static ROOM room;
    public static DAFL dafl;
    public static EMBI embi;
    public static TPAG tpag;
    public static TGIN tgin;
    public static STRG strg;
    public static TXTR txtr;
    public static AUDO audo;

    public static ByteBuffer getBuffer() {
        return buffer;
    }

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
            chunks.add( new DataChunk(name, buffer,i + 4, chunkLen) );

            i = chunkLen + i + 4;

        }

        strg = new STRG(getChunkAddress("STRG"));
        gen8 = new GEN8(getChunkAddress("GEN8"));
        font = new FONT(getChunkAddress("FONT"));
        tpag = new TPAG(getChunkAddress("TPAG"));
        txtr = new TXTR(getChunkAddress("TXTR"));

        
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
    static public int getIndexChunkByName(String name) {
        return names.indexOf(name);
    }

    static public void assembly() {

        // взять чанк FONT а модифицировать таким образом, чтобы там были добавлены новые символы
        // модифицированный чанк добавить к концу архива и в заголовке архива прописать новый адрес на этот чанк (старый не трогается)

        // берем оригинальный буфер и наращиваем ему шрифт
        // в прописываем размер файла в заголовке
        // находим вхождение FONT и заменяем его на UNKN (либо потом на нули заменяем)

        int spacesLen = Utils.round16(buffer.capacity()) - buffer.capacity();
        ByteBuffer spaceBuffer = ByteBuffer.allocate(spacesLen);
        buffer = Utils.appendToBuffer(buffer, spaceBuffer);

        System.out.println("spacesLen " + spacesLen);


        ByteBuffer fontBuffer = font.assemblyFONT(buffer.capacity());


        ByteBuffer result = Utils.appendToBuffer(buffer, fontBuffer);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.putInt(4, result.capacity() - 8);
        result.put(font.chunk.startAddress - 8, "UNKN".getBytes(StandardCharsets.UTF_8));

        // перекрываем размером предыдущий чанк чтобы парсер не пытался прочитать старые шрифты
        DataChunk dc = chunks.get(getIndexChunkByName("FONT") - 1);
        result.putInt(dc.startAddress - 4, dc.size + font.chunk.size + 8);

        DataChunk lc = chunks.get(chunks.size() - 1); // get last chunk
        result.putInt(lc.startAddress - 4, lc.size + spacesLen);



        File file = new File("D:\\games\\Deaths Gambit Afterlife\\Backup\\mod_data.win");
        FileChannel channel = null;
        try {
            channel = new FileOutputStream(file, false).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            result.position(0);
            channel.write(result);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saved!");

    }

    static public void assemblyT() {

//        Utils.replaceBufferContent(buffer, )


//        txtr.addSprite();

        ByteBuffer result = txtr.assemblyTXTR(0);



        File file = new File("D:\\games\\Deaths Gambit Afterlife\\Backup\\txtr.win");
        FileChannel channel = null;
        try {
            channel = new FileOutputStream(file, false).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            result.position(0);
            channel.write(result);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saved textures!");

    }


}
