package com.workshop.translationworkshop.gms.chunk;

import com.workshop.translationworkshop.gms.DataChunk;
import com.workshop.translationworkshop.gms.Texture;
import java.util.ArrayList;
import java.util.List;

public class TXTR {

    private final DataChunk chunk;
    private List<Integer> addresses = new ArrayList<>();
    public List<Texture> textures = new ArrayList<>();
    public int entries = 0;

    public TXTR(DataChunk chunk) {

        this.chunk = chunk;
        chunk.buffer.position(chunk.startAddress);

        entries = chunk.buffer.getInt();

        for(int i = 0; i < entries; i++) {
            addresses.add(chunk.buffer.getInt());
        }

        for(int i = 0; i < entries; i++) {
            chunk.buffer.position(addresses.get(i));

            Texture t = new Texture();
            t.scaled = chunk.buffer.getInt();
            t.generatedMips = chunk.buffer.getInt();
            t.pngPointer = chunk.buffer.getInt();
            t.loadPng(chunk.buffer);
            textures.add(t);

        }

    }

    public Texture getByAddress(int address) {
        int index = addresses.indexOf(address);
        if(index == -1) return null;
        return textures.get(index);
    }

    public Texture getByIndex(int index) {
        return textures.get(index);
    }

}
