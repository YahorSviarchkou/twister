package com.twister.ui.screen;

import com.twister.configuration.ControllerFXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public abstract class ScreenController {

    @Autowired
    private FXMLLoader fxmlLoader;

    public Parent loadFXMLAndGetParent() {
        log.info("Loading FXML for controller: {}", getClass().getSimpleName());

        if(getClass().isAnnotationPresent(ControllerFXML.class)) {
            var fxmlPath = getClass().getAnnotation(ControllerFXML.class).fxml();
            fxmlLoader.setLocation(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            log.info("FXML file location: {}", fxmlPath);
            try {
                return fxmlLoader.load();
            } catch (IOException e) {
                var errorMessage = "Error loading FXML for controller: " + getClass().getSimpleName();
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        }

        var errorMessage = "Controller class " + getClass().getSimpleName() + " is missing @ControllerFXML annotation";
        log.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }
}
