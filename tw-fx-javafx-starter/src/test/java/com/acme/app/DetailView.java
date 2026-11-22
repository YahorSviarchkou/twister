package com.acme.app;

import com.twister.fx.javafx.view.FxView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** No default constructor: only Spring can create it. */
@FxView
public class DetailView {

    private final Greeter greeter;

    @FXML
    private Label text;

    public DetailView(Greeter greeter) {
        this.greeter = greeter;
    }

    public Greeter greeter() {
        return greeter;
    }

    public String text() {
        return text.getText();
    }
}
