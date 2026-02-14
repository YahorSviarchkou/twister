package com.twister.ui.dialog;

import com.twister.ui.TableModel;
import com.twister.ui.components.ValidatableField;
import com.twister.ui.screen.ScreenController;
import com.twister.util.I18nUtils;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

@Slf4j
@Component
public abstract class DefaultEditDialog<T extends TableModel> extends ScreenController {

    public static final String FXML_PATH = "/fxml/dialogs/default-edit-dialog.fxml";

    @Autowired
    protected I18nUtils i18nUtils;

    @Setter
    protected T editModel;
    @Setter
    protected DialogMode dialogMode = DialogMode.CREATE;

    @FXML
    protected VBox fieldBox;
    @FXML
    protected Text dialogTitle;
    @FXML
    protected Button saveButton;
    @FXML
    protected AnchorPane anchorPane;

    protected final Set<BooleanProperty> validations = new HashSet<>();

    protected abstract void doBeforeInitialization();

    protected abstract String getDialogCreateTitle();

    protected abstract String getDialogUpdateTitle();

    protected abstract void onCreateAction(ActionEvent event);

    protected abstract void onUpdateAction(ActionEvent event);

    protected abstract List<Pair<Text, TextField>> getTextFieldComponents();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        validations.clear();
        fieldBox.getChildren().clear();

        doBeforeInitialization();
        initDynamicComponents();

        if (dialogMode == DialogMode.CREATE) {
            initCreateModeDialog();
        } else if (dialogMode == DialogMode.UPDATE) {
            initUpdateModeDialog();
        }

        initKeysListeners();

        log.info("{} was init with mode:{}", this.getClass().getSimpleName(), dialogMode.name());
    }

    private void initCreateModeDialog() {
        dialogTitle.setText(getDialogCreateTitle());
        saveButton.setText(i18nUtils.get("add.button"));
    }

    private void initUpdateModeDialog() {
        dialogTitle.setText(getDialogUpdateTitle());
        saveButton.setText(i18nUtils.get("update.button"));
    }

    @FXML
    void onSaveAction(ActionEvent event) {
        if (dialogMode == DialogMode.UPDATE) {
            onUpdateAction(event);
        } else {
            onCreateAction(event);
        }

        onCancelAction(event);
    }

    @FXML
    void onCancelAction(ActionEvent event) {
        getTextFieldComponents().forEach(component -> {

        });

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void initKeysListeners() {
        anchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Stage stage = (Stage) anchorPane.getScene().getWindow();
                stage.close();
            }
        });

        Platform.runLater(() -> anchorPane.requestFocus());
    }

    private Pair<Text, TextField> initTextFieldPair(Pair<Text, TextField> component) {
        var text = component.getKey();
        text.setFont(new Font(15));
        text.setStrokeType(StrokeType.OUTSIDE);
        VBox.setMargin(text, new Insets(0, 0, 10, 0));

        var textField = component.getValue();
        textField.setPrefWidth(555);
        textField.setPrefHeight(25);
        VBox.setMargin(textField, new Insets(0, 0, 20, 0));

        return component;
    }

    private void initDynamicComponents() {
        var textFieldComponents = getTextFieldComponents();
        textFieldComponents
                .stream()
                .map(this::initTextFieldPair)
                .forEach(component -> {
                    var label = component.getKey();
                    var textField = component.getValue();

                    if (textField instanceof ValidatableField validatableField) {
                        validations.add(validatableField.validProperty());
                    }

                    fieldBox.getChildren().add(label);
                    fieldBox.getChildren().add(textField);
                });

        BooleanBinding allValidBinding = Bindings.createBooleanBinding(
                () -> validations.stream().allMatch(BooleanProperty::get),
                validations.toArray(new Observable[0])
        );

        saveButton.disableProperty().bind(Bindings.not(allValidBinding));
    }


}
