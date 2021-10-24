package com.workshop.translationworkshop.gms;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class TexturePage {

    public SpriteRect source, target;
    public SpritePoint size;
    public short textureIndex;
    private WritableImage cachedImage;

    public TexturePage(SpriteRect source, SpriteRect target, SpritePoint size, short textureIndex) {
        this.source = source;
        this.target = target;
        this.size = size;
        this.textureIndex = textureIndex; // texture index
    }

    public void clearCache() {
        cachedImage = null;
        getImage();
    }

    public WritableImage getImage() {
        if(cachedImage != null) return cachedImage;
        Texture txt = GMSDATA.txtr.getByIndex(textureIndex);
        PixelReader reader = txt.getImage().getPixelReader();
        cachedImage = new WritableImage(reader, source.position.x, source.position.y, source.size.x, source.size.y);
        return cachedImage;
    }

    public void drawImage(Image img, FontCharItem charItem) {

        cachedImage.getPixelWriter().setPixels(
                charItem.posX, charItem.posY,
                charItem.sizeX, charItem.sizeY,
                img.getPixelReader(),
                0, 0
        );

    }

    public String toString() {
        return textureIndex + " " + source + " " + target + " " + size;
    }
}
