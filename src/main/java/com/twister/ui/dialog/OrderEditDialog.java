package com.twister.ui.dialog;

import com.twister.configuration.annotation.FXMLController;
import com.twister.entity.Spare;
import com.twister.viewmodel.ProcessedOrder;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FXMLController(fxml = DefaultEditDialog.FXML_PATH)
public class OrderEditDialog extends DefaultEditDialog<ProcessedOrder> {

    private ComboBox<Spare> comboBox;

    @Override
    protected void doBeforeInitialization() {
        var spare = new Spare();
        spare.setId(1L);
        spare.setTitle("Spare 1");
        spare.setCost(new BigDecimal("1.25"));
        comboBox = new ComboBox<>(FXCollections.observableArrayList(spare));
        fieldBox.getChildren().add(comboBox);
    }

    @Override
    protected List<Pair<Text, TextField>> getTextFieldComponents() {
        return List.of(

        );
    }

    @Override
    protected String getDialogCreateTitle() {
        return "";
    }

    @Override
    protected String getDialogUpdateTitle() {
        return "";
    }

    @Override
    protected void onCreateAction(ActionEvent event) {

    }

    @Override
    protected void onUpdateAction(ActionEvent event) {

    }
}
