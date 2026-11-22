package com.twister.fx.javafx;

import com.twister.fx.javafx.theme.ThemeMode;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Settings of the starter. All of them have defaults.
 *
 * @param locale language of the texts and format of numbers and dates
 */
// @DefaultValue on the nested records too: without it they are null when no property is set.
@ConfigurationProperties("twister.javafx")
public record JavaFxProperties(
        @DefaultValue("en_US") Locale locale,
        @DefaultValue("light") ThemeMode theme,
        @DefaultValue Cache cache,
        @DefaultValue Tasks tasks,
        @DefaultValue Notifications notifications,
        @DefaultValue Window window) {

    /**
     * @param enabled switches caching of the screens marked {@code @FxView(cached = true)}
     * @param ttl how long a screen is kept unless {@code @FxView} sets its own time
     */
    public record Cache(
            @DefaultValue("false") boolean enabled,
            @DefaultValue("1h") Duration ttl) {}

    /** @param shutdownTimeout how long closing waits for running tasks before interrupting them */
    public record Tasks(@DefaultValue("5s") Duration shutdownTimeout) {}

    /** @param duration how long a notification stays visible */
    public record Notifications(@DefaultValue("4s") Duration duration) {}

    /** @param stateFile by default {@code window-state.properties} in the user's folder of the application */
    public record Window(Path stateFile) {}
}
