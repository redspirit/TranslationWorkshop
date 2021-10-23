package com.workshop.translationworkshop.utils;

import com.workshop.translationworkshop.Application;
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
import javafx.scene.text.Text;

public class Glyph {

    private Canvas canvas;
    private GraphicsContext ctx;
    private Font font;
    public int localWidth, localHeight;

    public Glyph(String fontFileName) {

        canvas = new Canvas(10,10);
        ctx = canvas.getGraphicsContext2D();
//        ctx.setImageSmoothing(false);
        font = Font.loadFont(Application.class.getResource("fonts/" + fontFileName).toExternalForm(), 72);
        ctx.setTextBaseline(VPos.TOP);
        ctx.setFont(font);
        ctx.setFill(Paint.valueOf("white"));

    }

    private Point2D textSizes(String s) {
        Text text = new Text(s);
        text.setFont(font);
        return new Point2D(
                text.getBoundsInLocal().getWidth(),
                text.getBoundsInLocal().getHeight()
        );
    }

    public WritableImage getCharImage(String ch, int size) {
        Point2D sizes = textSizes(ch);
        double ratio = size / sizes.getY();
        localWidth = (int)Math.round(ratio * sizes.getX());
        localHeight = size;

//        System.out.println("Glyph size: " + newWidth + " " + newHeight );

        Canvas cnv2 = new Canvas(localWidth, localHeight);
        GraphicsContext gc = cnv2.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        canvas.setWidth(Math.ceil(sizes.getX()));
        canvas.setHeight(Math.ceil(sizes.getY()));

        ctx.fillText(ch, 0,0);

        gc.drawImage(
                canvasToImage(canvas),
                0, 0,
                sizes.getX(), sizes.getY(),
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
