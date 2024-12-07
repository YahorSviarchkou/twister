package com.twister.ui.screen;

import com.twister.configuration.TwisterProperties;
import com.twister.configuration.annotation.FXMLController;
import com.twister.entity.Client;
import com.twister.repository.ClientRepository;
import com.twister.ui.ScreenManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
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
    private ApplicationContext context;
    @Autowired
    private ScreenManager screenManager;
    @Autowired
    private TwisterProperties twisterProperties;
    @Autowired
    private ClientRepository clientRepository;

    private Class<? extends ScreenController> currentTab;

    @FXML
    void clickClients(MouseEvent event) {
        showTab(ClientsScreen.class);
    }

    @FXML
    void clickWorks(MouseEvent event) {
        showTab(WorksScreen.class);
    }

    @FXML
    void clickOperations(MouseEvent mouseEvent) {
        showTab(OperationsScreen.class);
    }

    @FXML
    void clickSpares(MouseEvent mouseEvent) {
        showTab(SparesScreen.class);
    }

    @FXML
    void clickExit(MouseEvent mouseEvent) {
        showCloseDialog();
    }

    @FXML
    void createUser(ActionEvent event) {
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

    private void showCloseDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Подтверждение закрытия");
        dialog.setHeaderText("Вы действительно хотите закрыть это окно?");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE, ButtonType.CANCEL);
        dialogPane.setContent(new Label("Нажмите OK для закрытия или Cancel для отмены."));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.CLOSE) {
            Platform.exit();;
        }
    }

    private void showTab(Class<? extends ScreenController> tabClass) {
        if(Objects.isNull(currentTab) || tabClass != currentTab) {
            currentTab = tabClass;
            screenManager.loadChildFxml(contentPane, tabClass);
        }
    }
}
