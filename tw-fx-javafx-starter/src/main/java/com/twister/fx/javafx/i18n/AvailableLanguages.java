package com.twister.fx.javafx.i18n;

import java.util.List;
import java.util.Locale;

/**
 * Languages the user can choose from, built from the bundles on the classpath. English, the language of the base
 * bundles, comes first.
 */
public record AvailableLanguages(List<Locale> locales) {

    public AvailableLanguages {
        locales = List.copyOf(locales);
    }
}
