package com.workshop.translationworkshop.controllers;

import com.workshop.translationworkshop.gms.FontCharItem;
import com.workshop.translationworkshop.gms.FontItem;
import com.workshop.translationworkshop.gms.GMSDATA;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

public class FontController {

    public Canvas canvasView;
    public TextField fontIndexView;

    public void ViewLoaded() {

        canvasView.setOnMousePressed(event -> {
            System.out.println(event.getX() + " " + event.getY());
//            refresh();
        });

        showFont(0);

    }

    public void showFont(int index) {

        FontItem font = GMSDATA.font.getFontByIndex(index);

        Image img = font.getSprite();
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

//        System.out.println(font.getTexturePage());

        canvasView.setWidth(w);
        canvasView.setHeight(h);
        GraphicsContext ctx = canvasView.getGraphicsContext2D();

        ctx.setImageSmoothing(false);
        ctx.setFill(Paint.valueOf("#00587a"));
        ctx.fillRect(0,0, w, h);
        ctx.drawImage(img, 0, 0, w, h);

        // draw symbols
        ctx.setStroke(Paint.valueOf("#ff0000"));
        for(FontCharItem ch : font.chars) {
            ctx.strokeRect(ch.posX, ch.posY, ch.sizeX, ch.sizeY);
        }


    }

    public void onSelectFont(ActionEvent actionEvent) {

        int index = Integer.parseInt(fontIndexView.getText());

        showFont(index);

    }
}
