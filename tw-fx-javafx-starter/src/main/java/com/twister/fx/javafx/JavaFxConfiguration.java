package com.twister.fx.javafx;

import com.twister.fx.javafx.cache.CaffeineScreenProvider;
import com.twister.fx.javafx.cache.NoOpScreenProvider;
import com.twister.fx.javafx.cache.ScreenProvider;
import javafx.fxml.FXMLLoader;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.ResourceBundle;

@AutoConfiguration
@ComponentScan("com.twister.fx.javafx")
@EnableConfigurationProperties(JavaFxProperties.class)
public class JavaFxConfiguration {

    @Bean
    @Scope("prototype")
    public FXMLLoader fxmlLoader(ResourceBundle resourceBundle) {
        var loader = new FXMLLoader();
        loader.setResources(resourceBundle);
        return loader;
    }

    @Bean
    public MessageSource messageSource(JavaFxProperties properties) {
        var messageSource = new ReloadableResourceBundleMessageSource();
        var bundle = properties.bundle();

        messageSource.setBasename(bundle.baseName());
        messageSource.setDefaultEncoding(bundle.encoding());
        messageSource.setCacheSeconds(bundle.cacheSeconds());

        return messageSource;
    }

    @Bean
    public Locale locale(JavaFxProperties properties) {
        return properties.locale();
    }

    @Bean
    public ResourceBundle resourceBundle(MessageSource messageSource, Locale locale) {
        return new MessageSourceResourceBundle(messageSource, locale);
    }

    @Bean
    @ConditionalOnMissingBean(ScreenProvider.class)
    @ConditionalOnProperty(prefix = "twister.javafx.cache", name = "enabled", havingValue = "true")
    public ScreenProvider caffeineScreenProvider(JavaFxProperties properties) {
        return new CaffeineScreenProvider(properties);
    }

    @Bean
    @ConditionalOnMissingBean(ScreenProvider.class)
    public ScreenProvider noOpScreenProvider() {
        return new NoOpScreenProvider();
    }
}
