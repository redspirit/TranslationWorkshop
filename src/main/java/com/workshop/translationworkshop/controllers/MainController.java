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
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    public ListView<FontItem> fontListView;
    public Label fontNameLabelView;
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
        fontListView.getSelectionModel().select(0);
        currentFont = fontListView.getItems().get(0);

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
}