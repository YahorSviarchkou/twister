package com.twister.fx.javafx.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.twister.fx.javafx.dialog.Dialogs;
import com.twister.fx.javafx.testing.Fx;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.GenericApplicationContext;

@ExtendWith(OutputCaptureExtension.class)
class FxErrorHandlerTest {

    private final Dialogs dialogs = mock(Dialogs.class);
    private final FxErrorHandler handler = new FxErrorHandler(dialogs);

    @BeforeAll
    static void startToolkit() throws Exception {
        Fx.start();
    }

    @Test
    void anErrorFromAnyThreadIsLoggedAndShownOnTheFxThread(CapturedOutput output) throws Exception {
        var onFxThread = new AtomicBoolean();
        doAnswer(call -> {
                    onFxThread.set(Platform.isFxApplicationThread());
                    return null;
                })
                .when(dialogs)
                .unexpectedError();

        handler.uncaughtException(new Thread("importer"), new IllegalStateException("disk is full"));
        Fx.flush();

        assertThat(output).contains("importer", "disk is full");
        assertThat(onFxThread).isTrue();
    }

    @Test
    void aBurstOfErrorsGivesOneDialog(CapturedOutput output) throws Exception {
        Fx.run(() -> {
            handler.handle(new IllegalStateException("first"));
            handler.handle(new IllegalStateException("second"));
        });
        Fx.flush();

        verify(dialogs, times(1)).unexpectedError();
        assertThat(output).contains("first", "second");
    }

    @Test
    void aFailingDialogIsLoggedAndTheNextErrorStillGetsOne(CapturedOutput output) throws Exception {
        doThrow(new AssertionError("dialog is broken"))
                .doNothing()
                .when(dialogs)
                .unexpectedError();

        handler.handle(new IllegalStateException("first"));
        Fx.flush();
        handler.handle(new IllegalStateException("second"));
        Fx.flush();

        verify(dialogs, times(2)).unexpectedError();
        assertThat(output).contains("dialog is broken");
    }

    @Test
    void anErrorRaisedWhileTheDialogShowsDoesNotOpenAnother(CapturedOutput output) throws Exception {
        doAnswer(call -> {
                    handler.handle(new IllegalStateException("error inside the error dialog"));
                    return null;
                })
                .when(dialogs)
                .unexpectedError();

        handler.handle(new IllegalStateException("original"));
        Fx.flush();

        verify(dialogs, times(1)).unexpectedError();
        assertThat(output).contains("error inside the error dialog");
    }

    @Test
    void withoutAToolkitItOnlyLogsAndDoesNotThrow(CapturedOutput output) throws Exception {
        try (var platform = mockStatic(Platform.class)) {
            platform.when(() -> Platform.runLater(any()))
                    .thenThrow(new IllegalStateException("Toolkit not initialized"));

            assertThatCode(() -> handler.handle(new IllegalStateException("too early")))
                    .doesNotThrowAnyException();
        }
        assertThat(output).contains("too early", "Cannot schedule the error dialog");

        handler.handle(new IllegalStateException("later"));
        Fx.flush();
        verify(dialogs).unexpectedError();
    }

    @Test
    void whileClosingItOnlyLogs(CapturedOutput output) throws Exception {
        handler.onApplicationEvent(new ContextClosedEvent(new GenericApplicationContext()));

        handler.handle(new IllegalStateException("on the way out"));
        Fx.flush();

        verifyNoInteractions(dialogs);
        assertThat(output).contains("on the way out");
    }
}
