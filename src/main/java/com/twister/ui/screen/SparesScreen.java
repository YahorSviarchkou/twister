package com.twister.ui.screen;

import com.twister.configuration.annotation.FXMLController;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@FXMLController(fxml = TransportsScreen.FXML_PATH)
public class SparesScreen extends ScreenController {

    public static final String FXML_PATH = "/fxml/spares-screen.fxml";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
