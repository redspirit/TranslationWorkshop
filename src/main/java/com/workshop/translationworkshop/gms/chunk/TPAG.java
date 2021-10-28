package com.workshop.translationworkshop.gms.chunk;

import com.workshop.translationworkshop.gms.*;

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

            // загружаем только те страницы, которые привязаны к шрифтам
            if(!GMSDATA.font.tpagAddrList.contains(addresses.get(i))) continue;

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

            pages.add(new TexturePage(source, target, size, index, addresses.get(i)));

        }

    }

    public TexturePage getByAddress(int address) {
        return pages.stream().filter(it -> it.address == address).findFirst().orElse(null);
    }

}
