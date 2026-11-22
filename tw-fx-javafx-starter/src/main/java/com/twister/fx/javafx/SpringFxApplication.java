package com.twister.fx.javafx;

import com.twister.fx.javafx.error.FxErrorHandler;
import com.twister.fx.javafx.theme.ThemeManager;
import com.twister.fx.javafx.window.WindowState;
import java.util.List;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Runs Spring inside the JavaFX lifecycle: the context starts in {@link #init()}, {@link StageReadyEvent} is
 * published in {@link #start(Stage)} and the context is closed in {@link #stop()}.
 *
 * <p>The class with {@code main} must not extend {@link Application}, otherwise the JVM refuses to start it from
 * the classpath. It calls {@code Application.launch(subclass, args)}. Exit with {@code Platform.exit()}.
 */
// TODO(discuss): built-in texts of JavaFX controls (context menus, choosers, date picker) follow Locale.getDefault(),
// not twister.javafx.locale. Should init() set the JVM default locale to the selected one?
@Slf4j
public abstract class SpringFxApplication extends Application {

    private ConfigurableApplicationContext context;
    private Thread.UncaughtExceptionHandler previousHandler;

    /** The {@code @SpringBootApplication} class of the assembly. */
    protected abstract Class<?> springApplication();

    @Override
    public final void init() {
        context = new SpringApplicationBuilder(springApplication())
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(getParameters().getRaw().toArray(String[]::new));
    }

    @Override
    public final void start(Stage primaryStage) {
        previousHandler = Thread.getDefaultUncaughtExceptionHandler();
        try {
            Thread.setDefaultUncaughtExceptionHandler(context.getBean(FxErrorHandler.class));
            context.getBean(ThemeManager.class).apply();
            context.getBean(WindowState.class).attach(primaryStage);
            context.publishEvent(new StageReadyEvent(primaryStage));
        } catch (RuntimeException e) {
            // The launcher only prints it to the console, which an installed application does not have.
            log.error("The application failed to start", e);
            throw e;
        }
    }

    /** The launcher calls it on the FX thread when the last window closed, also when {@link #start} failed. */
    @Override
    public final void stop() {
        try (var ignored = context) {
            // Platform.exit() leaves windows open. Hidden here, they save their state while the beans still work,
            // and modal ones leave their event loops, which the toolkit's own exit breaks with an exception.
            List.copyOf(Window.getWindows()).reversed().forEach(Window::hide);
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(previousHandler);
        }
    }
}
