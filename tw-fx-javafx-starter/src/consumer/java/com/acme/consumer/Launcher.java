package com.acme.consumer;

import javafx.application.Application;

public final class Launcher {

    private Launcher() {}

    public static void main(String[] args) {
        if (!"Headless".equals(System.getProperty("glass.platform"))) {
            throw new IllegalStateException("This application is for tests and must not open real windows");
        }
        Application.launch(ConsumerFx.class, args);
        System.out.println("LAUNCH RETURNED");
    }
}
