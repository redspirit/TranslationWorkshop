package com.workshop.translationworkshop.utils;

import java.nio.ByteBuffer;

public class Utils {

    static public int round16(int n) {
        return (int)(Math.ceil(n / 16.0) * 16);
    }

    static public ByteBuffer replaceBufferContent(ByteBuffer source, int offset, int len, ByteBuffer content) {

        // вставляем один буфер в другой заменяя при этому часть из исходного буфера

        ByteBuffer result = ByteBuffer.allocate(source.capacity() - len + content.capacity());
        source.position(0);

        byte[] top = new byte[offset];
        source.get(top);
        result.put(top);

        result.put(content);

        int bottomLen = source.capacity() - offset - len;
        byte[] bottom = new byte[bottomLen];
        source.position(offset + len);
        source.get(bottom);
        result.put(bottom);

        result.position(0);
        return result;
    }

    static public ByteBuffer insertToBuffer(ByteBuffer source, int position, ByteBuffer content) {

        // вставляет один буфер в другой в указанную позицию

        ByteBuffer result = ByteBuffer.allocate(source.capacity() + content.capacity());
        source.position(0);

        byte[] top = new byte[position];
        source.get(top);
        result.put(top);

        result.put(content);

        byte[] bottom = new byte[source.capacity() - position];
        source.position(position);
        source.get(bottom);
        result.put(bottom);

        result.position(0);
        return result;
    }

}
