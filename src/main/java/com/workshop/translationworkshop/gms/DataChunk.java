package com.workshop.translationworkshop.gms;

import java.nio.ByteBuffer;
import java.util.List;

public class DataChunk {

    public String name;
    public int startAddress = 0;
    public int size = 0;
    public ByteBuffer buffer;

    public DataChunk(String name, ByteBuffer buffer, int startAddress, int size) {
        this.name = name;
        this.startAddress = startAddress;
        this.size = size;
        this.buffer = buffer;
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

    public String toString() {
        return name + " at " + startAddress + " len = " + size;
    }

}
