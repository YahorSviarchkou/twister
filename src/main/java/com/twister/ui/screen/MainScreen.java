package com.twister.ui.screen;

import com.twister.configuration.TwisterProperties;
import com.twister.configuration.annotation.FXMLController;
import com.twister.entity.Client;
import com.twister.repository.ClientRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@Slf4j
@FXMLController(fxml = MainScreen.FXML_PATH)
public class MainScreen extends ScreenController {

    public static final String FXML_PATH = "/fxml/main-screen.fxml";

    @FXML
    private Text appVersion;

    @FXML
    private StackPane contentPane;

    @Autowired
    private TwisterProperties twisterProperties;
    @Autowired
    private ClientRepository clientRepository;

    private Class<? extends ScreenController> currentTab;

    @FXML
    void clickClients(MouseEvent event) {
        if(Objects.isNull(currentTab) || ClientsScreen.class != currentTab) {
            currentTab = ClientsScreen.class;
            setChildFXML(contentPane, ClientsScreen.class);
        }
    }

    @FXML
    void clickWorks(MouseEvent event) {
        if(Objects.isNull(currentTab) || WorksScreen.class != currentTab) {
            currentTab = WorksScreen.class;
            setChildFXML(contentPane, WorksScreen.class);
        }
    }

    private ObservableList<Client> initialData() {
//        var u1 = new User();
//        u1.setLogin("user1");
//        u1.setPassword("password1");
//        userRepository.save(u1);
//
//        var u2 = new User();
//        u2.setLogin("user2");
//        u2.setPassword("password2");
//        userRepository.save(u2);

        return FXCollections.observableArrayList(clientRepository.findAll());
    }

    @FXML
    void createUser(ActionEvent event) {
//        var user = new User();
//        user.setLogin(f1.getText() + ": " + f2.getText());
//        user.setPassword(f3.getText());
//        var dbUser = clientRepository.save(user);
//        table.getItems().add(dbUser);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appVersion.setText(twisterProperties.getFullVersion());
//        tf.setCellValueFactory(new PropertyValueFactory<>("id"));
//        ts.setCellValueFactory(new PropertyValueFactory<>("login"));
//        tt.setCellValueFactory(new PropertyValueFactory<>("password"));
//
//        table.setItems(initialData());
    }
}
