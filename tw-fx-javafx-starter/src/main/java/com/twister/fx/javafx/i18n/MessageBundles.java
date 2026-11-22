package com.twister.fx.javafx.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * The bundles {@code i18n/**}{@code /messages*.properties} of all jars on the classpath.
 *
 * <p>A language pack either adds {@code messages_<language>.properties} next to the base file of a module or brings
 * a folder of its own. Its jar needs directory entries, which Gradle and Maven write.
 */
@Slf4j
public final class MessageBundles {

    private static final String LOCATION = "classpath*:i18n/**/messages*.properties";
    private static final String ROOT = "/i18n/";
    private static final String OWN_BUNDLE = "i18n/javafx/messages";

    // No dots in folder names: ResourceBundle reads them as folder separators.
    private static final Pattern BUNDLE_FILE = Pattern.compile("(i18n/(?:[\\w-]+/)*messages)"
            + "(?:_([a-z]{2,3})(?:_([A-Z][a-z]{3}))?(?:_([A-Z]{2}|\\d{3}))?)?\\.properties");

    private final List<String> basenames;
    private final List<Locale> languages;
    private final ClassLoader classLoader;

    private MessageBundles(List<String> basenames, List<Locale> languages, ClassLoader classLoader) {
        this.basenames = List.copyOf(basenames);
        this.languages = List.copyOf(languages);
        this.classLoader = classLoader;
    }

    public static MessageBundles find(ClassLoader classLoader) {
        var withBase = new TreeSet<String>();
        var all = new TreeSet<String>();
        var languages = new TreeMap<String, Locale>();
        for (Resource resource : resources(classLoader)) {
            String url = urlOf(resource);
            Matcher file = BUNDLE_FILE.matcher(url.substring(url.lastIndexOf(ROOT) + 1));
            if (!file.matches()) {
                log.warn("Ignoring {}: expected i18n/<module>/messages[_<locale>].properties", url);
                continue;
            }
            all.add(file.group(1));
            if (file.group(2) == null) {
                withBase.add(file.group(1));
            } else {
                var language = new Locale.Builder()
                        .setLanguage(file.group(2))
                        .setScript(file.group(3))
                        .setRegion(file.group(4))
                        .build();
                languages.put(language.toLanguageTag(), language);
            }
        }

        var translationsOnly = new TreeSet<>(all);
        translationsOnly.removeAll(withBase);

        // The first bundle that has a key wins, and a base file answers for every language. So packs without a base
        // file go first, and the starter's own texts last, where the application can override them.
        var basenames = new ArrayList<>(translationsOnly);
        withBase.stream().filter(name -> !name.equals(OWN_BUNDLE)).forEach(basenames::add);
        if (withBase.contains(OWN_BUNDLE)) {
            basenames.add(OWN_BUNDLE);
        }

        languages.remove(Locale.ENGLISH.toLanguageTag());
        var offered = new ArrayList<Locale>();
        offered.add(Locale.ENGLISH);
        offered.addAll(languages.values());

        ClassLoader loader = translationsOnly.isEmpty()
                ? classLoader
                : new EmptyBaseClassLoader(
                        classLoader,
                        translationsOnly.stream()
                                .map(name -> name + ".properties")
                                .collect(Collectors.toUnmodifiableSet()));
        return new MessageBundles(basenames, offered, loader);
    }

    /** In lookup order. */
    public List<String> basenames() {
        return basenames;
    }

    public List<Locale> languages() {
        return languages;
    }

    /** Keys that {@code basenames} offer for {@code locale}, the base files included. */
    public Set<String> keys(Locale locale, Collection<String> basenames) {
        var control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);
        var keys = new TreeSet<String>();
        for (String basename : basenames) {
            try {
                keys.addAll(ResourceBundle.getBundle(basename, locale, classLoader, control)
                        .keySet());
            } catch (MissingResourceException e) {
                log.debug("No bundle {} for {}", basename, locale, e);
            }
        }
        return keys;
    }

    ClassLoader classLoader() {
        return classLoader;
    }

    private static Resource[] resources(ClassLoader classLoader) {
        try {
            return new PathMatchingResourcePatternResolver(classLoader).getResources(LOCATION);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + LOCATION, e);
        }
    }

    private static String urlOf(Resource resource) {
        try {
            return resource.getURL().toString();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot locate " + resource, e);
        }
    }

    /**
     * Gives an empty base file to packs that have only translations. Without it
     * {@code ResourceBundleMessageSource} throws or warns on each lookup in any other language.
     */
    private static final class EmptyBaseClassLoader extends ClassLoader {

        private final Set<String> baseFiles;

        private EmptyBaseClassLoader(ClassLoader parent, Set<String> baseFiles) {
            super(parent);
            this.baseFiles = baseFiles;
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return baseFiles.contains(name) ? InputStream.nullInputStream() : super.getResourceAsStream(name);
        }
    }
}
