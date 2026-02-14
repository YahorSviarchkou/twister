package com.twister.ui.dialog;

import com.twister.configuration.annotation.FXMLController;
import com.twister.entity.Customer;
import com.twister.service.CustomerService;
import com.twister.ui.components.StringField;
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
public class CustomerEditDialog extends DefaultEditDialog<Customer> {

    private final CustomerService customerService;

    private Text nameLabel;
    private Text phoneLabel;
    private Text surnameLabel;
    private Text patronymicLabel;
    private StringField nameField;
    private StringField phoneField;
    private StringField surnameField;
    private StringField patronymicField;

    @Override
    protected void doBeforeInitialization() {
        nameLabel = new Text();
        phoneLabel = new Text();
        surnameLabel = new Text();
        patronymicLabel = new Text();

        nameField = new StringField();
        phoneField = new StringField();
        surnameField = new StringField();
        patronymicField = new StringField();

        nameLabel.setText(i18nUtils.get("customer.dialog.name.label"));
        phoneLabel.setText(i18nUtils.get("customer.dialog.phone.label"));
        surnameLabel.setText(i18nUtils.get("customer.dialog.surname.label"));
        patronymicLabel.setText(i18nUtils.get("customer.dialog.patronymic.label"));

        nameField.setId("nameField");
        phoneField.setId("phoneField");
        surnameField.setId("surnameField");
        patronymicField.setId("patronymicField");

        nameField.setPromptText(i18nUtils.get("customer.dialog.name.prompt"));
        phoneField.setPromptText(i18nUtils.get("customer.dialog.phone.prompt"));
        surnameField.setPromptText(i18nUtils.get("customer.dialog.surname.prompt"));
        patronymicField.setPromptText(i18nUtils.get("customer.dialog.patronymic.prompt"));

        if (dialogMode == DialogMode.UPDATE) {
            nameField.setText(editModel.getName());
            phoneField.setText(editModel.getPhone());
            surnameField.setText(editModel.getSurname());
            patronymicField.setText(editModel.getPatronymic());

            nameField.validProperty().set(true);
            phoneField.validProperty().set(true);
            surnameField.validProperty().set(true);
            patronymicField.validProperty().set(true);
        }
    }

    @Override
    protected String getDialogCreateTitle() {
        return i18nUtils.get("customer.dialog.create.title");
    }

    @Override
    protected String getDialogUpdateTitle() {
        return i18nUtils.get("customer.dialog.update.title");
    }

    @Override
    protected List<Pair<Text, TextField>> getTextFieldComponents() {
        return List.of(
                new Pair<>(nameLabel, nameField),
                new Pair<>(surnameLabel, surnameField),
                new Pair<>(patronymicLabel, patronymicField),
                new Pair<>(phoneLabel, phoneField)
        );
    }

    @Override
    protected void onCreateAction(ActionEvent event) {
        customerService.createCustomer(createClientFromFields());
    }

    @Override
    protected void onUpdateAction(ActionEvent event) {
        customerService.updateCustomer(editModel, createClientFromFields());
    }

    private Customer createClientFromFields() {
        var name = nameField.getText();
        var surname = surnameField.getText();
        var patronymic = patronymicField.getText();
        var phone = phoneField.getText();

        Customer customer = new Customer();
        customer.setName(name);
        customer.setSurname(surname);
        customer.setPatronymic(patronymic);
        customer.setPhone(phone);

        return customer;
    }
}
