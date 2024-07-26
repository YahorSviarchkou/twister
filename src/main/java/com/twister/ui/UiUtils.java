package com.twister.ui;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

@UtilityClass
public final class UiUtils {

    public static final Rectangle2D SCREEN_BOUNDS = Screen.getPrimary().getVisualBounds();

    public static void showErrorDialog(String title, String message, Consumer<ButtonType> responseHandler) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait().ifPresent(responseHandler);
        });
    }
}
