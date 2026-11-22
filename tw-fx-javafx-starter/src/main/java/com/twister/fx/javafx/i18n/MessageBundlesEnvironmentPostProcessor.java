package com.twister.fx.javafx.i18n;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Passes the bundles found on the classpath to Boot's {@code MessageSource} through {@code spring.messages.*}.
 * It runs before the auto-configuration, because Boot creates that message source only if a bundle is configured.
 */
public class MessageBundlesEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String BASENAME = "spring.messages.basename";
    private static final String BOOT_DEFAULT = "messages";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        ClassLoader classLoader = application.getClassLoader();
        List<String> configured = Binder.get(environment)
                .bind(BASENAME, Bindable.listOf(String.class))
                .orElseGet(() -> classLoader.getResource(BOOT_DEFAULT + ".properties") == null
                        ? List.of()
                        : List.of(BOOT_DEFAULT));
        var basenames = new LinkedHashSet<>(configured);
        basenames.addAll(MessageBundles.find(classLoader).basenames());

        var sources = environment.getPropertySources();
        sources.addFirst(new MapPropertySource("fxMessageBundles", Map.of(BASENAME, String.join(",", basenames))));
        // The base files are English: falling back to the system locale would show English users its language.
        sources.addLast(new MapPropertySource(
                "fxMessageDefaults", Map.of("spring.messages.fallback-to-system-locale", "false")));
    }

    /** After the configuration files are read. */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
