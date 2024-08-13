package com.twister.ui.screen;

import com.twister.configuration.annotation.FXMLController;
import com.twister.service.ClientService;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@FXMLController(fxml = ClientsScreen.FXML_PATH)
public class ClientsScreen extends ScreenController {

    public static final String FXML_PATH = "/fxml/clients-screen.fxml";

    @Autowired
    private ClientService clientService;

    @FXML
    void testClick(MouseEvent event) {
        log.info("test click clients");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("ClientsScreen was init");
//        clientService.findAllClients(0, 1, Sort.unsorted());
//        clientService.findAllClients()
    }
}
