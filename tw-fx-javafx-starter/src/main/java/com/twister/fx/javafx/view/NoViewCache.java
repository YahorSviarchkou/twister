package com.twister.fx.javafx.view;

import java.util.Optional;

final class NoViewCache implements ViewCache {

    @Override
    public <C> Optional<View<C>> find(Class<C> controllerType) {
        return Optional.empty();
    }

    @Override
    public <C> View<C> putIfAbsent(Class<C> controllerType, View<C> view) {
        return view;
    }

    @Override
    public void evict(Class<?> controllerType) {}

    @Override
    public void clear() {}
}
