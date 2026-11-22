package com.twister.fx.javafx;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Locale;

@ConfigurationProperties(prefix = "twister.javafx")
public record JavaFxProperties(
        Bundle bundle, @DefaultValue("en_US") Locale locale, ScreenCacheProperties cache) {

    public record Bundle(
            @DefaultValue("classpath:i18n/messages") String baseName,
            @DefaultValue("UTF-8") String encoding,
            @DefaultValue("3600") Integer cacheSeconds) {}

    public record ScreenCacheProperties(
            @DefaultValue("false") boolean globalEnabled,
            @DefaultValue("3600") Integer defaultTtl) {}
}
