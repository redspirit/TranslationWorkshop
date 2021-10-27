package com.workshop.translationworkshop.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Utils {

    static public int round16(int n) {
        return (int)(Math.ceil(n / 16.0) * 16);
    }

    static public int roundN(int n, int i) {
        return (int)(Math.ceil(n / (float)i) * i);
    }

    static public String intToHex(int n) {
        return Integer.toHexString(n);
    }


    static public ByteBuffer replaceBufferContent(ByteBuffer source, int offset, int len, ByteBuffer content) {

        // вставляем один буфер в другой заменяя при этому часть из исходного буфера

        ByteBuffer result = ByteBuffer.allocate(source.capacity() - len + content.capacity());
        source.rewind();

        byte[] top = new byte[offset];
        source.get(top);
        result.put(top);

        result.put(content);

        int bottomLen = source.capacity() - offset - len;
        byte[] bottom = new byte[bottomLen];
        source.position(offset + len);
        source.get(bottom);
        result.put(bottom);

        result.rewind();
        return result;
    }

    static public ByteBuffer insertToBuffer(ByteBuffer source, int position, ByteBuffer content) {

        // вставляет один буфер в другой в указанную позицию

        ByteBuffer result = ByteBuffer.allocate(source.capacity() + content.capacity());
        source.rewind();

        byte[] top = new byte[position];
        source.get(top);
        result.put(top);

        result.put(content);

        byte[] bottom = new byte[source.capacity() - position];
        source.position(position);
        source.get(bottom);
        result.put(bottom);

        result.rewind();
        return result;
    }

    static public ByteBuffer appendToBuffer(ByteBuffer source, ByteBuffer content) {

        ByteBuffer result = ByteBuffer.allocate(source.capacity() + content.capacity());
        source.rewind();

        result.put(source);
        result.put(content);

        result.rewind();
        return result;
    }

    static public int findAddress(ByteBuffer bb, int address) {

        int count = 0;
        bb.position(0);
        for (int i = 0; i < bb.capacity() - 4; i++) {
            if(bb.getInt(i) == address) {
                count++;
            }
        }
        return count;

    }

    static public void findAndReplaceString(ByteBuffer bb, String s1, String s2) {

        int pos = bb.position();
        int num = ByteBuffer.wrap(s1.getBytes(StandardCharsets.UTF_8)).getInt();
        int num2 = ByteBuffer.wrap(s2.getBytes(StandardCharsets.UTF_8)).getInt();

        bb.position(0);
        for (int i = 0; i < bb.capacity() - 4; i++) {
            if(bb.getInt(i) == num) {
                bb.putInt(i, num2);
                break;
            }
        }
        bb.position(pos);

    }

}
