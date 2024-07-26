package com.twister.handler;

import com.twister.event.ApplicationStartEvent;
import com.twister.ui.ScreenManager;
import com.twister.ui.screen.MainScreen;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationEventHandler implements ApplicationStartEvent, ApplicationListener<ApplicationContextEvent> {

    Stage primaryStage;

    @Override
    public void onApplicationStartEvent() {
        log.info("Handling ApplicationStartEvent...");
        Image loadingAnimation = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/loader/bicycle.gif")));

        ImageView imageView = new ImageView();
        imageView.setImage(loadingAnimation);
        imageView.setFitWidth(primaryStage.getWidth());
        imageView.setFitHeight(primaryStage.getHeight());

        Scene scene = new Scene(new Group(imageView));

        primaryStage.setScene(scene);
    }

    @Override
    public void handleApplicationContextReady(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent cre) {
            handleContextRefreshedEvent(cre);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        handleApplicationContextReady(event);
    }

    private void handleContextRefreshedEvent(ContextRefreshedEvent event) {
        log.info("Handling ContextRefreshedEvent...");
        Platform.runLater(() -> {
            primaryStage.close();

            var context = event.getApplicationContext();
            var screenManager = context.getBean(ScreenManager.class);

            screenManager.showScreen(MainScreen.class, new Stage());
        });
    }
}
