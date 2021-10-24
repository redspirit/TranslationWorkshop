package com.workshop.translationworkshop.gms.chunk;

import com.workshop.translationworkshop.gms.DataChunk;
import com.workshop.translationworkshop.gms.FontItem;

import java.nio.ByteBuffer;
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

}
