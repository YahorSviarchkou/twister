package com.twister.desktop.shell;

import com.twister.fx.javafx.i18n.FxMessages;
import com.twister.fx.javafx.navigation.Navigator;
import com.twister.fx.javafx.view.FxView;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

/** The main window: a menu of all {@link NavigationItem} beans and the area of the current screen. */
@FxView
@RequiredArgsConstructor
public class ShellView {

    private final List<NavigationItem> items;
    private final Navigator navigator;
    private final FxMessages messages;

    @FXML
    private BorderPane shell;

    @FXML
    private VBox menu;

    @FXML
    void initialize() {
        navigator.attach(shell::setCenter);
        items.forEach(item -> menu.getChildren().add(menuButton(item)));
        navigator.switchTo(items.getFirst().screen());
    }

    private Button menuButton(NavigationItem item) {
        var button = new Button(messages.get(item.titleKey()));
        button.getStyleClass().add("flat");
        button.setAlignment(Pos.CENTER_LEFT);
        button.setMaxWidth(Double.MAX_VALUE); // all as wide as the longest text
        button.setOnAction(event -> navigator.switchTo(item.screen()));
        return button;
    }
}
