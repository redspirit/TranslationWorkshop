package com.workshop.translationworkshop.utils;

import com.workshop.translationworkshop.Application;
import com.workshop.translationworkshop.gms.SpritePoint;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

public class Glyph {

    private Canvas canvas;
    private GraphicsContext ctx;
    private Font font;
    public int localWidth, localHeight;

    private float customScaleX = 1;
    private float customScaleY = 1;
    private float customOffsetY = 0;

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

    public void setParams(float customScaleX, float customScaleY, float customOffsetY) {
        this.customScaleX = customScaleX;
        this.customScaleY = customScaleY;
        this.customOffsetY = customOffsetY;
    }

    private SpritePoint textSizes(String s) {
        Text text = new Text(s);
        text.setFont(font);
        return new SpritePoint(
                (int) text.getBoundsInLocal().getWidth(),
                (int) text.getBoundsInLocal().getHeight()
        );
    }

    public WritableImage getCharImage(String ch, int size) {
        SpritePoint sizes = textSizes(ch);
        double ratio = (size + 10) / (double)sizes.y;
        localWidth = (int)Math.round(ratio * sizes.x);
        localHeight = size;

//        System.out.println("Glyph size: " + newWidth + " " + newHeight );

        Canvas cnv2 = new Canvas(localWidth, localHeight);
        GraphicsContext gc = cnv2.getGraphicsContext2D();
        gc.setImageSmoothing(true);

        canvas.setWidth(sizes.x);
        canvas.setHeight(sizes.y);

        ctx.fillText(ch, 0,0);

        gc.drawImage(
                canvasToImage(canvas),
                0, 0,
                sizes.x, sizes.y,
                0, 0,
                localWidth, localHeight
        );

        return canvasToImage(cnv2);

    }

    public WritableImage canvasToImage(Canvas c) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return c.snapshot(params, null);
    }

}
