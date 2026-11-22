package com.twister.fx.javafx.navigation;

import com.twister.fx.javafx.concurrent.FxThread;
import com.twister.fx.javafx.view.View;
import com.twister.fx.javafx.view.ViewLoader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Parent;
import lombok.RequiredArgsConstructor;

/** {@link Navigator} of one window. Another window creates its own. */
@RequiredArgsConstructor
public class StackNavigator implements Navigator {

    private final ViewLoader views;
    private final Deque<View<?>> history = new ArrayDeque<>();
    private final ReadOnlyObjectWrapper<View<?>> current = new ReadOnlyObjectWrapper<>(this, "current");
    private Consumer<? super Parent> host = root -> {};

    @Override
    public void attach(Consumer<? super Parent> host) {
        FxThread.check();
        this.host = host;
        if (current.get() != null) {
            host.accept(current.get().root());
        }
    }

    @Override
    public <C> C open(Class<C> controllerType, Consumer<? super C> prepare) {
        FxThread.check();
        View<C> next = views.load(controllerType);
        prepare.accept(next.controller());
        View<?> previous = current.get();
        // A cached screen opened again is the same view: it is not its own predecessor.
        if (previous != null && previous != next) {
            history.push(previous);
        }
        show(next);
        return next.controller();
    }

    @Override
    public <C> C switchTo(Class<C> controllerType, Consumer<? super C> prepare) {
        FxThread.check();
        View<C> next = views.load(controllerType);
        prepare.accept(next.controller());
        history.clear();
        show(next);
        return next.controller();
    }

    @Override
    public boolean back() {
        FxThread.check();
        if (history.isEmpty()) {
            return false;
        }
        show(history.pop());
        return true;
    }

    @Override
    public ReadOnlyObjectProperty<View<?>> currentProperty() {
        return current.getReadOnlyProperty();
    }

    private void show(View<?> view) {
        host.accept(view.root());
        current.set(view);
    }
}
