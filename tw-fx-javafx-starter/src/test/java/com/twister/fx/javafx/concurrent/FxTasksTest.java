package com.twister.fx.javafx.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.fx.javafx.testing.Fx;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FxTasksTest {

    private final List<Throwable> unhandled = new CopyOnWriteArrayList<>();
    private final FxTasks tasks = new FxTasks(Duration.ofSeconds(Fx.TIMEOUT_SECONDS), unhandled::add);

    @BeforeAll
    static void startToolkit() throws Exception {
        Fx.start();
    }

    @AfterEach
    void closeTasks() {
        tasks.close();
    }

    @Test
    void worksOnAVirtualThreadAndCallsBackOnTheFxThread() throws Exception {
        var calledBack = new CompletableFuture<Boolean>();

        var work = tasks.run(
                () -> Thread.currentThread().isVirtual() && !Platform.isFxApplicationThread(),
                value -> calledBack.complete(Platform.isFxApplicationThread()));

        assertThat(get(work)).as("work off the FX thread").isTrue();
        assertThat(get(calledBack)).as("callback on the FX thread").isTrue();
    }

    @Test
    void aFailureReachesOnErrorUnwrappedOnTheFxThread() throws Exception {
        var failure = new IllegalStateException("no database");
        var received = new CompletableFuture<Throwable>();
        var onFxThread = new AtomicBoolean();

        tasks.<String>run(
                () -> {
                    throw failure;
                },
                value -> received.complete(null),
                error -> {
                    onFxThread.set(Platform.isFxApplicationThread());
                    received.complete(error);
                });

        assertThat(get(received)).isSameAs(failure);
        assertThat(onFxThread).isTrue();
    }

    @Test
    void aFailureWithoutItsOwnCallbackGoesToTheDefaultOne() throws Exception {
        var failure = new AssertionError("broken");
        var received = new CompletableFuture<Throwable>();
        try (var withDefault = new FxTasks(Duration.ofSeconds(1), received::complete)) {
            withDefault.<String>run(
                    () -> {
                        throw failure;
                    },
                    value -> {});

            assertThat(get(received)).isSameAs(failure);
        }
    }

    @Test
    void workThatThrowsCancellationIsAFailureNotACancelledTask() throws Exception {
        var received = new CompletableFuture<Throwable>();

        tasks.<String>run(
                () -> {
                    throw new CancellationException("by the driver");
                },
                value -> received.complete(null),
                received::complete);

        assertThat(get(received)).isInstanceOf(CancellationException.class);
    }

    @Test
    void cancellingSkipsBothCallbacks() throws Exception {
        var release = new CountDownLatch(1);
        var called = new AtomicBoolean();

        var task = tasks.run(() -> await(release), value -> called.set(true), error -> called.set(true));
        task.cancel(false);
        release.countDown();
        Fx.flush();

        assertThat(called).isFalse();
    }

    @Test
    void closingWaitsForRunningWork() throws Exception {
        var started = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var finished = new AtomicBoolean();
        tasks.run(
                () -> {
                    started.countDown();
                    await(release);
                    finished.set(true);
                    return true;
                },
                value -> {});
        assertThat(started.await(Fx.TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();

        var closer = Thread.ofPlatform().start(tasks::close);
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(Fx.TIMEOUT_SECONDS);
        while (closer.getState() != Thread.State.TIMED_WAITING && System.nanoTime() < deadline) {
            Thread.onSpinWait();
        }
        assertThat(closer.getState()).as("close() waits").isEqualTo(Thread.State.TIMED_WAITING);
        release.countDown();

        assertThat(closer.join(Duration.ofSeconds(Fx.TIMEOUT_SECONDS))).isTrue();
        assertThat(finished).isTrue();
    }

    @Test
    void closingInterruptsWorkThatOutlivesTheTimeout() throws Exception {
        var started = new CountDownLatch(1);
        var interrupted = new CompletableFuture<Boolean>();
        try (var impatient = new FxTasks(Duration.ofMillis(1), unhandled::add)) {
            impatient.run(
                    () -> {
                        started.countDown();
                        try {
                            return new CountDownLatch(1).await(Fx.TIMEOUT_SECONDS, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            return interrupted.complete(true);
                        }
                    },
                    value -> {});
            assertThat(started.await(Fx.TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();

            impatient.close();

            assertThat(get(interrupted)).isTrue();
        }
    }

    @Test
    void afterClosingNoCallbackRunsAndNewWorkIsRejected() throws Exception {
        var fxBusy = new CountDownLatch(1);
        var called = new AtomicBoolean();
        Platform.runLater(() -> await(fxBusy));
        try {
            get(tasks.run(() -> "saved", value -> called.set(true))); // its callback now waits for the FX thread
            tasks.close();
        } finally {
            fxBusy.countDown();
        }
        Fx.flush();

        assertThat(called).isFalse();
        assertThat(tasks.run(() -> "late", value -> called.set(true))).isCompletedExceptionally();
    }

    private static <T> T get(CompletableFuture<T> future) throws Exception {
        return future.get(Fx.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private static boolean await(CountDownLatch latch) {
        try {
            return latch.await(Fx.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
