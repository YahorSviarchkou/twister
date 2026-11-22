package com.twister.desktop;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Creates {@code twister.data-dir}. Not a bean: the log opens its file before any bean exists, and SQLite does not
 * create folders.
 */
class DataDirectoryEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY = "twister.data-dir";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        var directory = Path.of(environment.getRequiredProperty(PROPERTY));
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create the data folder %s (%s)".formatted(directory, PROPERTY), e);
        }
    }

    /** After the configuration files are read. All post-processors run before logging starts. */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
