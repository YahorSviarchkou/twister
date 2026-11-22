package com.twister.ui.dialog;

import com.twister.configuration.annotation.FXMLController;
import com.twister.entity.Operation;
import com.twister.service.OperationService;
import com.twister.ui.components.DoubleField;
import com.twister.ui.components.StringField;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@FXMLController(fxml = DefaultEditDialog.FXML_PATH)
public class OperationEditDialog extends DefaultEditDialog<Operation> {

    private final OperationService operationService;
    private Text costLabel;
    private Text titleLabel;
    private DoubleField costField;
    private StringField titleField;

    public OperationEditDialog(OperationService operationService) {
        super();
        this.operationService = operationService;
    }

    @Override
    protected void doBeforeInitialization() {
        costLabel = new Text();
        titleLabel = new Text();
        costField = new DoubleField();
        titleField = new StringField();

        costLabel.setText(i18nUtils.get("operation.dialog.cost.label"));
        titleLabel.setText(i18nUtils.get("operation.dialog.title.label"));

        costField.setId("costField");
        titleField.setId("titleField");

        costField.setPromptText(i18nUtils.get("operation.dialog.cost.prompt"));
        titleField.setPromptText(i18nUtils.get("operation.dialog.title.prompt"));

        if (dialogMode == DialogMode.UPDATE) {
            titleField.setText(editModel.getTitle());
            costField.setText(editModel.getCost().toString());

            titleField.validProperty().set(true);
            costField.validProperty().set(true);
        }
    }

    @Override
    protected String getDialogCreateTitle() {
        return i18nUtils.get("operation.dialog.create.title");
    }

    @Override
    protected String getDialogUpdateTitle() {
        return i18nUtils.get("operation.dialog.update.title");
    }

    @Override
    protected List<Pair<Text, TextField>> getTextFieldComponents() {
        return List.of(
                new Pair<>(titleLabel, titleField),
                new Pair<>(costLabel, costField)
        );
    }

    @Override
    protected void onCreateAction(ActionEvent event) {
        operationService.createOperation(createOperationFromFields());
    }

    @Override
    protected void onUpdateAction(ActionEvent event) {
        operationService.updateOperation(editModel, createOperationFromFields());
    }

    private Operation createOperationFromFields() {
        var name = titleField.getText();
        var cost = costField.getText();

        Operation operation = new Operation();
        operation.setTitle(name);
        operation.setCost(new BigDecimal(cost));

        return operation;
    }
}
