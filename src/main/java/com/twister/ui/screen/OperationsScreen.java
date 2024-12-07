package com.twister.ui.screen;

import com.twister.configuration.annotation.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@FXMLController(fxml = OperationsScreen.FXML_PATH)
public class OperationsScreen extends ScreenController {

    public static final String FXML_PATH = "/fxml/operations-screen.fxml";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("OperationsScreen was init");
    }

    @FXML
    public void createOperation(ActionEvent actionEvent) {
    }
}
