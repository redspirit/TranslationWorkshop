package com.workshop.translationworkshop.gms.chunk;

import com.workshop.translationworkshop.gms.DataChunk;
import com.workshop.translationworkshop.gms.Texture;
import com.workshop.translationworkshop.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TXTR {

    private final DataChunk chunk;
    private final List<Integer> addresses = new ArrayList<>();
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

    public Texture getByIndex(int index) {
        return textures.get(index);
    }

    public void addSprite(byte[] image) {

        Texture t = new Texture();
        t.scaled = 1;
        t.generatedMips = 0;
        t.imageBytes = image;
        t.pngPointer = 0; // ?????

        textures.add(t);

    }

    public ByteBuffer assemblyTXTR(int contentSize) {

        entries = textures.size();


        int totalImagesSize = 0;
        for(Texture t : textures) {
            totalImagesSize += t.getImageSize();
        }

        int entireSize = (3 * 4) + (entries * 4) + (entries * 3 * 4) + totalImagesSize;
        ByteBuffer result = ByteBuffer.allocate(Utils.round16(entireSize));
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put("TXTR".getBytes(StandardCharsets.UTF_8));
        result.putInt(entireSize - 8); // size of chunk
        result.putInt(entries); // count of textures

        int pos = result.position() + (entries * 4) + contentSize;
        for(int i = 0; i < entries; i++) {
            result.putInt(pos + i * 12); // pointer to start texture header
        }

        pos = result.position() + (entries * 12) + contentSize;
        int accumulate = 0;
        for(Texture t : textures) {
            result.putInt(t.scaled);
            result.putInt(t.generatedMips);
            result.putInt(pos + accumulate); // pointer to image
            accumulate += t.getImageSize();
        }

        for(Texture t : textures) {
            result.put(t.getImageBytes());
        }

        result.rewind();
        return result;

    }

}
