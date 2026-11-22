package com.twister.fx.javafx.window;

import java.util.List;
import javafx.geometry.Rectangle2D;

/** Keeps restored bounds where the user can reach the window. */
final class WindowPlacement {

    private static final double TITLE_HEIGHT = 32;
    private static final double GRAB_HEIGHT = 16;
    private static final double GRAB_WIDTH = 100;

    private WindowPlacement() {}

    /**
     * @param screens visual bounds of the screens, the primary first
     * @return {@code saved} if its title bar can be reached, shrunk to that screen if bigger, otherwise the same
     *     size centered on the primary screen
     */
    static Rectangle2D fit(Rectangle2D saved, List<Rectangle2D> screens) {
        var titleBar = new Rectangle2D(saved.getMinX(), saved.getMinY(), saved.getWidth(), TITLE_HEIGHT);
        Rectangle2D best = null;
        for (Rectangle2D screen : screens) {
            boolean reachable = overlapX(titleBar, screen) >= Math.min(GRAB_WIDTH, saved.getWidth())
                    && overlapY(titleBar, screen) >= GRAB_HEIGHT;
            if (reachable && (best == null || area(saved, screen) > area(saved, best))) {
                best = screen;
            }
        }
        Rectangle2D screen = best != null ? best : screens.getFirst();
        double width = Math.min(saved.getWidth(), screen.getWidth());
        double height = Math.min(saved.getHeight(), screen.getHeight());
        if (best == null) {
            return new Rectangle2D(
                    screen.getMinX() + (screen.getWidth() - width) / 2,
                    screen.getMinY() + (screen.getHeight() - height) / 2,
                    width,
                    height);
        }
        return new Rectangle2D(
                width < saved.getWidth() ? screen.getMinX() : saved.getMinX(),
                height < saved.getHeight() ? screen.getMinY() : saved.getMinY(),
                width,
                height);
    }

    private static double area(Rectangle2D a, Rectangle2D b) {
        return overlapX(a, b) * overlapY(a, b);
    }

    private static double overlapX(Rectangle2D a, Rectangle2D b) {
        return Math.max(0, Math.min(a.getMaxX(), b.getMaxX()) - Math.max(a.getMinX(), b.getMinX()));
    }

    private static double overlapY(Rectangle2D a, Rectangle2D b) {
        return Math.max(0, Math.min(a.getMaxY(), b.getMaxY()) - Math.max(a.getMinY(), b.getMinY()));
    }
}
