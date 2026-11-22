package com.acme.consumer;

import com.twister.fx.javafx.StageReadyEvent;
import com.twister.fx.javafx.concurrent.FxTasks;
import com.twister.fx.javafx.dialog.Dialogs;
import com.twister.fx.javafx.dialog.Notifier;
import com.twister.fx.javafx.i18n.AvailableLanguages;
import com.twister.fx.javafx.navigation.Navigator;
import com.twister.fx.javafx.theme.ThemeManager;
import com.twister.fx.javafx.view.ViewLoader;
import java.util.concurrent.CountDownLatch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Plays the scenario named by the first argument and reports on standard output. */
@Component
public class ConsumerShell {

    private final ApplicationArguments arguments;
    private final ViewLoader views;
    private final Navigator navigator;
    private final FxTasks tasks;
    private final Dialogs dialogs;
    private final Notifier notifier;
    private final ThemeManager themes;
    private final AvailableLanguages languages;

    ConsumerShell(
            ApplicationArguments arguments,
            ViewLoader views,
            Navigator navigator,
            FxTasks tasks,
            Dialogs dialogs,
            Notifier notifier,
            ThemeManager themes,
            AvailableLanguages languages) {
        this.arguments = arguments;
        this.views = views;
        this.navigator = navigator;
        this.tasks = tasks;
        this.dialogs = dialogs;
        this.notifier = notifier;
        this.themes = themes;
        this.languages = languages;
    }

    @EventListener
    void onStageReady(StageReadyEvent event) {
        var names = arguments.getNonOptionArgs();
        String scenario = names.isEmpty() ? "close" : names.getFirst();
        if (scenario.equals("fail")) {
            throw new IllegalStateException("shell failed");
        }
        if (Screen.getPrimary().getBounds().getWidth() != 1000) {
            throw new IllegalStateException("Not the headless screen");
        }

        Stage stage = event.stage();
        var root = new BorderPane();
        navigator.attach(root::setCenter);
        HomeView home = navigator.switchTo(HomeView.class);
        stage.setScene(new Scene(root, 400, 300));
        stage.show();

        report("SCREEN " + home.text());
        report("NEW CONTROLLER " + (views.load(HomeView.class).controller() != home));
        report("LANGUAGES " + languages.locales());
        report("THEME " + themes.getMode() + " " + Application.getUserAgentStylesheet());
        report("BOUNDS " + (int) stage.getX() + "," + (int) stage.getY() + "," + (int) stage.getWidth() + ","
                + (int) stage.getHeight());

        switch (scenario) {
            case "close" -> saveInBackgroundThenClose(stage);
            case "move" -> {
                place(stage);
                Platform.runLater(() -> requestClose(stage));
            }
            case "exit" -> {
                place(stage);
                Platform.runLater(Platform::exit);
            }
            case "error" -> {
                closeDialogThenStage(stage);
                Platform.runLater(() -> {
                    throw new IllegalStateException("nobody catches this");
                });
            }
            case "exit-with-dialog" -> {
                onDialog(dialog -> Platform.exit());
                Platform.runLater(() -> dialogs.confirm("consumer.confirm"));
            }
            case "close-with-dialog" -> {
                onDialog(dialog -> stage.hide());
                Platform.runLater(() -> report("CONFIRMED " + dialogs.confirm("consumer.confirm")));
            }
            case "hung-task" -> {
                var started = new CountDownLatch(1);
                tasks.run(() -> hang(started), result -> {});
                tasks.run(() -> await(started), result -> requestClose(stage));
            }
            default -> throw new IllegalArgumentException(scenario);
        }
    }

    @EventListener
    void onClosed(ContextClosedEvent event) {
        report("CONTEXT CLOSED on fx=" + Platform.isFxApplicationThread());
    }

    private void saveInBackgroundThenClose(Stage stage) {
        tasks.run(() -> "work on fx=" + Platform.isFxApplicationThread(), result -> {
            report("TASK " + result + " callback on fx=" + Platform.isFxApplicationThread());
            notifier.success("consumer.saved");
            report("WINDOWS " + Window.getWindows().size());
            // Closing with the notification still up must end the process too.
            Platform.runLater(() -> requestClose(stage));
        });
    }

    private void closeDialogThenStage(Stage stage) {
        onDialog(dialog -> {
            report("DIALOG " + dialog.getHeaderText());
            ((Button) dialog.lookupButton(dialog.getButtonTypes().getFirst())).fire();
            Platform.runLater(() -> requestClose(stage));
        });
    }

    private void onDialog(java.util.function.Consumer<DialogPane> action) {
        Window.getWindows().addListener((ListChangeListener<Window>) change -> {
            while (change.next()) {
                for (Window window : change.getAddedSubList()) {
                    if (window.getScene() != null && window.getScene().getRoot() instanceof DialogPane pane) {
                        Platform.runLater(() -> action.accept(pane));
                    }
                }
            }
        });
    }

    private static void place(Stage stage) {
        stage.setX(120);
        stage.setY(130);
        stage.setWidth(500);
        stage.setHeight(320);
    }

    // What the close button of the title bar does.
    private static void requestClose(Stage stage) {
        Event.fireEvent(stage, new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    private static String hang(CountDownLatch started) {
        started.countDown();
        return await(new CountDownLatch(1));
    }

    private static String await(CountDownLatch latch) {
        try {
            latch.await();
            return "done";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "interrupted";
        }
    }

    private static void report(String line) {
        System.out.println(line);
    }
}
