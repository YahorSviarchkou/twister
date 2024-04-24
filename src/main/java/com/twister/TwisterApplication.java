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

    private static final String SPRING_BOOT_THREAD = "SPRING_BOOT_THREAD";
    private static String[] args;

    public static void main(String[] args) {
        SqlLitePet.args = args;
        Application.launch(SqlLitePet.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Image loadingAnimation = new Image(getClass().getResourceAsStream("/images/loader.gif"));
        ImageView imageView = new ImageView();
        imageView.setImage(loadingAnimation);
        Group root = new Group(imageView);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX with Spring Demo");
        primaryStage.setScene(scene);
        primaryStage.show();


        new Thread(() -> {
            new SpringApplicationBuilder(SqlLitePet.class).run(args);
        }, SPRING_BOOT_THREAD).start();
    }
}
