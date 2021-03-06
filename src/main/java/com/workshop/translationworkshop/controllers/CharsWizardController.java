package com.workshop.translationworkshop.controllers;

import com.workshop.translationworkshop.gms.FontCharItem;
import com.workshop.translationworkshop.gms.FontItem;
import com.workshop.translationworkshop.gms.GMSDATA;
import com.workshop.translationworkshop.gms.ReplacePointer;
import com.workshop.translationworkshop.utils.Glyph;
import com.workshop.translationworkshop.utils.TTFData;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Stage;


public class CharsWizardController {

    public Canvas canvasCharSheetView;
    public TextArea charsetTextView;
    public TextField sampleTextView;
    public Slider sliderOffsetView;
    public Slider sliderWidthView;
    public Slider sliderHeightView;
    public Canvas sampleCanvasView;
    public TextField textCharInfoView;
    public ChoiceBox<TTFData> fontsListView;
    public Spinner<Integer> pixelFontZoomView = new Spinner<>();
    public CheckBox is2XSizeFontView;
    private FontItem font;
    private TTFData localizeFont;

    public void viewLoaded(FontItem font) {

        canvasCharSheetView.setOnMousePressed(event -> {
            FontCharItem item = font.getCharItemByPosition((int)event.getX(), (int)event.getY());
            if(item == null) return;
            textCharInfoView.setText(item.letter + " " + item.code + " " + item.posX + " " + item.posY + " " + item.sizeX + " " + item.sizeY + " " + item.shift);
        });


        pixelFontZoomView.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,5, 1));
        pixelFontZoomView.valueProperty().addListener((obs, oldValue, newValue) -> {
            onSampleTextChange(null);
        });


        fontsListView.getSelectionModel().select(0);
//        localizeFont = TTFData.fonts.get(0).font;

        this.font = font;

        sliderWidthView.setValue(font.customScaleX * 100);
        sliderHeightView.setValue(font.customScaleY * 100);
        sliderOffsetView.setValue(font.customOffsetY * 100);

        drawImage();

        fontsListView.getItems().setAll(TTFData.fonts);
        fontsListView.setOnAction(event -> {
            localizeFont = fontsListView.getValue();
//            onApplyButton(null);
        });

    }

    public void drawImage() {

        Image img = font.modPage.image;
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        canvasCharSheetView.setWidth(w);
        canvasCharSheetView.setHeight(h);
        GraphicsContext ctx = canvasCharSheetView.getGraphicsContext2D();

        ctx.setImageSmoothing(true);
        ctx.setFill(Paint.valueOf("#00587a"));
        ctx.fillRect(0,0, w, h);
        ctx.drawImage(img, 0, 0, w, h);

        // ???????????? ??????????????
        ctx.setStroke(Color.rgb(255, 0, 0, 0.7));
        for(FontCharItem ch : font.chars) {
            ctx.strokeRect(ch.posX, ch.posY, ch.sizeX, ch.sizeY);
        }

    }

    public void onSampleTextChange(KeyEvent keyEvent) {
        font.getImageByString(sampleTextView.getText(), sampleCanvasView, pixelFontZoomView.getValue());
    }

    public void onApplyButton(ActionEvent actionEvent) {

        font.reset();

        font.customScaleX = sliderWidthView.getValue() / 100;
        font.customScaleY = sliderHeightView.getValue() / 100;;
        font.customOffsetY = sliderOffsetView.getValue() / 100;


        boolean is2XFont = is2XSizeFontView.isSelected();
        boolean done = font.addNewChars(localizeFont, charsetTextView.getText(), is2XFont);
        if(!done) {

            font.reset();
            font.modPage.extendSprite();
            font.addNewChars(localizeFont, charsetTextView.getText(), is2XFont);

        }

        drawImage();
        onSampleTextChange(null);

    }

    public void onSaveButton(ActionEvent actionEvent) {

        if(font.spriteStored) {
            GMSDATA.txtr.updateSprite(font.getSpritePngData(), font.modPage.textureIndex);
        } else {
            font.modPage.textureIndex = GMSDATA.txtr.addSprite(font.getSpritePngData());
        }


        font.updateSizesPointers();

        font.spriteStored = true;

        Stage stage = (Stage) charsetTextView.getScene().getWindow();
        stage.close();

    }

    public void onConfigChanged(MouseEvent mouseEvent) {

        onApplyButton(null);

    }

}
