package com.twister.fx.javafx.i18n;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

/** Texts in the selected language, for code. FXML uses {@code %key}. */
// TODO(discuss): i18n.md asks for ICU plurals, but Boot's MessageSource formats with java.text.MessageFormat,
// which has none. Add ICU4J, and where: here or as our own MessageSource?
@RequiredArgsConstructor
public class FxMessages {

    private final MessageSource messages;
    private final Locale locale;

    /** @throws org.springframework.context.NoSuchMessageException if no bundle has the key */
    public String get(String key, Object... args) {
        return messages.getMessage(key, args, locale);
    }
}
