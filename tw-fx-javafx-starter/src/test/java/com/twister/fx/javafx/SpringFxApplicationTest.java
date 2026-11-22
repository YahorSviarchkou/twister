package com.twister.fx.javafx;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Runs the application of {@code src/consumer} in a JVM of its own: only the starter on the classpath, and a JVM
 * can launch JavaFX once.
 */
class SpringFxApplicationTest {

    private static final long PROCESS_TIMEOUT_SECONDS = 90;
    // On the FX thread: the shutdown hook would close it too, and hide a stop() that does not.
    private static final String CLOSED_BY_STOP = "CONTEXT CLOSED on fx=true";

    @TempDir
    Path profile;

    private record Run(int exitCode, String output) {}

    @Test
    void withOnlyTheStarterAndNoPropertiesItLoadsAScreenAndClosesCleanly() throws Exception {
        Run run = launch();

        assertThat(run.exitCode()).as(run.output()).isZero();
        assertThat(run.output())
                .contains(
                        "SCREEN Workbench/Details",
                        "NEW CONTROLLER true",
                        "LANGUAGES [en, fil, ka, ru]",
                        "THEME LIGHT /atlantafx/base/theme/primer-light.css",
                        "TASK work on fx=false callback on fx=true",
                        "WINDOWS 2",
                        CLOSED_BY_STOP,
                        "LAUNCH RETURNED")
                .doesNotContain(" ERROR ", " WARN ", "Exception");
        assertThat(profile.resolve("javafx-application/window-state.properties"))
                .exists();
    }

    @Test
    void theWindowComesBackInTheNextRun() throws Exception {
        assertThat(launch("move").exitCode()).isZero();

        Run next = launch();

        assertThat(next.exitCode()).as(next.output()).isZero();
        assertThat(next.output()).contains("BOUNDS 120,130,500,320");
    }

    @Test
    void platformExitClosesTheContextAndSavesTheWindow() throws Exception {
        Run exit = launch("exit");

        assertThat(exit.exitCode()).as(exit.output()).isZero();
        assertThat(exit.output()).contains(CLOSED_BY_STOP).doesNotContain(" ERROR ", "Exception");
        assertThat(launch().output()).contains("BOUNDS 120,130,500,320");
    }

    @Test
    void platformExitWithAnOpenDialogStillEndsTheProcess() throws Exception {
        Run run = launch("exit-with-dialog");

        assertThat(run.exitCode()).as(run.output()).isZero();
        assertThat(run.output()).contains(CLOSED_BY_STOP).doesNotContain(" ERROR ", "Exception");
    }

    @Test
    void closingTheWindowUnderADialogEndsTheProcess() throws Exception {
        Run run = launch("close-with-dialog");

        assertThat(run.exitCode()).as(run.output()).isZero();
        assertThat(run.output()).contains("CONFIRMED false", CLOSED_BY_STOP).doesNotContain(" ERROR ", "Exception");
    }

    @Test
    void anUncaughtErrorBecomesADialogAndALogEntry() throws Exception {
        Run run = launch("error");

        assertThat(run.exitCode()).as(run.output()).isZero();
        assertThat(run.output()).contains("nobody catches this", "DIALOG Something went wrong", CLOSED_BY_STOP);
    }

    @Test
    void aHungTaskDoesNotKeepTheProcessAlive() throws Exception {
        Run run = launch("hung-task", "--twister.javafx.tasks.shutdown-timeout=10ms");

        assertThat(run.exitCode()).as(run.output()).isZero();
        assertThat(run.output()).contains("interrupting them", CLOSED_BY_STOP, "LAUNCH RETURNED");
    }

    @Test
    void aFailedStartIsLoggedAndClosesTheContext() throws Exception {
        Run run = launch("fail");

        assertThat(run.exitCode()).as(run.output()).isNotZero();
        assertThat(run.output()).contains("The application failed to start", "shell failed", CLOSED_BY_STOP);
    }

    private Run launch(String... args) throws IOException, InterruptedException {
        var command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-Dglass.platform=Headless",
                "-Dprism.order=sw",
                "-Duser.home=" + profile,
                "-Djava.io.tmpdir=" + profile,
                "-Dfile.encoding=UTF-8",
                "-Dstdout.encoding=UTF-8",
                "-Dstderr.encoding=UTF-8",
                "--enable-native-access=ALL-UNNAMED",
                "com.acme.consumer.Launcher"));
        command.addAll(List.of(args));

        File output = Files.createTempFile(profile, "run", ".log").toFile();
        var builder = new ProcessBuilder(command)
                .directory(profile.toFile())
                .redirectErrorStream(true)
                .redirectOutput(output);
        var environment = builder.environment();
        environment.put("CLASSPATH", System.getProperty("consumer.classpath")); // too long for a command line
        environment.put("LOCALAPPDATA", profile.toString());
        // "No properties" must not depend on the machine.
        environment.keySet().removeIf(name -> name.startsWith("SPRING_") || name.startsWith("TWISTER_"));

        Process process = builder.start();
        if (!process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly().waitFor();
            throw new AssertionError("The process did not end:\n" + Files.readString(output.toPath()));
        }
        return new Run(process.exitValue(), Files.readString(output.toPath(), StandardCharsets.UTF_8));
    }
}
