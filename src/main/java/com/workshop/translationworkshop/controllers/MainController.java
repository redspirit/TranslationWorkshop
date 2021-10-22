package com.workshop.translationworkshop.controllers;

import com.workshop.translationworkshop.gms.FontCharItem;
import com.workshop.translationworkshop.gms.FontItem;
import com.workshop.translationworkshop.gms.GMSDATA;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class MainController {

    public Canvas canvasView;
    public ListView<FontItem> fontListView;
    public Canvas testCanvasView;
    private Stage stage;
    private HostServices hostServices;

    public void onLoad(Stage stage, HostServices hs) {
        this.stage = stage;
        this.hostServices = hs;

        GMSDATA.loadFile("/home/spirit/hard/TEST/data.win");

        fontListView.getItems().setAll(GMSDATA.getFonts());

    }

    public void onFontListClick(MouseEvent mouseEvent) {

        ObservableList<FontItem> items = fontListView.getSelectionModel().getSelectedItems();
        if(items.size() > 0) {
            FontItem font = items.get(0);

            Image img = font.getSprite();
            int w = (int) img.getWidth();
            int h = (int) img.getHeight();

            canvasView.setWidth(w);
            canvasView.setHeight(h);
            GraphicsContext ctx = canvasView.getGraphicsContext2D();

            ctx.setImageSmoothing(false);
            ctx.setFill(Paint.valueOf("#00587a"));
            ctx.fillRect(0,0, w, h);
            ctx.drawImage(img, 0, 0, w, h);

//            ctx.setStroke(Paint.valueOf("#ff0000"));
//            for(FontCharItem ch : font.chars) {
//                ctx.strokeRect(ch.posX, ch.posY, ch.sizeX, ch.sizeY);
//            }

            font.getImageByString("Hello Spirit!", testCanvasView);

        }

    }
}