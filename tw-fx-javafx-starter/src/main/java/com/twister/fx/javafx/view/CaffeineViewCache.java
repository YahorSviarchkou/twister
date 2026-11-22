package com.twister.fx.javafx.view;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.Ticker;
import java.time.Duration;
import java.util.Optional;

final class CaffeineViewCache implements ViewCache {

    private final Cache<Class<?>, View<?>> views;

    CaffeineViewCache(Duration defaultTtl, Ticker ticker) {
        views = Caffeine.newBuilder()
                .ticker(ticker)
                .expireAfter(Expiry.<Class<?>, View<?>>creating((type, view) -> ttlOf(type, defaultTtl)))
                .build();
    }

    // putIfAbsent stores under a class only views of that class.
    @Override
    @SuppressWarnings("unchecked")
    public <C> Optional<View<C>> find(Class<C> controllerType) {
        return Optional.ofNullable((View<C>) views.getIfPresent(controllerType));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> View<C> putIfAbsent(Class<C> controllerType, View<C> view) {
        var earlier = (View<C>) views.asMap().putIfAbsent(controllerType, view);
        return earlier != null ? earlier : view;
    }

    @Override
    public void evict(Class<?> controllerType) {
        views.invalidate(controllerType);
    }

    @Override
    public void clear() {
        views.invalidateAll();
    }

    private static Duration ttlOf(Class<?> controllerType, Duration defaultTtl) {
        var annotation = controllerType.getAnnotation(FxView.class);
        return annotation == null || annotation.ttlSeconds() < 0
                ? defaultTtl
                : Duration.ofSeconds(annotation.ttlSeconds());
    }
}
