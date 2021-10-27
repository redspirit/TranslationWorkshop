package com.workshop.translationworkshop.gms.chunk;

import com.workshop.translationworkshop.gms.DataChunk;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AUDO {

    private final DataChunk chunk;
    private final List<Integer> addresses = new ArrayList<>();
    public int entries = 0;

    public AUDO(DataChunk chunk) {
        this.chunk = chunk;

        chunk.buffer.position(chunk.startAddress);
        entries = chunk.buffer.getInt(); // кол-во элементов

        for(int i = 0; i < entries; i++) {
            addresses.add(chunk.buffer.getInt());
        }

    }

    public void increaseAddressesBy(ByteBuffer bb, int value) {

        bb.position(chunk.startAddress + value + 4);
        for(int i = 0; i < entries; i++) {
            bb.putInt(addresses.get(i) + value);
        }

    }
}
