package com.twister.ui.components;

import javafx.beans.property.BooleanProperty;

public interface ValidatableField {

    BooleanProperty validProperty();

    default boolean isValid() {
        return validProperty().get();
    }
}
