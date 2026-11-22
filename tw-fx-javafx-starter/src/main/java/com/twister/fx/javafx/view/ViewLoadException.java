package com.twister.fx.javafx.view;

public class ViewLoadException extends RuntimeException {

    ViewLoadException(Class<?> controllerType, String reason) {
        super("Cannot load the view of %s: %s".formatted(controllerType.getName(), reason));
    }

    ViewLoadException(Class<?> controllerType, Throwable cause) {
        super("Cannot load the view of %s".formatted(controllerType.getName()), cause);
    }
}
