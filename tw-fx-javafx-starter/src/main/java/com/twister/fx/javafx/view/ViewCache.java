package com.twister.fx.javafx.view;

import com.github.benmanes.caffeine.cache.Ticker;
import java.time.Duration;
import java.util.Optional;

/** Keeps views of {@code @FxView(cached = true)} screens, root and controller together. */
public interface ViewCache {

    <C> Optional<View<C>> find(Class<C> controllerType);

    /** @return the view to use: the one cached earlier if there is one */
    <C> View<C> putIfAbsent(Class<C> controllerType, View<C> view);

    void evict(Class<?> controllerType);

    /** For changes that make every screen stale, such as signing out. */
    void clear();

    /** @param ttl for screens without their own {@link FxView#ttlSeconds()} */
    static ViewCache expiring(Duration ttl) {
        return new CaffeineViewCache(ttl, Ticker.systemTicker());
    }

    static ViewCache none() {
        return new NoViewCache();
    }
}
