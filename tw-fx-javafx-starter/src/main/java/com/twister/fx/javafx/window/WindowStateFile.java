package com.twister.fx.javafx.window;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Properties;
import javafx.geometry.Rectangle2D;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** A damaged or unwritable file costs the remembered position, never the start or the exit. */
@Slf4j
@RequiredArgsConstructor
final class WindowStateFile {

    private final Path file;

    Optional<SavedWindow> read(String name) {
        var values = load();
        String maximized = values.getProperty(name + ".maximized");
        if (maximized == null) {
            return Optional.empty();
        }
        Rectangle2D bounds = null;
        if (values.getProperty(name + ".width") != null) {
            double x = number(values, name + ".x");
            double y = number(values, name + ".y");
            double width = number(values, name + ".width");
            double height = number(values, name + ".height");
            if (!Double.isFinite(x + y + width + height) || width < 1 || height < 1) {
                log.warn("Ignoring the damaged state of window '{}' in {}", name, file);
                return Optional.empty();
            }
            bounds = new Rectangle2D(x, y, width, height);
        }
        return Optional.of(new SavedWindow(bounds, Boolean.parseBoolean(maximized)));
    }

    synchronized void write(String name, SavedWindow state) {
        var values = load();
        values.setProperty(name + ".maximized", Boolean.toString(state.maximized()));
        var bounds = state.bounds();
        if (bounds != null) {
            values.setProperty(name + ".x", Double.toString(bounds.getMinX()));
            values.setProperty(name + ".y", Double.toString(bounds.getMinY()));
            values.setProperty(name + ".width", Double.toString(bounds.getWidth()));
            values.setProperty(name + ".height", Double.toString(bounds.getHeight()));
        }

        Path target = file.toAbsolutePath();
        Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
        try {
            Files.createDirectories(target.getParent());
            try (var out = Files.newBufferedWriter(temporary)) {
                values.store(out, "Window state");
            }
            // Replaced whole, so that a crash while writing cannot leave half a file.
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            log.warn("Cannot save the window state to {}", target, e);
        }
    }

    private Properties load() {
        var values = new Properties();
        if (Files.notExists(file)) {
            return values;
        }
        try (var in = Files.newBufferedReader(file)) {
            values.load(in);
            return values;
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Ignoring the unreadable window state file {}", file, e);
            return new Properties();
        }
    }

    private static double number(Properties values, String key) {
        try {
            return Double.parseDouble(values.getProperty(key, "NaN"));
        } catch (NumberFormatException e) {
            log.debug("Not a number in {}", key, e);
            return Double.NaN;
        }
    }
}
