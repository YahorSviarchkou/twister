package com.twister.fx.javafx.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Marks a Spring-managed FXML controller: a prototype bean, so each load of a screen has its own. The FXML names
 * the class in {@code fx:controller}. Controllers of nested {@code fx:include} files are marked the same way.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public @interface FxView {

    /** Path relative to the class, or absolute with a leading slash. Default: {@code <SimpleName>.fxml}. */
    String fxml() default "";

    /**
     * Reuses the loaded screen while {@code twister.javafx.cache.enabled} is true. Such a screen can be shown in
     * one place at a time.
     */
    boolean cached() default false;

    /** Seconds to keep a cached screen. Negative: {@code twister.javafx.cache.ttl}. */
    long ttlSeconds() default -1;
}
