package com.twister.fx.javafx.navigation;

import com.twister.fx.javafx.view.View;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Parent;

/** Moves between screens, so that they do not know each other. Call it on the FX thread. */
public interface Navigator {

    /** Sets where screens appear, for example {@code borderPane::setCenter}. Shows the current one at once. */
    void attach(Consumer<? super Parent> host);

    /** Shows a screen on top of the current one, which {@link #back()} returns to. */
    default <C> C open(Class<C> controllerType) {
        return open(controllerType, controller -> {});
    }

    /** @param prepare receives the controller before the screen is shown, to pass parameters */
    <C> C open(Class<C> controllerType, Consumer<? super C> prepare);

    /** Shows a screen and forgets the history, as a menu item does. */
    default <C> C switchTo(Class<C> controllerType) {
        return switchTo(controllerType, controller -> {});
    }

    /** @param prepare receives the controller before the screen is shown, to pass parameters */
    <C> C switchTo(Class<C> controllerType, Consumer<? super C> prepare);

    /** @return false if there was nowhere to go back to */
    boolean back();

    ReadOnlyObjectProperty<View<?>> currentProperty();
}
