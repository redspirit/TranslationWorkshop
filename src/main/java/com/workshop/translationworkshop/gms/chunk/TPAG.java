package com.workshop.translationworkshop.gms.chunk;

import com.workshop.translationworkshop.gms.DataChunk;
import com.workshop.translationworkshop.gms.SpritePoint;
import com.workshop.translationworkshop.gms.SpriteRect;
import com.workshop.translationworkshop.gms.TexturePage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TPAG {

    private final DataChunk chunk;
    private List<Integer> addresses = new ArrayList<>();
    private List<TexturePage> pages = new ArrayList<>();
    public int entries = 0;

    public TPAG( DataChunk chunk) {

        this.chunk = chunk;
        chunk.buffer.position(chunk.startAddress);

        entries = chunk.buffer.getInt();

        for(int i = 0; i < entries; i++) {
            addresses.add(chunk.buffer.getInt());
        }

        for(int i = 0; i < entries; i++) {
            chunk.buffer.position(addresses.get(i));

            SpriteRect source = new SpriteRect(
                    new SpritePoint(chunk.buffer.getShort(), chunk.buffer.getShort()),
                    new SpritePoint(chunk.buffer.getShort(), chunk.buffer.getShort())
            );
            SpriteRect target = new SpriteRect(
                    new SpritePoint(chunk.buffer.getShort(), chunk.buffer.getShort()),
                    new SpritePoint(chunk.buffer.getShort(), chunk.buffer.getShort())
            );
            SpritePoint size = new SpritePoint(chunk.buffer.getShort(), chunk.buffer.getShort());
            short index = chunk.buffer.getShort();

            pages.add(new TexturePage(source, target, size, index));

        }

    }

    public TexturePage getByAddress(int address) {
        int index = addresses.indexOf(address);
        if(index == -1) return null;
        return pages.get(index);
    }

}
