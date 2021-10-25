package com.workshop.translationworkshop.gms;

import com.workshop.translationworkshop.Application;
import com.workshop.translationworkshop.utils.Glyph;
import com.workshop.translationworkshop.utils.Utils;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FontItem {

    public String name, fontName;
    public float size;
    public boolean isBold, isItalic;
    public int rangeBegin, rangeEnd;
    public int pagePointer;
    public float scaleX, scaleY;
    public int charsCount;
    public List<FontCharItem> chars = new ArrayList<>();
    public int glyphHeight = 0;

    public double customScaleX = 1;
    public double customScaleY = 1;
    public double customOffsetY = 0;
    public int charSpace = 0;

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

            glyphHeight = ch.sizeY;

        }

    }

    public Image getSprite() {
        return getTexturePage().getImage();
    }

    public void restoreSprite() {
        getTexturePage().clearCache();
    }

    public TexturePage getTexturePage() {
        return GMSDATA.tpag.getByAddress(pagePointer);
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

        ctx.setImageSmoothing(false);
        ctx.setFill(Paint.valueOf("#000000"));
        ctx.fillRect(0,0, 512, glyphHeight * scale);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            FontCharItem charItem = getFontCharByCode(ch);

            if(charItem == null) {
                continue; // символ, который не смогли найти - пропускаем
            }

            ctx.drawImage(
                    getSprite(),
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

    public void clearAllCustomChars() {
        chars = chars.stream().filter(it -> !it.isCustom).collect(Collectors.toList());
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
        TexturePage page = getTexturePage();
        FontCharItem lastChar = getLastCharItem();

        int newX = lastChar.posX + lastChar.sizeX + 2;
        int newY = lastChar.posY + lastChar.sizeY + 2;
        if(newX + charWidth > page.size.x) {
            // символ не влазиет в текущую строку, может есть место пониже?

            if(newY + glyphHeight > page.size.y) {
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

    public boolean addNewChar(Font font, char sym) {

        Glyph g = new Glyph(font);
        g.setParams(customScaleX, customScaleY, customOffsetY);
        Image img = g.getCharImage(Character.toString(sym), glyphHeight);
        SpritePoint newPos = findInsertPlace((int) img.getWidth());

        if(newPos == null) return false;

        FontCharItem ch = new FontCharItem();
        ch.code = (short) sym;
        ch.letter = Character.toString(sym);
        ch.posX = newPos.x;
        ch.posY = newPos.y;
        ch.sizeX = (short) img.getWidth();
        ch.sizeY = (short) glyphHeight;
        ch.shift = (short) (ch.sizeX + charSpace);
        ch.offset = 0;
        ch.isCustom = true;
        chars.add(ch);

        TexturePage page = getTexturePage();
        page.drawImage(img, ch);

        return true;
    }

    public void addNewChars(Font font, String str) {

        for (int i = 0; i < str.length(); i++) {
            boolean res = addNewChar(font, str.charAt(i));
            if(!res) {
//                getTexturePage().extendSprite();
//                addNewChars(font, str);
                break;
            }
        }

    }

    public byte[] getSpritePngData() {

        RenderedImage renderedImage = SwingFXUtils.fromFXImage(getSprite(), null);
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

        RenderedImage renderedImage = SwingFXUtils.fromFXImage(getSprite(), null);
        ImageIO.write(renderedImage, "png", new File(filename));

    }

    public FontCharItem getCharItemByPosition(int x, int y) {

        return chars.stream().filter(item -> (x >= item.posX && x <= item.posX + item.sizeX) && (y >= item.posY && y <= item.posY + item.sizeY)).findFirst().orElse(null);

    }

    public String toString() {
        return name;
    }

    public ByteBuffer toBytes() {

        int len = (12 * 4) + (charsCount * 4) + (charsCount * 16);

        System.out.println(len);

        ByteBuffer bb = ByteBuffer.allocate(len);

        return bb;
    }

}
