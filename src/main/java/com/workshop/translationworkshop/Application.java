package com.workshop.translationworkshop;

import com.workshop.translationworkshop.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlMain = new FXMLLoader(Application.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlMain.load());
        stage.setTitle("Translation Workshop v0.0.1");
        stage.setScene(scene);
        stage.show();

        MainController mainController = fxmlMain.getController();
        mainController.onLoad(stage, getHostServices());

    }

    public static void main(String[] args) {
        launch();
    }
}