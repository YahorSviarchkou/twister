package com.twister.fx.javafx.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.twister.fx.javafx.JavaFxProperties;
import com.twister.fx.javafx.annotation.FXController;
import javafx.scene.layout.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class CaffeineScreenProvider implements ScreenProvider {

    private final JavaFxProperties properties;

    private final Cache<Class<?>, Region> cache = Caffeine.newBuilder()
            .expireAfter(new RegionExpiry())
            .build();

    @Override
    public Region get(Class<?> controllerClass) {
        Region region = cache.getIfPresent(controllerClass);
        if (region != null) {
            log.debug("Found cached screen for controller: {}", controllerClass.getSimpleName());
        }
        return region;
    }

    @Override
    public void put(Class<?> controllerClass, Region region) {
        if (isCacheable(controllerClass)) {
            log.debug("Caching screen for controller: {}", controllerClass.getSimpleName());
            cache.put(controllerClass, region);
        }
    }

    @Override
    public void clear(Class<?> controllerClass) {
        log.debug("Invalidating cache for controller: {}", controllerClass.getSimpleName());
        cache.invalidate(controllerClass);
    }

    private boolean isCacheable(Class<?> controllerClass) {
        FXController annotation = controllerClass.getAnnotation(FXController.class);
        return annotation != null && annotation.cached() && properties.cache().globalEnabled();
    }

    private long calculateTtl(Class<?> controllerClass) {
        FXController annotation = controllerClass.getAnnotation(FXController.class);

        int ttlSeconds = (annotation != null && annotation.ttl() != -1)
                ? annotation.ttl()
                : properties.cache().defaultTtl();

        log.trace("Calculated TTL for {}: {} seconds", controllerClass.getSimpleName(), ttlSeconds);
        return TimeUnit.SECONDS.toNanos(ttlSeconds);
    }

    private class RegionExpiry implements Expiry<Class<?>, Region> {

        @Override
        public long expireAfterCreate(Class<?> key, Region value, long currentTime) {
            return calculateTtl(key);
        }

        @Override
        public long expireAfterUpdate(Class<?> key, Region value, long currentTime, long currentDuration) {
            return currentDuration;
        }

        @Override
        public long expireAfterRead(Class<?> key, Region value, long currentTime, long currentDuration) {
            return currentDuration;
        }
    }
}
