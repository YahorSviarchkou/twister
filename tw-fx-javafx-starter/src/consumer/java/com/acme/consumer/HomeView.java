package com.acme.consumer;

import com.twister.fx.javafx.view.FxView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

@FxView
public class HomeView {

    @FXML
    private Label title;

    @FXML
    private DetailView detailController;

    String text() {
        return title.getText() + "/" + detailController.text();
    }
}
