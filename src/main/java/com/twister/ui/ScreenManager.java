package com.twister.ui;

import com.twister.ui.screen.ScreenController;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScreenManager {

    ApplicationContext context;

    public <T extends ScreenController> void showScreen(Class<T> controllerClass, Stage stage) {
        Platform.runLater(() -> {
            var controller = context.getBean(controllerClass);
            var root = controller.loadFXMLAndGetParent();

            stage.setTitle("Hello World");
            stage.setScene(new Scene(root, 300, 275));
            stage.show();
        });
    }
}
