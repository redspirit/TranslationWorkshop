package com.workshop.translationworkshop.utils;

import com.workshop.translationworkshop.Application;
import com.workshop.translationworkshop.gms.SpritePoint;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class Glyph {

    private Canvas canvas;
    private GraphicsContext ctx;
    private Font font;
    public int localWidth, localHeight;

    private double customScaleX;
    private double customScaleY;
    private double customOffsetY;

    static public Font getFont(String fontFileName) {
        return Font.loadFont(Application.class.getResource("fonts/" + fontFileName).toExternalForm(), 72);
    }

    public Glyph(Font font) {

        this.font = font;
        canvas = new Canvas(10,10);
        ctx = canvas.getGraphicsContext2D();
        ctx.setImageSmoothing(true);
//        ctx.setFontSmoothingType(FontSmoothingType.LCD);
        ctx.setTextBaseline(VPos.TOP);
        ctx.setFont(font);
        ctx.setFill(Paint.valueOf("white"));

    }

    public void setParams(double customScaleX, double customScaleY, double customOffsetY) {
        this.customScaleX = customScaleX;
        this.customScaleY = customScaleY;
        this.customOffsetY = customOffsetY;
    }

    private Point2D textSizes(String s) {
        Text text = new Text(s);
        text.setFont(font);
        return new Point2D(
                text.getBoundsInLocal().getWidth(),
                text.getBoundsInLocal().getHeight()
        );
    }

    public WritableImage getCharImagePixel(String ch, int sizeY) {

//        int sizeX = sizeY;

        Canvas cnv = new Canvas(sizeY, sizeY);
        GraphicsContext gc = cnv.getGraphicsContext2D();
        gc.setFont(font);
        gc.setFill(Paint.valueOf("white"));
        gc.setImageSmoothing(false);

        gc.fillText(ch, 0, sizeY + customOffsetY);

        WritableImage wi = canvasToImage(cnv);
        PixelReader reader = wi.getPixelReader();
        PixelWriter writer = gc.getPixelWriter();

        int charWidth = 0;
        Color clr = new Color(0,0,0, 0.0);
        for(int x = 0; x < sizeY; x++) {
            for(int y = 0; y < sizeY; y++) {
                boolean isPixel = !reader.getColor(x,y).isOpaque();
                if(isPixel) {
                    writer.setColor(x, y, clr);
                } else {
                    if(x + 1 > charWidth) charWidth = x + 1;
                }
            }
        }

        cnv.setWidth(charWidth + 1);

        return canvasToImage(cnv);

    }

    public void savePngToFile(Image img, String filename) throws IOException {

        RenderedImage renderedImage = SwingFXUtils.fromFXImage(img, null);
        ImageIO.write(renderedImage, "png", new File(filename));

    }

    public WritableImage getCharImage(String ch, int size) {
//        Point2D sizes = textSizes(ch);
//        double ratio = (size / (double)sizes.y) * customScaleX;
//        localWidth = (int)Math.round(ratio * sizes.x);
//        localHeight = size;
//
//        Canvas cnv2 = new Canvas(localWidth, localHeight);
//        GraphicsContext gc = cnv2.getGraphicsContext2D();
//        gc.setImageSmoothing(true);
//
//        canvas.setWidth(sizes.x);
//        canvas.setHeight(sizes.y);
//
//        ctx.fillText(ch, 0, customOffsetY * localHeight);
//
//        double hOff = sizes.y * customScaleY;
//
//        gc.drawImage(
//                canvasToImage(canvas),
//                0, hOff,
//                sizes.x, sizes.y - hOff,
//                0, 0,
//                localWidth, localHeight
//        );
//
//        return canvasToImage(cnv2);
        return null;
    }

    public WritableImage canvasToImage(Canvas c) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return c.snapshot(params, null);
    }

}
