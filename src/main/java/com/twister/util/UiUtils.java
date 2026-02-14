package com.twister.util;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;
import java.util.function.Predicate;

@UtilityClass
public final class UiUtils {

    private static final String TEXT_FIELD_ERROR_CSS_STYLE = "text-field-error";

    public static final Rectangle2D SCREEN_BOUNDS = Screen.getPrimary().getVisualBounds();

    public static void showErrorDialog(String title,
                                       String message,
                                       Consumer<ButtonType> responseHandler) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait().ifPresent(responseHandler);
        });
    }

    public static void showWarningDialog(String title,
                                         String message,
                                         Consumer<ButtonType> responseHandler) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait().ifPresent(responseHandler);
        });
    }

    public static ChangeListener<String> textFieldValidator(@NonNull TextField textField,
                                                            @NonNull BooleanProperty validProperty) {
        return fieldValidator(
                textField,
                val -> !val.isBlank(),
                validProperty
        );
    }

    public static ChangeListener<String> doubleFieldValidator(@NonNull TextField textField,
                                                              @NonNull BooleanProperty validProperty) {
        return fieldValidator(
                textField,
                val -> !val.isBlank() && val.matches("\\d+(\\.\\d+)?"),
                validProperty
        );
    }

    public static ChangeListener<String> fieldValidator(@NonNull TextField textField,
                                                        @NonNull Predicate<String> validator,
                                                        @NonNull BooleanProperty validProperty) {
        return (obs, oldVal, newVal) -> {
            boolean isValid = validator.test(newVal);
            validProperty.set(isValid);
            setTextFieldWarning(textField, isValid);
        };
    }

    public static void setTextFieldWarning(@NonNull TextField textField, boolean isValid) {
        if (isValid) {
            textField.getStyleClass().remove(TEXT_FIELD_ERROR_CSS_STYLE);
        } else if (!textField.getStyleClass().contains(TEXT_FIELD_ERROR_CSS_STYLE)) {
            textField.getStyleClass().add(TEXT_FIELD_ERROR_CSS_STYLE);
        }
    }
}
