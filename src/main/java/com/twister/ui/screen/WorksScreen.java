package com.twister.ui.screen;

import com.twister.configuration.annotation.FXMLController;
import com.twister.service.WorkService;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@FXMLController(fxml = WorksScreen.FXML_PATH)
@RequiredArgsConstructor
public class WorksScreen extends ScreenController {

    public static final String FXML_PATH = "/fxml/works-screen.fxml";

    @Autowired
    private WorkService workService;

//    @FXML
//    void testClick(MouseEvent event) {
//        log.info("test click works");
//    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("WorksScreen was init");
    }
}
