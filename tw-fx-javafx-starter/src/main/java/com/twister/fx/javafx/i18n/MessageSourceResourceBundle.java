package com.twister.fx.javafx.i18n;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.MessageSource;

/** Shows a {@link MessageSource} as a {@link ResourceBundle}, for {@code %key} in FXML. */
public class MessageSourceResourceBundle extends ResourceBundle {

    // Not null: with useCodeAsDefaultMessage a message source would answer the key itself.
    private static final String MISSING = "\u0001missing";

    private final MessageSource messages;
    private final Locale locale;
    private final Set<String> keys;

    /** @param keys the keys to list: a {@link MessageSource} cannot list its own */
    public MessageSourceResourceBundle(MessageSource messages, Locale locale, Set<String> keys) {
        this.messages = messages;
        this.locale = locale;
        this.keys = Set.copyOf(keys);
    }

    // null is the contract for a missing key: getString then throws MissingResourceException.
    @Override
    protected Object handleGetObject(String key) {
        Objects.requireNonNull(key, "key");
        String message = messages.getMessage(key, null, MISSING, locale);
        return MISSING.equals(message) ? null : message;
    }

    // The inherited method knows only the listed keys, and a message source may resolve more.
    @Override
    public boolean containsKey(String key) {
        return handleGetObject(key) != null;
    }

    @Override
    protected Set<String> handleKeySet() {
        return keys.stream().filter(this::containsKey).collect(Collectors.toSet());
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(handleKeySet());
    }

    @Override
    public Locale getLocale() {
        return locale;
    }
}
