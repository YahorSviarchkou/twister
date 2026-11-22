package com.twister.fx.javafx.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Window;

/** The FX toolkit of the test JVM, on JavaFX's headless platform. */
public final class Fx {

    public static final long TIMEOUT_SECONDS = 30;

    private static boolean started;

    private Fx() {}

    public static synchronized void start() throws Exception {
        if (started) {
            return;
        }
        System.setProperty("glass.platform", "Headless");
        System.setProperty("prism.order", "sw");
        var up = new CountDownLatch(1);
        Platform.startup(up::countDown);
        assertThat(up.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();
        Platform.setImplicitExit(false);
        // Before any test shows a window: the owner is working on this screen.
        assertThat(call(() -> Screen.getPrimary().getBounds().getWidth()))
                .as("width of the headless screen")
                .isEqualTo(1000);
        started = true;
    }

    public static <T> T call(Callable<T> action) throws Exception {
        var task = new FutureTask<>(action);
        Platform.runLater(task);
        try {
            return task.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof Exception cause) {
                throw cause;
            }
            throw e;
        }
    }

    public static void run(Action action) throws Exception {
        call(() -> {
            action.run();
            return null;
        });
    }

    /** Lets what is queued on the FX thread run, and what that queues. */
    public static void flush() throws Exception {
        run(() -> {});
        run(() -> {});
    }

    public static void hideAllWindows() throws Exception {
        run(() -> List.copyOf(Window.getWindows()).forEach(Window::hide));
    }

    @FunctionalInterface
    public interface Action {
        void run() throws Exception;
    }
}
