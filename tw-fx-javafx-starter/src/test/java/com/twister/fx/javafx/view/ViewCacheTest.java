package com.twister.fx.javafx.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import javafx.scene.Group;
import org.junit.jupiter.api.Test;

class ViewCacheTest {

    @FxView(cached = true)
    private static class DefaultTime {}

    @FxView(cached = true, ttlSeconds = 10)
    private static class OwnTime {}

    private final AtomicLong nanos = new AtomicLong();
    private final ViewCache cache = new CaffeineViewCache(Duration.ofMinutes(1), nanos::get);

    @Test
    void aScreensOwnTimeWinsOverTheDefault() {
        cache.putIfAbsent(DefaultTime.class, new View<>(new Group(), new DefaultTime()));
        cache.putIfAbsent(OwnTime.class, new View<>(new Group(), new OwnTime()));

        nanos.set(Duration.ofSeconds(11).toNanos());
        assertThat(cache.find(OwnTime.class)).isEmpty();
        assertThat(cache.find(DefaultTime.class)).isPresent();

        nanos.set(Duration.ofSeconds(61).toNanos());
        assertThat(cache.find(DefaultTime.class)).isEmpty();
    }

    @Test
    void theFirstViewPutWinsSoThatRacingLoadsShareOne() {
        var first = new View<>(new Group(), new DefaultTime());
        var second = new View<>(new Group(), new DefaultTime());

        assertThat(cache.putIfAbsent(DefaultTime.class, first)).isSameAs(first);
        assertThat(cache.putIfAbsent(DefaultTime.class, second)).isSameAs(first);
    }
}
