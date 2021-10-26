package com.workshop.translationworkshop.gms.chunk;

import com.workshop.translationworkshop.gms.DataChunk;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class STRG {

    private final DataChunk chunk;
    private final List<String> values = new ArrayList<>();
    private final List<Integer> addresses = new ArrayList<>();
    public int entries = 0;

    public STRG(DataChunk chunk) {

        this.chunk = chunk;

        chunk.buffer.position(chunk.startAddress);

        entries = chunk.buffer.getInt(); // кол-во строк

        for(int i = 0; i < entries; i++) {
            addresses.add(chunk.buffer.getInt());     // массив адресов начала строк
        }

        for(int i = 0; i < entries; i++) {

            chunk.buffer.position(addresses.get(i));      // для каждой строки берем адрес ее начала
            int length = chunk.buffer.getInt();           // и от туда читаем длину строки

//            System.out.println( i + " " + addresses.get(i) );

            byte[] chars = new byte[length];
            chunk.buffer.get(chars);                     // читаем из буфера символы указанной длины
            String s = new String(chars, StandardCharsets.UTF_8);
            values.add(s);

        }

    }

    public String getStringByAddress(int address) {
        int index = addresses.indexOf(address - 4);
        if(index == -1) return "[not_found]";
        return values.get(index);
    }

    public List<Integer> getReferencesList() {
        // ссылки на строки идут как на длинну строки так и на саму строку
        return addresses.stream().map(it -> it + 4).collect(Collectors.toList());
    }

}
