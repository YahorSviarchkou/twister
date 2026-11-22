package com.acme.app;

import com.twister.fx.javafx.view.FxView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

@FxView
public class MainView {

    @FXML
    private Label title;

    @FXML
    private DetailView detailController;

    public String title() {
        return title.getText();
    }

    public DetailView detail() {
        return detailController;
    }
}
