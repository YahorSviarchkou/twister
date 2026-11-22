package com.twister.fx.javafx;

import lombok.NonNull;
import org.springframework.context.MessageSource;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageSourceResourceBundle extends ResourceBundle {

    private final MessageSource messageSource;
    private final Locale locale;

    public MessageSourceResourceBundle(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }

    @Override
    protected Object handleGetObject(@NonNull String key) {
        return messageSource.getMessage(key, null, locale);
    }

    @NonNull
    @Override
    public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
    }
}
