package com.workshop.translationworkshop.controllers;

import com.workshop.translationworkshop.Application;
import com.workshop.translationworkshop.gms.FontItem;
import com.workshop.translationworkshop.gms.GMSDATA;
import com.workshop.translationworkshop.gms.TexturePage;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainController {

    public ListView<FontItem> fontListView;
    public Label fontNameLabelView;
    public ImageView previewFontImageView;
    public Label spriteInfoLabelView;
    private Stage stage;
    private HostServices hostServices;
    private FontItem currentFont;

    public void onLoad(Stage stage, HostServices hs) {
        this.stage = stage;
        this.hostServices = hs;

//        GMSDATA.loadFile("/home/spirit/hard/TEST/data.win");
//        GMSDATA.loadFile("/Users/spirit/Documents/Garden Story v1.0.3/data.win");
//        GMSDATA.loadFile("/Users/spirit/Documents/data.win");     // DG mac
        GMSDATA.loadFile("D:\\games\\Deaths Gambit Afterlife\\Backup\\data.win");   // DG PC

        fontListView.getItems().setAll(GMSDATA.getFonts());
        fontListView.getSelectionModel().select(0);
        currentFont = fontListView.getItems().get(0);
        previewFontImageView.setImage(currentFont.modPage.image);
        TexturePage tp = currentFont.modPage;
        spriteInfoLabelView.setText(tp.size.x + "x" + tp.size.y);

    }


    public void onFontListClick(MouseEvent mouseEvent) {

        ObservableList<FontItem> items = fontListView.getSelectionModel().getSelectedItems();
        if(items.size() == 0) return;

        currentFont = items.get(0);

        TexturePage tp = currentFont.modPage;
//        System.out.println("source " + tp.source);
//        System.out.println("target " + tp.target);
//        System.out.println("size " + tp.size);

        previewFontImageView.setImage(currentFont.modPage.image);
        spriteInfoLabelView.setText(tp.size.x + "x" + tp.size.y);

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
        ctrl.viewLoaded(currentFont);
        stage.show();

    }


    public void onSaveToWin(ActionEvent actionEvent) {

        ByteBuffer binary = GMSDATA.assemblyBinary();
        try {
            GMSDATA.saveBufferToWIN(binary, "D:\\games\\Deaths Gambit Afterlife\\data.win");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saved all!");

    }

}