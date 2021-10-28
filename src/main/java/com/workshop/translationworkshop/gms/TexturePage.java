package com.workshop.translationworkshop.gms;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class TexturePage {

    public SpriteRect source, target;
    public SpritePoint size;
    public int address;
    public short textureIndex;
    public boolean isExtended = false;
    public WritableImage image;

    public TexturePage(SpriteRect source, SpriteRect target, SpritePoint size, short textureIndex, int address) {
        this.source = source;
        this.target = target;
        this.size = size;
        this.address = address;
        this.textureIndex = textureIndex; // texture index
        this.image = loadImage();
    }

    private WritableImage loadImage() {
        Texture txt = GMSDATA.txtr.getByIndex(textureIndex);
        PixelReader reader = txt.getImage().getPixelReader();
        return new WritableImage(reader, source.position.x, source.position.y, source.size.x, source.size.y);
    }

    public void extendSprite() {

        // увеличиваем спрайт шрифта в 2 раза по высоте
        if(isExtended) return; // нельзя увеличить спрайт шрифта более одного раза
        isExtended = true;

        PixelReader reader = image.getPixelReader();
        image = new WritableImage(size.x, size.y * 2);
        image.getPixelWriter().setPixels(0, 0, size.x, size.y, reader, 0, 0);
        size.y = (short) (size.y * 2);
        source.position.x = 0;
        source.position.y = 0;
        source.size = size;
        target.position.x = 0;
        target.position.y = 0;
        target.size = size;

    }

    public void drawImage(Image img, FontCharItem charItem) {

        // рисуем на указанной картинке указанный символ из спрайта шрифта

        image.getPixelWriter().setPixels(
                charItem.posX, charItem.posY,
                charItem.sizeX, charItem.sizeY,
                img.getPixelReader(),
                0, 0
        );

    }

    public TexturePage clone() {
        return new TexturePage(source.clone(), target.clone(), size.clone(), textureIndex, address);
    }

    public String toString() {
        return textureIndex + " " + source + " " + target + " " + size;
    }
}
