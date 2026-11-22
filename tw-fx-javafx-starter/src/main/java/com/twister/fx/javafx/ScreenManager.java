package com.twister.fx.javafx;

import com.twister.fx.javafx.annotation.FXController;
import com.twister.fx.javafx.cache.ScreenProvider;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScreenManager {

    private final ScreenProvider screenProvider;
    private final ApplicationContext applicationContext;

    @Lookup
    @NonNull
    protected FXMLLoader getFxmlLoader() {
        //noinspection DataFlowIssue
        return null;
    }

    public <T extends Initializable> Region loadFxml(Class<T> controllerClass) {
        log.debug("Requesting FXML for controller: {}", controllerClass.getSimpleName());

        Region cachedRegion = screenProvider.get(controllerClass);
        if (cachedRegion != null) {
            log.info("Returning cached instance for: {}", controllerClass.getSimpleName());
            return cachedRegion;
        }

        if (!controllerClass.isAnnotationPresent(FXController.class)) {
            var errorMessage = "Controller class " + controllerClass.getSimpleName() + " is missing @FXController annotation";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        var fxmlPath = controllerClass.getAnnotation(FXController.class).fxml();

        try {
            var fxmlLoader = getFxmlLoader();
            fxmlLoader.setLocation(Objects.requireNonNull(controllerClass.getResource(fxmlPath)));
            fxmlLoader.setController(applicationContext.getBean(controllerClass));

            Region loadedRegion = fxmlLoader.load();

            screenProvider.put(controllerClass, loadedRegion);

            return loadedRegion;
        } catch (NullPointerException npe) {
            var errorMessage = "Not found FXML '%s' for controller: %s".formatted(fxmlPath, controllerClass.getSimpleName());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        } catch (IOException ioe) {
            var errorMessage = "Error loading FXML for controller: " + controllerClass.getSimpleName();
            log.error(errorMessage, ioe);
            throw new RuntimeException(errorMessage, ioe);
        }
    }
}
