package com.twister.fx.javafx.dialog;

import java.util.Optional;
import java.util.function.Supplier;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

final class ActiveWindow {

    private ActiveWindow() {}

    /** The focused stage, otherwise the one shown last. Popups and tooltips cannot own a dialog. */
    static Optional<Stage> find() {
        var stages = Window.getWindows().stream()
                .filter(window -> window instanceof Stage && window.isShowing())
                .map(Stage.class::cast)
                .toList();
        return stages.stream()
                .filter(Window::isFocused)
                .findFirst()
                .or(() -> stages.isEmpty() ? Optional.empty() : Optional.of(stages.getLast()));
    }

    /**
     * Waits in {@code showAndWait} and closes that window if its owner hides first. JavaFX leaves this to the
     * platform, not every one does it, and a window left behind keeps the process running.
     */
    static <T> T closingWith(Optional<Stage> owner, Runnable close, Supplier<T> showAndWait) {
        EventHandler<WindowEvent> ownerHiding = event -> close.run();
        owner.ifPresent(stage -> stage.addEventHandler(WindowEvent.WINDOW_HIDING, ownerHiding));
        try {
            return showAndWait.get();
        } finally {
            owner.ifPresent(stage -> stage.removeEventHandler(WindowEvent.WINDOW_HIDING, ownerHiding));
        }
    }
}
