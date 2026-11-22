package com.twister.ui.components;

import com.twister.util.UiUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;

public class DoubleField extends TextField implements ValidatableField {

    private final BooleanProperty valid = new SimpleBooleanProperty(false);

    public DoubleField() {
        super();
        textProperty().addListener(UiUtils.doubleFieldValidator(this, valid));
    }

    @Override
    public BooleanProperty validProperty() {
        return valid;
    }
}
