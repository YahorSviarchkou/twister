package com.twister.desktop.shell;

import com.twister.fx.javafx.StageReadyEvent;
import com.twister.fx.javafx.i18n.FxMessages;
import com.twister.fx.javafx.view.ViewLoader;
import javafx.scene.Scene;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ShellWindow {

    // For the first run. Later the saved window state wins.
    private static final double WIDTH = 1100;
    private static final double HEIGHT = 720;

    private final ViewLoader views;
    private final FxMessages messages;

    @EventListener
    void show(StageReadyEvent event) {
        var stage = event.stage();
        stage.setTitle(messages.get("shell.title"));
        stage.setScene(new Scene(views.load(ShellView.class).root(), WIDTH, HEIGHT));
        stage.show();
    }
}
