package com.twister.fx.javafx.navigation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.twister.fx.javafx.testing.Fx;
import com.twister.fx.javafx.view.View;
import com.twister.fx.javafx.view.ViewLoader;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StackNavigatorTest {

    private static class Board {}

    private static class Order {}

    private final ViewLoader views = mock(ViewLoader.class);
    private final View<Board> board = new View<>(new Pane(), new Board());
    private final View<Order> order = new View<>(new Pane(), new Order());
    private final StackNavigator navigator = new StackNavigator(views);
    private final List<Parent> shown = new ArrayList<>();

    @BeforeAll
    static void startToolkit() throws Exception {
        Fx.start();
    }

    @Test
    void goesBackThroughWhatWasOpenedOnTop() throws Exception {
        when(views.load(Board.class)).thenReturn(board);
        when(views.load(Order.class)).thenReturn(order);

        Fx.run(() -> {
            navigator.switchTo(Board.class);
            navigator.attach(shown::add);
            navigator.open(Order.class);
            navigator.open(Order.class); // the same cached view

            assertThat(navigator.back()).isTrue();
            assertThat(navigator.back()).isFalse();
        });

        assertThat(shown).containsExactly(board.root(), order.root(), order.root(), board.root());
    }

    @Test
    void switchingForgetsTheHistory() throws Exception {
        when(views.load(Board.class)).thenReturn(board);
        when(views.load(Order.class)).thenReturn(order);

        Fx.run(() -> {
            navigator.open(Board.class);
            navigator.open(Order.class);
            navigator.switchTo(Board.class);

            assertThat(navigator.back()).isFalse();
        });
    }

    @Test
    void aScreenThatFailsToPrepareLeavesTheCurrentOne() throws Exception {
        when(views.load(Board.class)).thenReturn(board);
        when(views.load(Order.class)).thenReturn(order);

        Fx.run(() -> {
            navigator.switchTo(Board.class);
            assertThatThrownBy(() -> navigator.open(Order.class, controller -> {
                        throw new IllegalStateException("no such order");
                    }))
                    .isInstanceOf(IllegalStateException.class);

            assertThat(navigator.currentProperty().get()).isSameAs(board);
            assertThat(navigator.back()).isFalse();
        });
    }

    @Test
    void refusesToNavigateOffTheFxThread() {
        assertThatThrownBy(() -> navigator.open(Board.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("FX thread");
    }
}
