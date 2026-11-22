package com.twister.fx.javafx.concurrent;

import javafx.application.Platform;

public final class FxThread {

    private FxThread() {}

    /** @throws IllegalStateException off the FX thread */
    public static void check() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Call this on the FX thread, not on "
                    + Thread.currentThread().getName());
        }
    }

    /** Runs {@code action} now on the FX thread, later from any other. */
    public static void run(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
