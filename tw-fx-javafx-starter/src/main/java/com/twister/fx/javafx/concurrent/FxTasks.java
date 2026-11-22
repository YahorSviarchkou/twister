package com.twister.fx.javafx.concurrent;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/** The FX thread never waits for I/O: work runs on virtual threads, callbacks on the FX thread. */
@Slf4j
public class FxTasks implements ApplicationListener<ContextClosedEvent>, AutoCloseable {

    private final ExecutorService background = Executors.newVirtualThreadPerTaskExecutor();
    private final Duration shutdownTimeout;
    private final Consumer<? super Throwable> defaultOnError;
    private volatile boolean closed;

    public FxTasks(Duration shutdownTimeout, Consumer<? super Throwable> defaultOnError) {
        this.shutdownTimeout = shutdownTimeout;
        this.defaultOnError = defaultOnError;
    }

    /** Failures go to {@link com.twister.fx.javafx.error.FxErrorHandler}. */
    public <T> CompletableFuture<T> run(Supplier<? extends T> work, Consumer<? super T> onSuccess) {
        return run(work, onSuccess, defaultOnError);
    }

    /**
     * @return completes with the work, before its callback runs. Cancelling it skips both callbacks and does not
     *     interrupt the work.
     */
    public <T> CompletableFuture<T> run(
            Supplier<? extends T> work, Consumer<? super T> onSuccess, Consumer<? super Throwable> onError) {
        CompletableFuture<T> result;
        try {
            result = CompletableFuture.supplyAsync(work::get, background);
        } catch (RejectedExecutionException e) {
            log.warn("Task rejected: the application is closing", e);
            return CompletableFuture.failedFuture(e);
        }
        result.whenComplete((value, error) -> {
            if (result.isCancelled()) {
                return;
            }
            Throwable failure = unwrap(error);
            if (closed) {
                // The screens and beans behind the callbacks are gone, so only the log is left for a failure.
                logDropped(failure);
                return;
            }
            Platform.runLater(() -> {
                if (closed) {
                    logDropped(failure);
                } else if (!result.isCancelled()) {
                    if (failure == null) {
                        onSuccess.accept(value);
                    } else {
                        onError.accept(failure);
                    }
                }
            });
        });
        return result;
    }

    // Before the beans are destroyed, so that work in flight still has its data source.
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        close();
    }

    /** Waits for running work up to the timeout, then interrupts it. */
    @Override
    public void close() {
        closed = true;
        background.shutdown();
        try {
            // Not ExecutorService.close(): it waits without limit, and this runs on the FX thread on exit.
            if (!background.awaitTermination(shutdownTimeout.toNanos(), TimeUnit.NANOSECONDS)) {
                log.warn("Tasks still running after {}, interrupting them", shutdownTimeout);
                background.shutdownNow();
            }
        } catch (InterruptedException e) {
            background.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static void logDropped(Throwable failure) {
        if (failure != null) {
            log.error("A task failed while the application was closing", failure);
        }
    }

    private static Throwable unwrap(Throwable error) {
        return error instanceof CompletionException && error.getCause() != null ? error.getCause() : error;
    }
}
