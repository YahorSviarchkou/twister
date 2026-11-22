package com.twister.fx.javafx;

import com.twister.fx.javafx.concurrent.FxTasks;
import com.twister.fx.javafx.dialog.Dialogs;
import com.twister.fx.javafx.dialog.Notifier;
import com.twister.fx.javafx.error.FxErrorHandler;
import com.twister.fx.javafx.i18n.AvailableLanguages;
import com.twister.fx.javafx.i18n.FxMessages;
import com.twister.fx.javafx.i18n.MessageBundles;
import com.twister.fx.javafx.i18n.MessageBundlesPostProcessor;
import com.twister.fx.javafx.i18n.MessageSourceResourceBundle;
import com.twister.fx.javafx.navigation.Navigator;
import com.twister.fx.javafx.navigation.StackNavigator;
import com.twister.fx.javafx.theme.ThemeManager;
import com.twister.fx.javafx.view.ViewCache;
import com.twister.fx.javafx.view.ViewLoader;
import com.twister.fx.javafx.window.WindowState;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.core.env.Environment;

/** Declares the beans of the starter. The application replaces any of them by declaring its own. */
@AutoConfiguration
@EnableConfigurationProperties(JavaFxProperties.class)
public class JavaFxAutoConfiguration {

    // Static, because bean post-processors are created before the configurations.
    @Bean
    @ConditionalOnMissingBean
    static MessageBundlesPostProcessor messageBundlesPostProcessor() {
        return new MessageBundlesPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    MessageBundles messageBundles(ApplicationContext context) {
        return MessageBundles.find(context.getClassLoader());
    }

    @Bean
    @ConditionalOnMissingBean
    AvailableLanguages availableLanguages(MessageBundles bundles) {
        return new AvailableLanguages(bundles.languages());
    }

    @Bean
    @ConditionalOnMissingBean
    ResourceBundle fxResourceBundle(
            @Qualifier(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME) MessageSource messages,
            MessageBundles bundles,
            JavaFxProperties properties) {
        Collection<String> basenames = messages instanceof AbstractResourceBasedMessageSource source
                ? source.getBasenameSet()
                : bundles.basenames();
        return new MessageSourceResourceBundle(
                messages, properties.locale(), bundles.keys(properties.locale(), basenames));
    }

    @Bean
    @ConditionalOnMissingBean
    FxMessages fxMessages(
            @Qualifier(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME) MessageSource messages,
            JavaFxProperties properties) {
        return new FxMessages(messages, properties.locale());
    }

    @Bean
    @ConditionalOnMissingBean
    ViewCache viewCache(JavaFxProperties properties) {
        var cache = properties.cache();
        return cache.enabled() ? ViewCache.expiring(cache.ttl()) : ViewCache.none();
    }

    @Bean
    @ConditionalOnMissingBean
    ViewLoader viewLoader(ApplicationContext context, ResourceBundle resources, ViewCache cache) {
        return new ViewLoader(context, resources, cache);
    }

    @Bean
    @ConditionalOnMissingBean
    Navigator navigator(ViewLoader views) {
        return new StackNavigator(views);
    }

    @Bean
    @ConditionalOnMissingBean
    Dialogs dialogs(FxMessages messages, ViewLoader views) {
        return new Dialogs(messages, views);
    }

    @Bean
    @ConditionalOnMissingBean
    Notifier notifier(FxMessages messages, JavaFxProperties properties) {
        return new Notifier(messages, properties.notifications().duration());
    }

    @Bean
    @ConditionalOnMissingBean
    FxErrorHandler fxErrorHandler(Dialogs dialogs) {
        return new FxErrorHandler(dialogs);
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    FxTasks fxTasks(FxErrorHandler errors, JavaFxProperties properties) {
        return new FxTasks(properties.tasks().shutdownTimeout(), errors::handle);
    }

    @Bean
    @ConditionalOnMissingBean
    ThemeManager themeManager(JavaFxProperties properties) {
        return new ThemeManager(properties.theme());
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    WindowState windowState(JavaFxProperties properties, Environment environment) {
        Path configured = properties.window().stateFile();
        Path file = configured != null
                ? configured
                : WindowState.defaultFile(
                        environment.getProperty("spring.application.name", "javafx-application"),
                        System.getenv("LOCALAPPDATA"),
                        System.getProperty("user.home"));
        return new WindowState(file);
    }
}
