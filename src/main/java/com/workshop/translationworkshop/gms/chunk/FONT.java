package com.workshop.translationworkshop.gms.chunk;

import com.workshop.translationworkshop.gms.DataChunk;
import com.workshop.translationworkshop.gms.FontItem;
import com.workshop.translationworkshop.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FONT {

    public final DataChunk chunk;
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

    public ByteBuffer assemblyFONT(int contentSize) {


        int fontsItemsSize = 0;
        int startCount = 12 + (entries * 4);
        List<ByteBuffer> fontBuffers = new ArrayList<>();
        for(int i = 0; i < entries; i++) {
            ByteBuffer b = fonts.get(i).toBuffer(startCount + contentSize);
            fontBuffers.add(b);
            fontsItemsSize += b.capacity();
            startCount += b.capacity();
        }

        int entireSize = (3 * 4) + (entries * 4) + fontsItemsSize + 512;
        ByteBuffer result = ByteBuffer.allocate(Utils.round16(entireSize));
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put("FONT".getBytes(StandardCharsets.UTF_8));
        result.putInt(entireSize - 8); // size of chunk
        result.putInt(entries); // count of fonts


        int pos = result.position() + (entries * 4) + contentSize;
        int accumulator = 0;
        for(int i = 0; i < entries; i++) {
            result.putInt(pos + accumulator); // надо расчитать начала каждого шрифтаи записать потом
            accumulator += fontBuffers.get(i).capacity();
        }

        for(int i = 0; i < entries; i++) {
            result.put(fontBuffers.get(i));
        }

        for(short i = 0; i < 128; i++) {
            result.putShort(i);
        }
        for(short i = 0; i < 128; i++) {
            result.putShort((short)63);
        }

        System.out.println("LEN FONTS = " + entireSize);

        result.rewind();
        return result;
    }


}
