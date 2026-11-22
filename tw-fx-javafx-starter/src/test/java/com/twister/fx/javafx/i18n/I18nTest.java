package com.twister.fx.javafx.i18n;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acme.app.TestApp;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class I18nTest {

    private static final Locale FILIPINO = Locale.of("fil");
    private static final Locale GEORGIAN = Locale.of("ka");
    private static final Locale RUSSIAN = Locale.of("ru");

    @Test
    void languagesComeFromTheBundlesOfASecondJarToo() {
        URL pack = getClass().getResource("/i18n/javafx/messages_ka.properties");
        assertThat(pack).isNotNull();
        assertThat(pack.getProtocol()).as("the pack is a jar, not a folder").isEqualTo("jar");

        try (var context = TestApp.start()) {
            assertThat(context.getBean(AvailableLanguages.class).locales())
                    .containsExactly(Locale.ENGLISH, FILIPINO, GEORGIAN, RUSSIAN);
        }
    }

    @Test
    void aPackNextToTheBaseFileTranslates() {
        try (var context = TestApp.start()) {
            assertThat(context.getMessage("javafx.dialog.cancel", null, GEORGIAN))
                    .isEqualTo("Gaukmeba");
            assertThat(context.getMessage("javafx.dialog.ok", null, GEORGIAN)).isEqualTo("OK");
        }
    }

    // Without help ResourceBundleMessageSource throws here for unknown locales and with a cache duration.
    @ParameterizedTest
    @ValueSource(strings = {"", "--spring.messages.cache-duration=1h"})
    void aPackInAFolderOfItsOwnTranslatesAndDoesNotBreakOtherLanguages(String argument) {
        String[] args = argument.isEmpty() ? new String[0] : new String[] {argument};
        try (var context = TestApp.start(args)) {
            assertThat(context.getMessage("javafx.dialog.close", null, FILIPINO))
                    .isEqualTo("Isara");
            assertThat(context.getMessage("javafx.dialog.close", null, Locale.ENGLISH))
                    .isEqualTo("Close");
            assertThat(context.getMessage("testapp.title", null, Locale.of("qq", "ZZ")))
                    .isEqualTo("Orders");
        }
    }

    @Test
    void englishDoesNotFallBackToTheLanguageOfTheComputer() {
        Locale computer = Locale.getDefault();
        Locale.setDefault(RUSSIAN);
        try (var context = TestApp.start()) {
            assertThat(context.getMessage("javafx.dialog.close", null, Locale.ENGLISH))
                    .isEqualTo("Close");
        } finally {
            Locale.setDefault(computer);
        }
    }

    @Test
    void theApplicationOverridesTextsOfTheStarterAndKeepsItsConfiguredBundles() {
        try (var context = TestApp.start("--spring.messages.basename=extra/texts")) {
            assertThat(context.getMessage("extra.key", null, Locale.ENGLISH)).isEqualTo("Configured");
            assertThat(context.getMessage("testapp.title", null, Locale.ENGLISH))
                    .isEqualTo("Orders");
            assertThat(context.getMessage("javafx.dialog.info.title", null, Locale.ENGLISH))
                    .isEqualTo("Notice");
        }
    }

    @Test
    void textsFollowTheSelectedLanguage() {
        try (var context = TestApp.start("--twister.javafx.locale=fil")) {
            assertThat(context.getBean(FxMessages.class).get("javafx.dialog.close"))
                    .isEqualTo("Isara");
            assertThat(context.getBean(FxMessages.class).get("testapp.count", 3))
                    .isEqualTo("3 items");
            assertThat(context.getBean(ResourceBundle.class).getString("javafx.dialog.close"))
                    .isEqualTo("Isara");
        }
    }

    @Test
    void theResourceBundleListsRealKeys() {
        try (var context = TestApp.start()) {
            var bundle = context.getBean(ResourceBundle.class);

            assertThat(Collections.list(bundle.getKeys())).contains("testapp.title", "javafx.dialog.ok");
            assertThat(bundle.keySet()).contains("testapp.title", "javafx.dialog.ok");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"false", "true"})
    void aMissingKeyGivesMissingResourceException(String codeAsDefault) {
        try (var context = TestApp.start("--spring.messages.use-code-as-default-message=" + codeAsDefault)) {
            var bundle = context.getBean(ResourceBundle.class);

            assertThat(bundle.containsKey("no.such.key")).isFalse();
            assertThatThrownBy(() -> bundle.getString("no.such.key")).isInstanceOf(MissingResourceException.class);
        }
    }

    @Test
    void readsTheLanguageFromTheFileNameAndIgnoresOtherFiles(@TempDir Path classpath) throws Exception {
        for (String file : new String[] {
            "shop/messages.properties",
            "shop/messages_pt_BR.properties",
            "shop/messages_sr_Latn_RS.properties",
            "shop/messages_es_419.properties",
            "shop/messages_en.properties",
            "shop/messages-old.properties",
            "shop/messages_backup.properties",
            "dotted.name/messages_de.properties",
            "messages_fr.properties"
        }) {
            Path path = classpath.resolve("i18n").resolve(file);
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }

        try (var loader = new URLClassLoader(new URL[] {classpath.toUri().toURL()}, null)) {
            var bundles = MessageBundles.find(loader);

            assertThat(bundles.languages())
                    .extracting(Locale::toLanguageTag)
                    .containsExactly("en", "es-419", "fr", "pt-BR", "sr-Latn-RS");
            assertThat(bundles.basenames()).containsExactly("i18n/messages", "i18n/shop/messages");
        }
    }
}
