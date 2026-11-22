package com.twister.desktop.app;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.ViolationStore;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Violation store of the frozen rules, wired in {@code archunit.properties}.
 *
 * <p>The default store cannot be used: with updates disabled it fails when a violation is <em>fixed</em>. This one
 * lets the store only shrink: fixed violations are removed (so reintroducing one fails), while new violations and
 * rules without an entry are never recorded and fail the test. A missing or empty store therefore fails too.
 * Recording a baseline needs {@code freeze.store.default.allowStoreCreation=true}.
 *
 * <p>Files are written with LF and without a timestamp, so a run with no change leaves them untouched.
 */
public class FrozenViolationStore implements ViolationStore {

    private static final String RULES_FILE = "stored.rules";

    private Path directory;
    private boolean recording;

    @Override
    public void initialize(Properties properties) {
        directory = Path.of(properties.getProperty("default.path", "archunit_store"));
        recording = Boolean.parseBoolean(properties.getProperty("default.allowStoreCreation", "false"));
    }

    @Override
    public boolean contains(ArchRule rule) {
        return readRules().containsKey(rule.getDescription());
    }

    @Override
    public List<String> getViolations(ArchRule rule) {
        String file = readRules().getProperty(rule.getDescription());
        return file == null ? List.of() : readLines(directory.resolve(file));
    }

    @Override
    public void save(ArchRule rule, List<String> violations) {
        Properties rules = readRules();
        String description = rule.getDescription();
        String file = rules.getProperty(description);
        if (!recording
                && violations.size()
                        > (file == null ? 0 : readLines(directory.resolve(file)).size())) {
            throw new IllegalStateException("Refusing to record new violations of '" + description
                    + "'. Fix them, or set freeze.store.default.allowStoreCreation=true to record a baseline.");
        }
        if (violations.isEmpty()) {
            if (file != null) {
                rules.remove(description);
                delete(directory.resolve(file));
            }
        } else {
            file = file == null ? UUID.randomUUID().toString() : file;
            rules.setProperty(description, file);
            write(directory.resolve(file), String.join("\n", violations) + "\n");
        }
        writeRules(rules);
    }

    private Properties readRules() {
        Properties rules = new Properties();
        Path path = directory.resolve(RULES_FILE);
        if (Files.exists(path)) {
            try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                rules.load(reader);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return rules;
    }

    private void writeRules(Properties rules) {
        var writer = new StringWriter();
        try {
            rules.store(writer, null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        // Properties adds a timestamp comment and uses the platform line separator.
        String content = Arrays.stream(writer.toString().split("\\R"))
                .filter(line -> !line.startsWith("#") && !line.isEmpty())
                .sorted()
                .reduce("", (all, line) -> all + line + "\n");
        write(directory.resolve(RULES_FILE), content);
    }

    private static List<String> readLines(Path path) {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void write(Path path, String content) {
        try {
            Files.createDirectories(directory);
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void delete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
