package com.twister.fx.javafx.error;

import com.twister.fx.javafx.dialog.Dialogs;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/** Turns errors nobody caught into a log entry and a dialog. */
@Slf4j
@RequiredArgsConstructor
public class FxErrorHandler implements Thread.UncaughtExceptionHandler, ApplicationListener<ContextClosedEvent> {

    private final Dialogs dialogs;
    private final AtomicBoolean dialogPending = new AtomicBoolean();
    private volatile boolean closing;

    @Override
    public void uncaughtException(Thread thread, Throwable error) {
        log.error("Uncaught error in thread {}", thread.getName(), error);
        // One dialog at a time. An error repeated on each pulse or raised by the dialog itself is only logged.
        if (closing || !dialogPending.compareAndSet(false, true)) {
            return;
        }
        try {
            // Later even on the FX thread: showAndWait is not allowed during animation and layout.
            Platform.runLater(this::showDialog);
        } catch (RuntimeException e) {
            dialogPending.set(false);
            log.error("Cannot schedule the error dialog", e);
        }
    }

    public void handle(Throwable error) {
        uncaughtException(Thread.currentThread(), error);
    }

    // A dialog opened while the application exits would keep the process alive.
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        closing = true;
    }

    private void showDialog() {
        try {
            if (!closing) {
                dialogs.unexpectedError();
            }
        } catch (Throwable e) {
            // Everything, as whatever escapes from here would come back to this handler.
            log.error("Cannot show the error dialog", e);
        } finally {
            dialogPending.set(false);
        }
    }
}
