package com.twister.fx.javafx.dialog;

import com.twister.fx.javafx.concurrent.FxThread;
import com.twister.fx.javafx.i18n.FxMessages;
import com.twister.fx.javafx.view.View;
import com.twister.fx.javafx.view.ViewLoader;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

/**
 * Modal windows over the active one. Texts are message keys. Each method waits until the user closes the window,
 * so call it on the FX thread and not from animation or layout.
 */
@RequiredArgsConstructor
public class Dialogs {

    private final FxMessages messages;
    private final ViewLoader views;

    public void info(String key, Object... args) {
        FxThread.check();
        showAlert(AlertType.INFORMATION, "javafx.dialog.info.title", null, messages.get(key, args), okButton());
    }

    public void warning(String key, Object... args) {
        FxThread.check();
        showAlert(AlertType.WARNING, "javafx.dialog.warning.title", null, messages.get(key, args), okButton());
    }

    public void error(String key, Object... args) {
        FxThread.check();
        showAlert(AlertType.ERROR, "javafx.dialog.error.title", null, messages.get(key, args), closeButton());
    }

    /** @return true only for OK, false for Cancel and for closing the window */
    public boolean confirm(String key, Object... args) {
        FxThread.check();
        var ok = okButton();
        var cancel = new ButtonType(messages.get("javafx.dialog.cancel"), ButtonData.CANCEL_CLOSE);
        return showAlert(
                        AlertType.CONFIRMATION,
                        "javafx.dialog.confirm.title",
                        null,
                        messages.get(key, args),
                        ok,
                        cancel)
                .filter(ok::equals)
                .isPresent();
    }

    /** The dialog for an error nobody handled. Technical details are in the log, not here. */
    public void unexpectedError() {
        FxThread.check();
        showAlert(
                AlertType.ERROR,
                "javafx.dialog.error.title",
                messages.get("javafx.dialog.unexpected.header"),
                messages.get("javafx.dialog.unexpected.message"),
                closeButton());
    }

    /**
     * Shows a screen in a modal window.
     *
     * @param prepare receives the controller before the window opens, to pass parameters
     * @return the controller, to read the result from
     */
    public <C> C showModal(Class<C> controllerType, String titleKey, Consumer<? super C> prepare) {
        FxThread.check();
        String title = messages.get(titleKey);
        View<C> view = views.load(controllerType);
        prepare.accept(view.controller());

        var stage = new Stage();
        var scene = new Scene(view.root());
        var activeWindow = ActiveWindow.find();
        activeWindow.ifPresentOrElse(
                owner -> {
                    stage.initOwner(owner);
                    stage.initModality(Modality.WINDOW_MODAL);
                    if (owner.getScene() != null) {
                        scene.getStylesheets().setAll(owner.getScene().getStylesheets());
                    }
                },
                () -> stage.initModality(Modality.APPLICATION_MODAL));
        stage.setTitle(title);
        stage.setScene(scene);
        try {
            ActiveWindow.closingWith(activeWindow, stage::hide, () -> {
                stage.showAndWait();
                return view;
            });
        } finally {
            // A node can be the root of one scene only, and a cached view comes back.
            scene.setRoot(new Region());
        }
        return view.controller();
    }

    private Optional<ButtonType> showAlert(
            AlertType type, String titleKey, String header, String text, ButtonType... buttons) {
        var alert = new Alert(type, text, buttons);
        alert.setTitle(messages.get(titleKey));
        alert.setHeaderText(header);
        var pane = alert.getDialogPane();
        pane.setMinHeight(Region.USE_PREF_SIZE); // otherwise long translations are cut off
        var activeWindow = ActiveWindow.find();
        activeWindow.ifPresent(owner -> {
            alert.initOwner(owner);
            if (owner.getScene() != null) {
                pane.getStylesheets().addAll(owner.getScene().getStylesheets());
            }
        });
        return ActiveWindow.closingWith(activeWindow, alert::close, alert::showAndWait);
    }

    // Own button types: the texts of the built-in ones follow the JVM locale, not the selected language.
    private ButtonType okButton() {
        return new ButtonType(messages.get("javafx.dialog.ok"), ButtonData.OK_DONE);
    }

    private ButtonType closeButton() {
        return new ButtonType(messages.get("javafx.dialog.close"), ButtonData.CANCEL_CLOSE);
    }
}
