package com.twister.ui.screen;

import com.twister.configuration.ControllerFXML;
import com.twister.repository.ClientRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@ControllerFXML(fxml = "/fxml/main-screen.fxml")
@RequiredArgsConstructor
public class MainScreen extends ScreenController {

    private final ClientRepository clientRepository;

    @FXML
    private Button btn;

    @FXML
    private void click(ActionEvent event) {
        btn.setText("ывавыаыва");
    }
}
