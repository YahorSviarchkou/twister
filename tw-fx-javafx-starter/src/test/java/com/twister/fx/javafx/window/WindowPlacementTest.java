package com.twister.fx.javafx.window;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javafx.geometry.Rectangle2D;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class WindowPlacementTest {

    private static final List<Rectangle2D> SCREENS =
            List.of(new Rectangle2D(0, 0, 1920, 1040), new Rectangle2D(1920, 0, 1280, 984));

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "fits,                        100,  100, 800, 600,   100, 100, 800, 600",
        "on the second screen,       2000,   50, 800, 600,  2000,  50, 800, 600",
        "snapped with hidden border,   -7,    0, 974, 1040,   -7,   0, 974, 1040",
        "its screen was unplugged,   5000,  100, 800, 600,   560, 220, 800, 600",
        "left of every screen,      -3000,  100, 800, 600,   560, 220, 800, 600",
        "a sliver left to grab,      -750,  100, 800, 600,   560, 220, 800, 600",
        "title bar above the top,     100,  -40, 800, 600,   560, 220, 800, 600",
        "below the bottom,            100, 1035, 800, 600,   560, 220, 800, 600",
        "minimized on Windows,     -32000,-32000, 800, 600,   560, 220, 800, 600",
        "bigger than its screen,     2000,    0, 1600, 1100, 1920,   0, 1280, 984",
        "lost and too big,           9000, 9000, 3000, 2000,    0,   0, 1920, 1040"
    })
    void keepsTheWindowReachable(
            String name,
            double x,
            double y,
            double width,
            double height,
            double expectedX,
            double expectedY,
            double expectedWidth,
            double expectedHeight) {
        assertThat(WindowPlacement.fit(new Rectangle2D(x, y, width, height), SCREENS))
                .as(name)
                .isEqualTo(new Rectangle2D(expectedX, expectedY, expectedWidth, expectedHeight));
    }
}
