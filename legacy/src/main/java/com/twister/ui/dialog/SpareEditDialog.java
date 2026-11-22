package com.twister.ui.dialog;

import com.twister.configuration.annotation.FXMLController;
import com.twister.entity.Spare;
import com.twister.service.SpareService;
import com.twister.ui.components.DoubleField;
import com.twister.ui.components.StringField;
import javafx.event.ActionEvent;
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
public class SpareEditDialog extends DefaultEditDialog<Spare> {

    private final SpareService spareService;

    private Text costLabel;
    private Text titleLabel;
    private DoubleField costField;
    private StringField titleField;

    @Override
    protected void doBeforeInitialization() {
        costLabel = new Text();
        titleLabel = new Text();
        costField = new DoubleField();
        titleField = new StringField();

        costLabel.setText(i18nUtils.get("spare.dialog.cost.label"));
        titleLabel.setText(i18nUtils.get("spare.dialog.title.label"));

        costField.setId("costField");
        titleField.setId("titleField");

        costField.setPromptText(i18nUtils.get("spare.dialog.cost.prompt"));
        titleField.setPromptText(i18nUtils.get("spare.dialog.title.prompt"));

        if (dialogMode == DialogMode.UPDATE) {
            titleField.setText(editModel.getTitle());
            costField.setText(editModel.getCost().toString());

            titleField.validProperty().set(true);
            costField.validProperty().set(true);
        }
    }

    @Override
    protected String getDialogCreateTitle() {
        return i18nUtils.get("spare.dialog.create.title");
    }

    @Override
    protected String getDialogUpdateTitle() {
        return i18nUtils.get("spare.dialog.update.title");
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
        spareService.createSpare(createSpareFromFields());
    }

    @Override
    protected void onUpdateAction(ActionEvent event) {
        spareService.updateSpare(editModel, createSpareFromFields());
    }

    private Spare createSpareFromFields() {
        var name = titleField.getText();
        var cost = costField.getText();

        Spare spare = new Spare();
        spare.setTitle(name);
        spare.setCost(new BigDecimal(cost));

        return spare;
    }
}