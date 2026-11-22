package com.twister.fx.javafx.i18n;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;

/** Lets Boot's message source read language packs that have no base file, see {@link MessageBundles}. */
public class MessageBundlesPostProcessor implements BeanPostProcessor, BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME.equals(beanName)
                && bean instanceof ResourceBundleMessageSource source) {
            source.setBundleClassLoader(MessageBundles.find(classLoader).classLoader());
        }
        return bean;
    }
}
