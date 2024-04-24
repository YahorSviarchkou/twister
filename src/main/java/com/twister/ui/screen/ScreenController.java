package com.twister.ui.screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.SneakyThrows;

import java.util.Objects;

public abstract class ScreenController {

    protected abstract String getFXMLName();

    @SneakyThrows
    public Parent loadFXMLAndGetParent() {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(getFXMLName())));
    }
}
