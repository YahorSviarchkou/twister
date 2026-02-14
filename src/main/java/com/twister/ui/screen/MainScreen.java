package com.twister.ui.screen;

import com.twister.configuration.TwisterProperties;
import com.twister.configuration.annotation.FXMLController;
import com.twister.repository.CustomerRepository;
import com.twister.ui.ScreenManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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

    @Autowired
    private ApplicationContext context;
    @Autowired
    private ScreenManager screenManager;
    @Autowired
    private TwisterProperties twisterProperties;
    @Autowired
    private CustomerRepository customerRepository;

    @FXML
    private Text appVersion;
    @FXML
    private StackPane contentPane;
    @FXML
    private ImageView imageView;

    private Class<? extends ScreenController> currentTab;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appVersion.setText(twisterProperties.getFullVersion());
        imageView.setFitHeight(contentPane.getHeight());
    }

    @FXML
    void clickClients(MouseEvent event) {
        showTab(CustomersTabScreen.class);
    }

    @FXML
    void clickWorks(MouseEvent event) {
        showTab(OrdersTabScreen.class);
    }

    @FXML
    void clickOperations(MouseEvent mouseEvent) {
        showTab(OperationsTabScreen.class);
    }

    @FXML
    void clickSpares(MouseEvent mouseEvent) {
        showTab(SparesTabScreen.class);
    }

    @FXML
    void clickExit(MouseEvent mouseEvent) {
        showCloseDialog();
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
            Platform.exit();
        }
    }

    private void showTab(Class<? extends ScreenController> tabClass) {
        if (Objects.isNull(currentTab) || tabClass != currentTab) {
            currentTab = tabClass;
            contentPane.getChildren().clear();
            screenManager.loadChildFxml(contentPane, tabClass);
        }
    }
}
