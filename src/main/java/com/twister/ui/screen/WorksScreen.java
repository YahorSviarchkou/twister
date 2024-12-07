package com.twister.ui.screen;

import com.twister.configuration.annotation.FXMLController;
import com.twister.service.WorkService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
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

    @FXML
    void create(ActionEvent event) {
        log.info("test click works");
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL); // Модальное окно
        dialog.initStyle(StageStyle.UTILITY); // Минималистичный стиль
        dialog.setTitle("Добавление нового клиента");

        // Создание сетки для расположения элементов
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Поля для ввода данных
        Label lblName = new Label("ФИО клиента:");
        TextField txtName = new TextField();

        Label lblPhone = new Label("Телефон клиента:");
        TextField txtPhone = new TextField();

        grid.add(lblName, 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(lblPhone, 0, 1);
        grid.add(txtPhone, 1, 1);

        // Кнопки "Сохранить" и "Добавить транспорт"
        Button btnSave = new Button("Сохранить");
        Button btnAddTransport = new Button("+ Добавить транспорт");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(btnSave, btnAddTransport);

        grid.add(buttonBox, 1, 2);

        btnSave.setOnAction(e -> {
            // Логика сохранения данных
            String name = txtName.getText();
            String phone = txtPhone.getText();
            System.out.println("Сохранено: " + name + ", " + phone);
            dialog.close();
        });

        btnAddTransport.setOnAction(e -> {
            // Логика добавления транспорта (возможно открытие нового диалога)
            System.out.println("Добавление транспорта...");
        });

        // Установка сцены и отображение диалога
        Scene dialogScene = new Scene(grid, 400, 200);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("WorksScreen was init");
    }
}
