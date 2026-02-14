package com.twister.ui;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

import static com.twister.util.UiUtils.SCREEN_BOUNDS;

public final class LoaderBuilder {

    private static final double WIDTH = 0.25;
    private static final double HEIGHT = 0.25;
    private static final StageStyle STAGE_STYLE = StageStyle.UNDECORATED;

    private Stage primaryStage;

    public LoaderBuilder configure(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setWidth(SCREEN_BOUNDS.getWidth() * WIDTH);
        primaryStage.setHeight(SCREEN_BOUNDS.getHeight() * HEIGHT);

        double centerX = SCREEN_BOUNDS.getWidth() / 2 - primaryStage.getWidth() / 2;
        double centerY = SCREEN_BOUNDS.getHeight() / 2 - primaryStage.getHeight() / 2;

        primaryStage.setX(centerX);
        primaryStage.setY(centerY);

        primaryStage.initStyle(STAGE_STYLE);

        return this;
    }

    public Stage buildAndShow() {
        Optional.ofNullable(primaryStage).ifPresentOrElse(Stage::show, () -> {
            throw new UnsupportedOperationException();
        });
        return primaryStage;
    }
}
