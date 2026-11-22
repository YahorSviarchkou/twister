package com.twister;

import com.twister.configuration.TwisterProperties;
import com.twister.handler.ApplicationEventHandler;
import com.twister.handler.GlobalExceptionHandler;
import com.twister.ui.LoaderBuilder;
import javafx.application.Application;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Locale;

import static com.twister.util.UiUtils.showErrorDialog;

@SpringBootApplication
@EnableConfigurationProperties(TwisterProperties.class)
public class TwisterApplication extends Application {

    private static final String SPRING_BOOT_THREAD_NAME = "SPRING_BOOT";
    private static String[] args;

    public static void main(String[] args) {
        TwisterApplication.args = args;
        Application.launch(TwisterApplication.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GlobalExceptionHandler.register();

        Locale.setDefault(new Locale("ru"));

        new LoaderBuilder()
                .configure(primaryStage)
                .buildAndShow();

        var appStartEvent = new ApplicationEventHandler(primaryStage);
        appStartEvent.onApplicationStartEvent();

        new Thread(() -> {
            try {
                new SpringApplicationBuilder(TwisterApplication.class)
                        .listeners(appStartEvent)
                        .run(args);
            } catch (Exception e) {
                showErrorDialog("Error Starting Spring Boot", "Ошибка запуска: " + e.getMessage(), (resp) -> {
                    if (ButtonType.OK == resp) {
                        primaryStage.close();
                    }
                });
            }
        }, SPRING_BOOT_THREAD_NAME
        ).start();
    }
}
