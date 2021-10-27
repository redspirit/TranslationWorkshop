package com.workshop.translationworkshop.gms;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DataChunk {

    public String name;
    public int startAddress = 0;
    public int size = 0;
    public ByteBuffer buffer;
    public int chunkStart, chunkSize;

    public DataChunk(String name, ByteBuffer buffer, int startAddress, int size) {
        this.name = name;
        this.startAddress = startAddress;
        this.size = size;
        this.buffer = buffer;

        this.chunkStart = startAddress - 8;
        this.chunkSize = size + 8;

    }

    public int findAndReplace(int value, int newValue) {
        // ищем в этом чанке 4 байтное значение чтобы потом заменить его

        int count = 0;

        for (int i = startAddress; i < startAddress + size; i++) {

            if(buffer.getInt(i) == value) {
                count++;
                buffer.putInt(i, newValue);
            };

        }

        return count;
    }

    static public int findAddressByName(ByteBuffer bb, String name) {

        ByteBuffer nameBuf = ByteBuffer.wrap(name.getBytes(StandardCharsets.UTF_8));
        nameBuf.order(ByteOrder.LITTLE_ENDIAN);
        int nameInt = nameBuf.getInt();

        for (int i = (int)(bb.capacity() / 2); i < bb.capacity() - 4; i++) {
            if(bb.getInt(i) == nameInt) {
                return i;
            };
        }

        return 0;

    }

    public String toString() {
        return name + " at " + startAddress + " len = " + size;
    }

}
