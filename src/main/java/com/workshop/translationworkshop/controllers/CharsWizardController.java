package com.workshop.translationworkshop.controllers;

import com.workshop.translationworkshop.gms.FontCharItem;
import com.workshop.translationworkshop.gms.FontItem;
import com.workshop.translationworkshop.gms.GMSDATA;
import com.workshop.translationworkshop.utils.Glyph;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class CharsWizardController {

    public TextField fontIndexView;
    public Canvas canvasCharSheetView;
    public TextArea charsetTextView;
    public TextField sampleTextView;
    public Slider sliderOffsetView;
    public Slider sliderWidthView;
    public Slider sliderHeightView;
    public Canvas sampleCanvasView;
    private FontItem font;
    private Font localizeFont;

    public void ViewLoaded(FontItem font) {

//        canvasView.setOnMousePressed(event -> {
//            System.out.println(event.getX() + " " + event.getY());
//            refresh();
//        });

        this.font = font;

        localizeFont = Glyph.getFont("Alegreya.ttf");

        drawImage();
//        onSampleTextChange(null);
    }

    public void drawImage() {

        Image img = font.getSprite();
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        canvasCharSheetView.setWidth(w);
        canvasCharSheetView.setHeight(h);
        GraphicsContext ctx = canvasCharSheetView.getGraphicsContext2D();

        ctx.setImageSmoothing(false);
        ctx.setFill(Paint.valueOf("#00587a"));
        ctx.fillRect(0,0, w, h);
        ctx.drawImage(img, 0, 0, w, h);

        // рисуем сеточку
        ctx.setStroke(Color.rgb(255, 0, 0, 0.7));
        for(FontCharItem ch : font.chars) {
            ctx.strokeRect(ch.posX, ch.posY, ch.sizeX, ch.sizeY);
        }

    }

    public void onSampleTextChange(KeyEvent keyEvent) {
        font.getImageByString(sampleTextView.getText(), sampleCanvasView, 1);
    }

    public void onApplyButton(ActionEvent actionEvent) {

        font.restoreSprite();
        font.clearAllCustomChars();

        font.customScaleX = sliderWidthView.getValue() / 100;
        font.customScaleY = sliderHeightView.getValue() / 100;;
        font.customOffsetY = sliderOffsetView.getValue() / 100;

        System.out.println("customScaleX=" + font.customScaleX);
        System.out.println("customScaleY=" + font.customScaleY);
        System.out.println("customOffsetY=" + font.customOffsetY);


        font.addNewChars(localizeFont, charsetTextView.getText());

        drawImage();
        onSampleTextChange(null);


        RenderedImage renderedImage = SwingFXUtils.fromFXImage(font.getSprite(), null);
        try {

            ByteArrayOutputStream ios = new ByteArrayOutputStream();
            ImageIO.write(renderedImage, "png", ios);

            System.out.println( ios.toByteArray().length  );

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void onSaveButton(ActionEvent actionEvent) {



    }
}
