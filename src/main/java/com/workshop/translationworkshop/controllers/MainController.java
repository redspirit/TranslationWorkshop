package com.workshop.translationworkshop.controllers;

import com.workshop.translationworkshop.Application;
import com.workshop.translationworkshop.gms.FontCharItem;
import com.workshop.translationworkshop.gms.FontItem;
import com.workshop.translationworkshop.gms.GMSDATA;
import com.workshop.translationworkshop.utils.Glyph;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    public Canvas canvasView;
    public ListView<FontItem> fontListView;
    public Canvas testCanvasView;
    public TextField testTextView;
    public Label fontNameLabelView;
    public Canvas glyphCanvasView;
    private Stage stage;
    private HostServices hostServices;
    private FontItem currentFont;

    public void onLoad(Stage stage, HostServices hs) {
        this.stage = stage;
        this.hostServices = hs;

//        GMSDATA.loadFile("/home/spirit/hard/TEST/data.win");
//        GMSDATA.loadFile("/Users/spirit/Documents/Garden Story v1.0.3/data.win");
        GMSDATA.loadFile("/Users/spirit/Documents/data.win");

        fontListView.getItems().setAll(GMSDATA.getFonts());


        Glyph g = new Glyph("Alegreya.ttf");
        Image img = g.getCharImage("я", 32);

        System.out.println("IMG " + img.getWidth() + " x " +  img.getHeight());

        glyphCanvasView.setHeight(img.getHeight());
        glyphCanvasView.setWidth(img.getWidth());
        GraphicsContext c = glyphCanvasView.getGraphicsContext2D();
        c.setFill(Paint.valueOf("black"));
        c.fillRect(0, 0, img.getHeight(), img.getHeight());
        c.drawImage(img, 0, 0);

    }

    public void onFontListClick(MouseEvent mouseEvent) {

        ObservableList<FontItem> items = fontListView.getSelectionModel().getSelectedItems();
        if(items.size() > 0) {
            FontItem font = items.get(0);

            currentFont = font;

            Image img = font.getSprite();
            int w = (int) img.getWidth();
            int h = (int) img.getHeight();

            canvasView.setWidth(w);
            canvasView.setHeight(h);
            GraphicsContext ctx = canvasView.getGraphicsContext2D();

            fontNameLabelView.setText("Name: " + font.fontName + ". Size: "+w+"x"+h+" px. Glyph height: "
                    +font.glyphHeight+" px");

            ctx.setImageSmoothing(false);
            ctx.setFill(Paint.valueOf("#00587a"));
            ctx.fillRect(0,0, w, h);
            ctx.drawImage(img, 0, 0, w, h);

            ctx.setStroke(Paint.valueOf("#ff0000"));
            for(FontCharItem ch : font.chars) {
                ctx.strokeRect(ch.posX, ch.posY, ch.sizeX, ch.sizeY);
            }

            testTextView.setText("Hello World");
            onTestTextChanged(null);
        }

    }


    public void onTestTextChanged(KeyEvent keyEvent) {

        if(currentFont != null) {
            currentFont.getImageByString(testTextView.getText(), testCanvasView);
        }

    }

    public void onAddCharsButton(ActionEvent actionEvent) throws IOException {

        Stage stage = new Stage();
        FXMLLoader fxml = new FXMLLoader(Application.class.getResource("chars-wizard-view.fxml"));
        Scene scene = new Scene(fxml.load());
        stage.setScene(scene);
        stage.setTitle("Font chars wizard");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.stage);
        stage.setResizable(false);
        CharsWizardController ctrl = fxml.getController();
        ctrl.ViewLoaded();
        stage.show();

    }
}