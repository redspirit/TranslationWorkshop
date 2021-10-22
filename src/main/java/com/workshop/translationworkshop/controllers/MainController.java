package com.workshop.translationworkshop.controllers;

import javafx.application.HostServices;
import javafx.stage.Stage;

public class MainController {

    private Stage stage;
    private HostServices hostServices;

    public void onLoad(Stage stage, HostServices hs) {
        this.stage = stage;
        this.hostServices = hs;
    }

}