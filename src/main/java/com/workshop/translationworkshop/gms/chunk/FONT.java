package com.workshop.translationworkshop.gms.chunk;

import com.workshop.translationworkshop.gms.DataChunk;
import com.workshop.translationworkshop.gms.FontItem;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FONT {

    private final DataChunk chunk;
    public List<FontItem> fonts = new ArrayList<>();
    private List<Integer> addresses = new ArrayList<>();
    private int entries = 0;

    public FONT(DataChunk chunk) {

        this.chunk = chunk;
        chunk.buffer.position(chunk.startAddress);

        entries = chunk.buffer.getInt(); // кол-во шрифтов

        for(int i = 0; i < entries; i++) {
            addresses.add(chunk.buffer.getInt());
        }

        for(int i = 0; i < entries; i++) {
            fonts.add(new FontItem(chunk.buffer, addresses.get(i)));
        }

    }

    public FontItem getFontByAddress(int address) {
        int index = addresses.indexOf(address);
        if(index == -1) return null;
        return fonts.get(index);
    };

    public FontItem getFontByIndex(int index) {
        if(index > entries - 1) return null;
        return fonts.get(index);
    };


    public ByteBuffer toBytes() {
        // сдесь надо собрать массив адресов НА ЧТО могут ссылаться из других мест


//        chunk.name.getBytes(StandardCharsets.UTF_8);

        int len = 4 + (addresses.size() * 4);
        for(FontItem it : fonts) {
            len = len + it.toBytes().capacity();
        }

        int need = 34760;
        System.out.println("FONTLEN " + len + " OFFSET=" + (need - len));

        ByteBuffer bb = ByteBuffer.allocate(len);

        return bb;
    }

}
