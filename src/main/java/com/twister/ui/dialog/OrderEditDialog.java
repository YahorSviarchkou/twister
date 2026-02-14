package com.twister.ui.dialog;

import com.twister.configuration.annotation.FXMLController;
import com.twister.payload.ProcessedOrder;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FXMLController(fxml = DefaultEditDialog.FXML_PATH)
public class OrderEditDialog extends DefaultEditDialog<ProcessedOrder> {


    @Override
    protected void doBeforeInitialization() {

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

    @Override
    protected List<Pair<Text, TextField>> getTextFieldComponents() {
        return List.of();
    }
}
