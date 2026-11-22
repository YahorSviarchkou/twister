package com.twister.fx.javafx.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * Loads {@link FxView} screens with controllers from the Spring context.
 *
 * <p>Works on any thread, as JavaFX allows building nodes off the FX thread. Attach the root to a shown scene on
 * the FX thread.
 */
@Slf4j
@RequiredArgsConstructor
public class ViewLoader {

    private final ApplicationContext context;
    private final ResourceBundle resources;
    private final ViewCache cache;

    public <C> View<C> load(Class<C> controllerType) {
        var annotation = controllerType.getAnnotation(FxView.class);
        if (annotation == null) {
            throw new IllegalArgumentException(controllerType.getName() + " is not annotated with @FxView");
        }
        if (!annotation.cached()) {
            return create(controllerType, annotation);
        }
        // Not computeIfAbsent: a controller may load other views while it initializes.
        return cache.find(controllerType)
                .orElseGet(() -> cache.putIfAbsent(controllerType, create(controllerType, annotation)));
    }

    private <C> View<C> create(Class<C> type, FxView annotation) {
        var path = annotation.fxml().isEmpty() ? type.getSimpleName() + ".fxml" : annotation.fxml();
        URL location = type.getResource(path);
        if (location == null) {
            throw new ViewLoadException(type, "FXML not found: " + path);
        }

        var loader = new FXMLLoader(location, resources);
        loader.setControllerFactory(context::getBean);
        loader.setClassLoader(type.getClassLoader());
        Object root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new ViewLoadException(type, e);
        }

        Object controller = loader.getController();
        if (!type.isInstance(controller)) {
            throw new ViewLoadException(type, "the root element of " + path + " needs fx:controller with this class");
        }
        if (!(root instanceof Parent parent)) {
            throw new ViewLoadException(type, "the root element of " + path + " is not a Parent");
        }
        log.debug("Loaded {} from {}", type.getName(), location);
        return new View<>(parent, type.cast(controller));
    }
}
