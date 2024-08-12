package com.twister.ui;

import com.twister.configuration.TwisterProperties;
import com.twister.configuration.annotation.FXMLController;
import com.twister.ui.screen.ScreenController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScreenManager {

    TwisterProperties twisterProperties;

    @Lookup
    @NonNull
    protected FXMLLoader getFxmlLoader() {
        //noinspection DataFlowIssue
        return null;
    }

    public <T extends ScreenController> void showScreen(Class<T> controllerClass, Stage stage) {
        Platform.runLater(() -> {
            var root = loadFxml(controllerClass);

            stage.setTitle(twisterProperties.getAppName());
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        });
    }

    public Region loadFxml(Class<? extends ScreenController> controllerClass) {
        log.info("Loading FXML for controller: {}", controllerClass.getSimpleName());

        if (!controllerClass.isAnnotationPresent(FXMLController.class)) {
            var errorMessage = "Controller class " + controllerClass.getSimpleName() + " is missing @ControllerFXML annotation";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        var fxmlPath = controllerClass.getAnnotation(FXMLController.class).fxml();
        log.info("FXML file location: {}", fxmlPath);

        var fxmlLoader = getFxmlLoader();
        fxmlLoader.setLocation(Objects.requireNonNull(controllerClass.getResource(fxmlPath)));

        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            var errorMessage = "Error loading FXML for controller: " + controllerClass.getSimpleName();
            log.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public Parent loadChildFxml(Pane parentPane, Class<? extends ScreenController> controllerClass) {
        Region child = loadFxml(controllerClass);
        parentPane.getChildren().setAll(child);
        child.prefWidthProperty().bind(parentPane.widthProperty());
        child.prefHeightProperty().bind(parentPane.heightProperty());
        return child;
    }
}
