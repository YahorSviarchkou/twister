package com.twister.fx.javafx.window;

import com.twister.fx.javafx.concurrent.FxThread;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Keeps size, position and maximized state of windows between runs. {@code SpringFxApplication} attaches the
 * primary stage. Saved bounds win over the size the application sets before showing the stage.
 */
public class WindowState implements AutoCloseable {

    private static final String PRIMARY = "main";
    private static final String FILE_NAME = "window-state.properties";

    private final WindowStateFile file;
    private final Map<Stage, Boolean> attached = new WeakHashMap<>();
    private final Set<Tracker> showing = ConcurrentHashMap.newKeySet();

    public WindowState(Path file) {
        this.file = new WindowStateFile(file);
    }

    /**
     * @param localAppData {@code %LOCALAPPDATA%}, null or empty outside Windows
     * @return the file in a folder named after the application in the user's profile
     */
    public static Path defaultFile(String applicationName, String localAppData, String userHome) {
        // The name becomes one folder: no separators, and no "..".
        String folder = applicationName.replaceAll("[^\\p{L}\\p{N}._-]", "_").replaceAll("^\\.+", "");
        if (folder.isEmpty()) {
            folder = "javafx-application";
        }
        return localAppData == null || localAppData.isEmpty()
                ? Path.of(userHome, "." + folder, FILE_NAME)
                : Path.of(localAppData, folder, FILE_NAME);
    }

    public void attach(Stage stage) {
        attach(stage, PRIMARY);
    }

    /** @param name distinguishes the windows of one application */
    public void attach(Stage stage, String name) {
        FxThread.check();
        if (attached.putIfAbsent(stage, true) != null) {
            return;
        }
        var tracker = new Tracker(stage, name);
        if (stage.isShowing()) {
            tracker.restore();
            tracker.shown();
        } else {
            // On showing: after the application has set its scene and size, before the window appears.
            stage.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<>() {
                @Override
                public void handle(WindowEvent event) {
                    stage.removeEventHandler(WindowEvent.WINDOW_SHOWING, this);
                    tracker.restore();
                }
            });
        }
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> tracker.shown());
        stage.addEventHandler(WindowEvent.WINDOW_HIDING, event -> tracker.hiding());
    }

    /** Saves the windows still open, as {@code System.exit} does not hide them. May run on any thread. */
    @Override
    public void close() {
        showing.forEach(Tracker::save);
    }

    private final class Tracker {

        private final Stage stage;
        private final String name;
        // Snapshots, because close() may run on the shutdown hook thread.
        private volatile Rectangle2D normalBounds;
        private volatile boolean maximized;
        private boolean captureScheduled;

        private Tracker(Stage stage, String name) {
            this.stage = stage;
            this.name = name;
            InvalidationListener changed = property -> scheduleCapture();
            stage.xProperty().addListener(changed);
            stage.yProperty().addListener(changed);
            stage.widthProperty().addListener(changed);
            stage.heightProperty().addListener(changed);
            stage.maximizedProperty().addListener(changed);
        }

        private void restore() {
            file.read(name).ifPresent(saved -> {
                if (saved.bounds() != null) {
                    var bounds = WindowPlacement.fit(saved.bounds(), screens());
                    stage.setX(bounds.getMinX());
                    stage.setY(bounds.getMinY());
                    stage.setWidth(bounds.getWidth());
                    stage.setHeight(bounds.getHeight());
                    normalBounds = bounds;
                }
                maximized = saved.maximized();
                if (saved.maximized()) {
                    stage.setMaximized(true);
                }
            });
        }

        private void shown() {
            showing.add(this);
            scheduleCapture();
        }

        private void hiding() {
            capture();
            save();
            showing.remove(this);
        }

        // Maximizing changes the bounds before the maximized flag, so read both once the change is complete.
        private void scheduleCapture() {
            if (!captureScheduled && showing.contains(this)) {
                captureScheduled = true;
                Platform.runLater(() -> {
                    captureScheduled = false;
                    if (showing.contains(this)) {
                        capture();
                    }
                });
            }
        }

        private void capture() {
            // Minimized windows report a position far off the screen.
            if (stage.isIconified() || stage.isFullScreen()) {
                return;
            }
            maximized = stage.isMaximized();
            if (!maximized && stage.getWidth() >= 1 && stage.getHeight() >= 1) {
                normalBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            }
        }

        private void save() {
            file.write(name, new SavedWindow(normalBounds, maximized));
        }
    }

    private static List<Rectangle2D> screens() {
        var primary = Screen.getPrimary();
        var result = new ArrayList<Rectangle2D>();
        result.add(primary.getVisualBounds());
        Screen.getScreens().stream()
                .filter(screen -> !screen.equals(primary))
                .forEach(screen -> result.add(screen.getVisualBounds()));
        return result;
    }
}
