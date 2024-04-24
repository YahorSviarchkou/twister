package com.twister;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class TwisterApplication extends Application {

    public static String[] args;

    public static void main(String[] args) {
        TwisterApplication.args = args;
        Application.launch(TwisterApplication.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Image loadingAnimation = new Image(getClass().getResourceAsStream("/images/loading.gif"));
        ImageView imageView = new ImageView();
        imageView.setImage(loadingAnimation);
        Group root = new Group(imageView);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("JavaFX with Spring Demo");
        stage.setScene(scene);
        stage.show();

        new Thread(() -> {
            new SpringApplicationBuilder(TwisterApplication.class).run(args);
        }, "Spring Thread").start();
    }
}
