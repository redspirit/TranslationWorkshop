package com.workshop.translationworkshop.controllers;

import com.workshop.translationworkshop.Application;
import com.workshop.translationworkshop.gms.FontCharItem;
import com.workshop.translationworkshop.gms.FontItem;
import com.workshop.translationworkshop.gms.GMSDATA;
import com.workshop.translationworkshop.utils.Glyph;
import com.workshop.translationworkshop.utils.Utils;
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
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainController {

    public ListView<FontItem> fontListView;
    public Label fontNameLabelView;
    public TextField addressView;
    private Stage stage;
    private HostServices hostServices;
    private FontItem currentFont;

    public void onLoad(Stage stage, HostServices hs) {
        this.stage = stage;
        this.hostServices = hs;

//        GMSDATA.loadFile("/home/spirit/hard/TEST/data.win");
//        GMSDATA.loadFile("/Users/spirit/Documents/Garden Story v1.0.3/data.win");
//        GMSDATA.loadFile("/Users/spirit/Documents/data.win");     // DG mac
        GMSDATA.loadFile("D:\\games\\Deaths Gambit Afterlife\\Backup\\garden_data.win");   // DG PC

        fontListView.getItems().setAll(GMSDATA.getFonts());
        fontListView.getSelectionModel().select(0);
        currentFont = fontListView.getItems().get(0);



        loadNewSprite();


        ByteBuffer binary = GMSDATA.assemblyBinary();
        try {
            GMSDATA.saveBufferToWIN(binary, "D:\\games\\Deaths Gambit Afterlife\\Backup\\all_data.win");
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Saved all!");

    }

    public void loadNewSprite() {

        byte[] imgBytes;
        try {
            imgBytes = Files.readAllBytes(Path.of("D:\\games\\Deaths Gambit Afterlife\\Backup\\new-texture.png"));
        } catch (IOException e) {
            e.printStackTrace(); return;
        }

        GMSDATA.txtr.addSprite(imgBytes);

    }

    public void onFontListClick(MouseEvent mouseEvent) {

        ObservableList<FontItem> items = fontListView.getSelectionModel().getSelectedItems();
        if(items.size() == 0) return;

        currentFont = items.get(0);

    }

    public void onAddCharsButton(ActionEvent actionEvent) throws IOException {

        Stage stage = new Stage();
        FXMLLoader fxml = new FXMLLoader(Application.class.getResource("chars-wizard-view.fxml"));
        Scene scene = new Scene(fxml.load());
        stage.setScene(scene);
        stage.setTitle("Font chars wizard - " + currentFont.fontName);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.stage);
        stage.setResizable(true);
        CharsWizardController ctrl = fxml.getController();
        ctrl.ViewLoaded(currentFont);
        stage.show();

    }

    public void onFIND(ActionEvent actionEvent) {

        int count = Utils.findAddress(GMSDATA.getBuffer(), Integer.parseInt(addressView.getText()) );

        System.out.println("Count = " + count);

    }
}