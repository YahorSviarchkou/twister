package com.twister.fx.javafx.cache;

import javafx.scene.layout.Region;

public class NoOpScreenProvider implements ScreenProvider {

    @Override
    public Region get(Class<?> controllerClass) {
        return null;
    }

    @Override
    public void put(Class<?> controllerClass, Region region) {}

    @Override
    public void clear(Class<?> controllerClass) {}
}
