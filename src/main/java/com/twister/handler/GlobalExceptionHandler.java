package com.twister.handler;

import lombok.extern.slf4j.Slf4j;

import static com.twister.ui.UiUtils.showErrorDialog;

@Slf4j
public final class GlobalExceptionHandler {

    public static void register() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            if(log.isDebugEnabled()) {
                throwable.printStackTrace();
            }

            log.error("{}: {}", throwable.getClass().getSimpleName(), throwable.getMessage());
            showErrorDialog("Error", "An unexpected error occurred", (resp) -> {});
        });
    }
}
