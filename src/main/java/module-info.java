module com.workshop.translationworkshop {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.workshop.translationworkshop to javafx.fxml;
    exports com.workshop.translationworkshop;
    exports com.workshop.translationworkshop.controllers;
    opens com.workshop.translationworkshop.controllers to javafx.fxml;
}