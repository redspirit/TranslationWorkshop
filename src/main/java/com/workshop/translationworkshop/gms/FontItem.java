package com.workshop.translationworkshop.gms;

import com.workshop.translationworkshop.utils.Glyph;
import com.workshop.translationworkshop.utils.TTFData;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FontItem {

    public String name, fontName;
    public int namePointer, fontNamePointer;
    public float size;
    public boolean isBold, isItalic;
    public int rangeBegin, rangeEnd;
    public int pagePointer;
    public float scaleX, scaleY;
    public int charsCount;
    public int unknownData;
    public List<FontCharItem> chars = new ArrayList<>();
    public int glyphHeight = 0;

    public double customScaleX = 1.05;
    public double customScaleY = 0;
    public double customOffsetY = 0;
    public boolean spriteStored = false;

    public TexturePage origPage, modPage;

    public FontItem(ByteBuffer buffer, int start) {

        buffer.position(start);

        namePointer = buffer.getInt();
        fontNamePointer = buffer.getInt();

        name = GMSDATA.strg.getStringByAddress(namePointer);
        fontName = GMSDATA.strg.getStringByAddress(fontNamePointer);
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

            glyphHeight = ch.sizeY;

        }

    }

    public void assignTpage() {
        origPage = GMSDATA.tpag.getByAddress(pagePointer);
        modPage = origPage.clone();
    }

    public void reset() {
        clearAllCustomChars();
        modPage = origPage.clone();
    }


    public FontCharItem getLastCharItem() {

        int maxY = 0;
        int maxX = 0;
        FontCharItem match = null;

        for(FontCharItem ch : chars) {
            if(ch.posY > maxY) maxY = ch.posY;
        }

        for(FontCharItem ch : chars) {
            if(ch.posY == maxY && ch.posX > maxX) {
                maxX = ch.posX;
                match = ch;
            }
        }

        return match;

    }

    private SpritePoint findInsertPlace(int charWidth) {

        FontCharItem lastChar = getLastCharItem();

        int newX = lastChar.posX + lastChar.sizeX + 2;
        int newY = lastChar.posY + lastChar.sizeY + 2;
        if(newX + charWidth > modPage.size.x) {
            // символ не влазиет в текущую строку, может есть место пониже?

            if(newY + glyphHeight > modPage.size.y) {
                // на спрайте уже совсем нет места
                return null;
            } else {
                return new SpritePoint(2, newY);
            }

        } else {
            // для нового символа нашлость подходящее местечко
            return new SpritePoint(newX, lastChar.posY);
        }

    }

    public boolean addNewChar(TTFData ttf, char sym) {

        Glyph g = new Glyph(ttf.font);
        g.setParams(customScaleX, customScaleY, customOffsetY, ttf.glyphShift);

        Image img;
        if(ttf.isPixelFont) {
            img = g.getCharImagePixel(Character.toString(sym), glyphHeight);
        } else {
            img = g.getCharImage(Character.toString(sym), glyphHeight);
        }

        SpritePoint newPos = findInsertPlace((int) img.getWidth());

        if(newPos == null) return false;

        FontCharItem ch = new FontCharItem();
        ch.code = (short) sym;
        ch.letter = Character.toString(sym);
        ch.posX = newPos.x;
        ch.posY = newPos.y;
        ch.sizeX = (short) img.getWidth();
        ch.sizeY = (short) glyphHeight;
        ch.shift = (short) (ch.sizeX + ttf.glyphShift);
        ch.offset = 0;
        ch.isCustom = true;
        chars.add(ch);

        modPage.drawImage(img, ch);

        return true;
    }

    public boolean addNewChars(TTFData ttf, String str) {

        for (int i = 0; i < str.length(); i++) {
            boolean res = addNewChar(ttf, str.charAt(i));
            if(!res) {
                return false;
            }
        }

        // сортируем символы по коду - это важно
        chars = chars.stream()
                .sorted(Comparator.comparing(FontCharItem::getCode))
                .collect(Collectors.toList());

        return true;
    }

    private void clearAllCustomChars() {
        chars = chars.stream().filter(it -> !it.isCustom).collect(Collectors.toList());
    }



    private FontCharItem getFontCharByCode(int code) {
        return chars.stream().filter(it -> it.code == code).findFirst().orElse(null);
    }

    public void getImageByString(String text, Canvas canvas, double scale) {

        int textWidth = 5;
        int textHeight = 0;

        GraphicsContext ctx = canvas.getGraphicsContext2D();

        canvas.setHeight(glyphHeight * scale);
        canvas.setWidth(512);

        ctx.setFontSmoothingType(FontSmoothingType.LCD);
        ctx.setImageSmoothing(true);

        ctx.setFill(Paint.valueOf("#000000"));
        ctx.fillRect(0,0, 512, glyphHeight * scale);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            FontCharItem charItem = getFontCharByCode(ch);

            if(charItem == null) {
                continue; // символ, который не смогли найти - пропускаем
            }

            ctx.drawImage(
                    modPage.image,
                    charItem.posX, charItem.posY,
                    charItem.sizeX, charItem.sizeY,
                    textWidth * scale, 0,
                    charItem.sizeX * scale, charItem.sizeY * scale
            );

            textWidth += charItem.shift;
            textHeight = charItem.sizeY;

        }

        canvas.setHeight(textHeight * scale);
        canvas.setWidth(textWidth * scale + 3);

    };



    public byte[] getSpritePngData() {

        RenderedImage renderedImage = SwingFXUtils.fromFXImage(modPage.image, null);
        try {

            ByteArrayOutputStream ios = new ByteArrayOutputStream();
            ImageIO.write(renderedImage, "png", ios);
            return ios.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void savePngToFile(String filename) throws IOException {

        RenderedImage renderedImage = SwingFXUtils.fromFXImage(modPage.image, null);
        ImageIO.write(renderedImage, "png", new File(filename));

    }

    public FontCharItem getCharItemByPosition(int x, int y) {
        return chars.stream().filter(item -> (x >= item.posX && x <= item.posX + item.sizeX) && (y >= item.posY && y <= item.posY + item.sizeY)).findFirst().orElse(null);
    }

    public ByteBuffer toBuffer(int startCount) {

        charsCount = chars.size();
        int entireSize = (12 * 4) + (charsCount * 4) + (charsCount * 16); // font header + chars header + chars content
        ByteBuffer b = ByteBuffer.allocate(entireSize);
        b.order(ByteOrder.LITTLE_ENDIAN);

        b.putInt(namePointer);
        b.putInt(fontNamePointer);
        b.putFloat(size);
        b.putInt(isBold ? 1 : 0);
        b.putInt(isItalic ? 1 : 0);
        b.putInt(rangeBegin);
        b.putInt(rangeEnd);
        b.putInt(pagePointer);
        b.putFloat(scaleX);
        b.putFloat(scaleY);
        b.putInt(unknownData);
        b.putInt(charsCount);

        int pos = b.position() + (charsCount * 4) + startCount;
        for(int i = 0; i < charsCount; i++) {
            b.putInt(pos + (i * 16));
        }

        for(int i = 0; i < charsCount; i++) {
            FontCharItem ch = chars.get(i);
            b.putShort((short)ch.code);
            b.putShort(ch.posX);
            b.putShort(ch.posY);
            b.putShort(ch.sizeX);
            b.putShort(ch.sizeY);
            b.putShort(ch.shift);
            b.putInt(ch.offset);
        }

        b.rewind();
        return b;
    }

    public void updateSizesPointers() {

        GMSDATA.repPointers.add(new ReplacePointer(pagePointer, 0, true));            // tpage source x = 0
        GMSDATA.repPointers.add(new ReplacePointer(pagePointer + 2, 0, true)); // tpage source y = 0
        GMSDATA.repPointers.add(new ReplacePointer(pagePointer + 4, modPage.source.size.x, true));
        GMSDATA.repPointers.add(new ReplacePointer(pagePointer + 6, modPage.source.size.y, true));
        GMSDATA.repPointers.add(new ReplacePointer(pagePointer + 8, 0, true));  // tpage dest x = 0
        GMSDATA.repPointers.add(new ReplacePointer(pagePointer + 10, 0, true)); // tpage dest y = 0
        GMSDATA.repPointers.add(new ReplacePointer(pagePointer + 12, modPage.target.size.x, true));
        GMSDATA.repPointers.add(new ReplacePointer(pagePointer + 14, modPage.target.size.y, true));
        GMSDATA.repPointers.add(new ReplacePointer(pagePointer + 16, modPage.size.x, true));
        GMSDATA.repPointers.add(new ReplacePointer(pagePointer + 18, modPage.size.y, true));
        GMSDATA.repPointers.add(new ReplacePointer(pagePointer + 20, modPage.textureIndex, true)); // texture index

    }

    public String toString() {
        return name;
    }


}
