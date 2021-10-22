package com.workshop.translationworkshop.gms;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FontItem {

    public String name, fontName;
    public float size;
    public boolean isBold, isItalic;
    public int rangeBegin, rangeEnd;
    public int pagePointer;
    public float scaleX, scaleY;
    public int charsCount;
    public List<FontCharItem> chars = new ArrayList<>();

    public FontItem(ByteBuffer buffer, int start) {

        buffer.position(start);

        int unknownData = 0;

        name = GMSDATA.strg.getStringByAddress(buffer.getInt());
        fontName = GMSDATA.strg.getStringByAddress(buffer.getInt());
        size = buffer.getFloat();
        isBold = buffer.getInt() > 0;
        isItalic = buffer.getInt() > 0;
        rangeBegin = buffer.getInt();
        rangeEnd = buffer.getInt();
        pagePointer = buffer.getInt();
        scaleX = buffer.getFloat();
        scaleY = buffer.getFloat();
        unknownData = buffer.getInt();
        charsCount = buffer.getInt();

        List<Integer> pointers = new ArrayList<>();

        // parse chars table
        for(int i = 0; i < charsCount; i++) {
            pointers.add(buffer.getInt());
        }

        for(int i = 0; i < charsCount; i++) {

            buffer.position(pointers.get(i));

            FontCharItem ch = new FontCharItem();
            ch.code = buffer.getShort() & 0xffff; // unsigned short
            ch.letter = Character.toString(ch.code);
            ch.posX = buffer.getShort();
            ch.posY = buffer.getShort();
            ch.sizeX = buffer.getShort();
            ch.sizeY = buffer.getShort();
            ch.shift = buffer.getShort();
            ch.offset = buffer.getInt();

            chars.add(ch);

        }

    }

    public Image getSprite() {
        TexturePage page = GMSDATA.tpag.getByAddress(pagePointer);
        return page.getImage();
    }

    public TexturePage getTexturePage() {
        return GMSDATA.tpag.getByAddress(pagePointer);
    }

    private FontCharItem getFontCharByCode(int code) {
        return chars.stream().filter(it -> it.code == code).findFirst().orElse(null);
    }

    public void getImageByString(String text, Canvas canvas) {

        Image sprite = getSprite();
        int gap = 1;
        int textWidth = 5;
        int textHeight = 0;

//        Canvas canvas = new Canvas();
        GraphicsContext ctx = canvas.getGraphicsContext2D();

        canvas.setHeight(50);
        canvas.setWidth(300);

        ctx.setImageSmoothing(false);
        ctx.setFill(Paint.valueOf("#00587a"));
        ctx.fillRect(0,0, 300, 50);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            FontCharItem charItem = getFontCharByCode((int) ch);

//            ctx.drawImage(sprite, textWidth + 1, 1, charItem.sizeX, charItem.sizeY, charItem.posX, charItem.posY, charItem.sizeX, charItem.sizeY);
            ctx.drawImage(sprite, charItem.posX, charItem.posY, charItem.sizeX, charItem.sizeY, textWidth, 1, charItem.sizeX, charItem.sizeY);

            textWidth += charItem.shift;
            textHeight = charItem.sizeY;

        }

    };

    public String toString() {
        return name;
    }

}
