package com.twister.fx.javafx.window;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.fx.javafx.testing.Fx;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WindowStateTest {

    private static final Rectangle2D PLACE = new Rectangle2D(120, 130, 500, 320);

    @TempDir
    Path folder;

    @BeforeAll
    static void startToolkit() throws Exception {
        Fx.start();
    }

    @AfterEach
    void hideWindows() throws Exception {
        Fx.hideAllWindows();
    }

    @Test
    void comesBackWhereItWasClosed() throws Exception {
        Stage first = show(new WindowState(file()), "main");
        Fx.run(() -> {
            move(first, PLACE);
            first.hide();
        });

        Stage second = show(new WindowState(file()), "main");

        assertThat(bounds(second)).isEqualTo(PLACE);
    }

    @Test
    void comesBackMaximizedOverItsNormalBounds() throws Exception {
        Stage first = show(new WindowState(file()), "main");
        Fx.run(() -> move(first, PLACE));
        Fx.flush();
        // The order Windows reports maximizing in: bounds first, the flag after them.
        Fx.run(() -> {
            move(first, new Rectangle2D(0, 0, 1000, 1000));
            first.setMaximized(true);
        });
        Fx.flush();
        Fx.run(first::hide);

        assertThat(new WindowStateFile(file()).read("main")).contains(new SavedWindow(PLACE, true));
        Stage second = show(new WindowState(file()), "main");
        assertThat(Fx.call(second::isMaximized)).isTrue();
    }

    @Test
    void aMinimizedWindowKeepsTheBoundsItHadBefore() throws Exception {
        Stage stage = show(new WindowState(file()), "main");
        Fx.run(() -> move(stage, PLACE));
        Fx.flush();
        Fx.run(() -> {
            stage.setIconified(true);
            stage.hide();
        });

        assertThat(new WindowStateFile(file()).read("main")).contains(new SavedWindow(PLACE, false));
    }

    @Test
    void closingSavesAWindowThatIsStillOpen() throws Exception {
        var state = new WindowState(file());
        Stage stage = show(state, "main");
        Fx.run(() -> move(stage, PLACE));
        Fx.flush();

        state.close(); // off the FX thread, like the shutdown hook

        assertThat(new WindowStateFile(file()).read("main")).contains(new SavedWindow(PLACE, false));
    }

    @Test
    void aPositionThatIsNowOffScreenIsCentered() throws Exception {
        new WindowStateFile(file()).write("main", new SavedWindow(new Rectangle2D(5000, 4000, 500, 320), false));

        Stage stage = show(new WindowState(file()), "main");

        assertThat(bounds(stage)).isEqualTo(new Rectangle2D(250, 340, 500, 320));
    }

    @Test
    void windowsWithDifferentNamesDoNotShareState() throws Exception {
        var state = new WindowState(file());
        Stage main = show(state, "main");
        Stage other = show(state, "estimate");
        var otherPlace = new Rectangle2D(200, 210, 360, 260);
        Fx.run(() -> {
            move(main, PLACE);
            move(other, otherPlace);
            main.hide();
            other.hide();
        });

        assertThat(new WindowStateFile(file()).read("main")).contains(new SavedWindow(PLACE, false));
        assertThat(new WindowStateFile(file()).read("estimate")).contains(new SavedWindow(otherPlace, false));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "\u0000\u0001\u0002 not properties",
                "main.maximized=\\uZZZZ",
                "main.maximized=false\nmain.width=wide",
                "main.maximized=false\nmain.x=NaN\nmain.y=1\nmain.width=500\nmain.height=300",
                "main.maximized=false\nmain.x=1\nmain.y=1\nmain.width=-500\nmain.height=300",
                "main.maximized=false\nmain.width=500"
            })
    void aCorruptFileIsIgnoredAndThenReplaced(String content) throws Exception {
        Files.writeString(file(), content);

        Stage stage = show(new WindowState(file()), "main");
        Fx.run(() -> {
            move(stage, PLACE);
            stage.hide();
        });

        assertThat(new WindowStateFile(file()).read("main")).contains(new SavedWindow(PLACE, false));
    }

    @Test
    void aFileThatIsNotTextIsIgnored() throws Exception {
        Files.write(file(), new byte[] {(byte) 0xC3, (byte) 0x28, (byte) 0xFF, (byte) 0xFE});

        assertThat(new WindowStateFile(file()).read("main")).isEmpty();
    }

    @Test
    void aPlaceThatCannotBeWrittenDoesNotBreakClosing() throws Exception {
        Path notAFolder = Files.writeString(folder.resolve("taken"), "a file");
        Stage stage = show(new WindowState(notAFolder.resolve("state.properties")), "main");

        Fx.run(stage::hide);

        assertThat(Fx.call(stage::isShowing)).isFalse();
    }

    @Test
    void theDefaultFileStaysInsideTheProfile() {
        assertThat(WindowState.defaultFile("Shop", "C:/local", "C:/home"))
                .isEqualTo(Path.of("C:/local", "Shop", "window-state.properties"));
        assertThat(WindowState.defaultFile("Shop", null, "/home/me"))
                .isEqualTo(Path.of("/home/me", ".Shop", "window-state.properties"));
        assertThat(WindowState.defaultFile("../../evil", "C:/local", "C:/home").normalize())
                .startsWithRaw(Path.of("C:/local"));
        assertThat(WindowState.defaultFile("..", "C:/local", "C:/home"))
                .isEqualTo(Path.of("C:/local", "javafx-application", "window-state.properties"));
    }

    private Path file() {
        return folder.resolve("window-state.properties");
    }

    private static Stage show(WindowState state, String name) throws Exception {
        Stage stage = Fx.call(() -> {
            var created = new Stage();
            state.attach(created, name);
            created.setScene(new Scene(new Pane(), 300, 200));
            created.show();
            return created;
        });
        Fx.flush();
        return stage;
    }

    private static void move(Stage stage, Rectangle2D to) {
        stage.setX(to.getMinX());
        stage.setY(to.getMinY());
        stage.setWidth(to.getWidth());
        stage.setHeight(to.getHeight());
    }

    private static Rectangle2D bounds(Stage stage) throws Exception {
        return Fx.call(() -> new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()));
    }
}
