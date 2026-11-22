package com.twister.fx.javafx;

import javafx.stage.Stage;

/**
 * Hands the primary stage to the application on the FX thread. Receive it with {@code @EventListener}, set the
 * scene and show the stage.
 */
public record StageReadyEvent(Stage stage) {}
