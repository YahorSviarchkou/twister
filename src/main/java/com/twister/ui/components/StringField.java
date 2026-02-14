package com.twister.ui.components;

import com.twister.util.UiUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;

public class StringField extends TextField implements ValidatableField {

    private final BooleanProperty valid = new SimpleBooleanProperty(false);

    public StringField() {
        super();
        textProperty().addListener(UiUtils.textFieldValidator(this, valid));
    }

    @Override
    public BooleanProperty validProperty() {
        return valid;
    }
}
