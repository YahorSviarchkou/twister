package com.twister.ui.screen;

import com.twister.configuration.annotation.FXMLController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public abstract class ScreenController implements Initializable {

    @Autowired
    private FXMLLoader fxmlLoader;

    public Parent loadFXMLAndGetParent() {
        log.info("Loading FXML for controller: {}", getClass().getSimpleName());

        if(getClass().isAnnotationPresent(FXMLController.class)) {
            var fxmlPath = getClass().getAnnotation(FXMLController.class).fxml();
            fxmlLoader.setLocation(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            log.info("FXML file location: {}", fxmlPath);
            try {
                return fxmlLoader.load();
            } catch (IOException e) {
                var errorMessage = "Error loading FXML for controller: " + getClass().getSimpleName();
                log.error(errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        }

        var errorMessage = "Controller class " + getClass().getSimpleName() + " is missing @ControllerFXML annotation";
        log.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }

    public Parent setChildFXML(Pane contentPane, Class<? extends ScreenController> controller) {
        log.info("Loading FXML for controller: {}", controller.getSimpleName());

        if(controller.isAnnotationPresent(FXMLController.class)) {
            var fxmlPath = controller.getAnnotation(FXMLController.class).fxml();
            log.info("FXML file location: {}", fxmlPath);
            try {
                fxmlLoader.setLocation(Objects.requireNonNull(controller.getResource(fxmlPath)));
                Region parent = fxmlLoader.load();
//                Region parent = FxmlLoader.load(Objects.requireNonNull(controller.getResource(fxmlPath)));
                contentPane.getChildren().setAll(parent);

                parent.prefWidthProperty().bind(contentPane.widthProperty());
                parent.prefHeightProperty().bind(contentPane.heightProperty());

                return parent;
            } catch (IOException e) {
                var errorMessage = "Error loading FXML for controller: " + controller.getSimpleName();
                log.error(errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        }

        var errorMessage = "Controller class " + controller.getSimpleName() + " is missing @ControllerFXML annotation";
        log.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }
}
