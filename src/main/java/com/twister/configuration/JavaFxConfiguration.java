package com.twister.configuration;

import javafx.fxml.FXMLLoader;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Locale;
import java.util.ResourceBundle;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JavaFxConfiguration {

    ApplicationContext applicationContext;

    @Bean
    @Scope("prototype")
    public FXMLLoader fxmlLoader() {
        var bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());
        var loader = new FXMLLoader();

        loader.setResources(bundle);
//        loader.setControllerFactory(applicationContext::getBean);

        return loader;
    }
}
