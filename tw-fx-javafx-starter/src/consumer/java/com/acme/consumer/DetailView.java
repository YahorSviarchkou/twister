package com.acme.consumer;

import com.twister.fx.javafx.view.FxView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

@FxView
public class DetailView {

    @FXML
    private Label text;

    String text() {
        return text.getText();
    }
}
