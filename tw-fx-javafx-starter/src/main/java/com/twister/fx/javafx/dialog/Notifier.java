package com.twister.fx.javafx.dialog;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import com.twister.fx.javafx.concurrent.FxThread;
import com.twister.fx.javafx.i18n.FxMessages;
import java.time.Duration;
import javafx.animation.PauseTransition;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

/** Short messages in a corner of the active window that leave by themselves. Works from any thread. */
@Slf4j
public class Notifier {

    private static final int MAX_VISIBLE = 5;
    private static final double MAX_WIDTH = 420; // text wraps and the height follows it
    private static final double MARGIN = 16;
    private static final double SPACING = 8;

    private final FxMessages messages;
    private final Duration visibleFor;
    // Created on first use: the bean appears before the FX thread may be used.
    private Popup popup;
    private VBox stack;

    public Notifier(FxMessages messages, Duration visibleFor) {
        this.messages = messages;
        this.visibleFor = visibleFor;
    }

    public void info(String key, Object... args) {
        post(Styles.ACCENT, messages.get(key, args));
    }

    public void success(String key, Object... args) {
        post(Styles.SUCCESS, messages.get(key, args));
    }

    public void warning(String key, Object... args) {
        post(Styles.WARNING, messages.get(key, args));
    }

    public void error(String key, Object... args) {
        post(Styles.DANGER, messages.get(key, args));
    }

    private void post(String style, String text) {
        FxThread.run(() -> ActiveWindow.find()
                .ifPresentOrElse(
                        owner -> show(owner, style, text),
                        () -> log.info("No window to show the notification on: {}", text)));
    }

    private void show(Window owner, String style, String text) {
        if (popup == null) {
            stack = new VBox(SPACING);
            popup = new Popup();
            popup.setHideOnEscape(false);
            popup.setAnchorLocation(AnchorLocation.WINDOW_BOTTOM_RIGHT);
            popup.getContent().add(stack);
        }

        var notification = new Notification(text);
        notification.getStyleClass().addAll(style, Styles.ELEVATED_1);
        notification.setMaxWidth(MAX_WIDTH);
        var timer = new PauseTransition(javafx.util.Duration.millis(visibleFor.toMillis()));
        Runnable remove = () -> {
            timer.stop();
            stack.getChildren().remove(notification);
            if (stack.getChildren().isEmpty()) {
                popup.hide();
            }
        };
        timer.setOnFinished(event -> remove.run());
        notification.setOnClose(event -> remove.run());
        if (stack.getChildren().size() >= MAX_VISIBLE) {
            stack.getChildren().removeFirst();
        }
        stack.getChildren().add(notification);

        double right = owner.getX() + owner.getWidth() - MARGIN;
        double bottom = owner.getY() + owner.getHeight() - MARGIN;
        if (popup.isShowing() && popup.getOwnerWindow() == owner) {
            popup.setAnchorX(right);
            popup.setAnchorY(bottom);
        } else {
            popup.hide();
            popup.show(owner, right, bottom);
        }
        timer.play();
    }
}
