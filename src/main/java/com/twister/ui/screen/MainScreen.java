package com.twister.ui.screen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MainScreen extends ScreenController {

    public static final String FXML_NAME = "/fxml/main-screen.fxml";

    @FXML
    private Button btn;


    @Override
    protected String getFXMLName() {
        return FXML_NAME;
    }

    @FXML
    private void click(ActionEvent event) {
        btn.setText("dsfgdfsuiugnfdksng");
    }
}
