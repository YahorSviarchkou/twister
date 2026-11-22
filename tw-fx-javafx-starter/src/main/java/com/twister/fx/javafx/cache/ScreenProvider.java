package com.twister.fx.javafx.cache;

import javafx.scene.layout.Region;

public interface ScreenProvider {

    Region get(Class<?> controllerClass);

    void put(Class<?> controllerClass, Region region);

    void clear(Class<?> controllerClass);
}
