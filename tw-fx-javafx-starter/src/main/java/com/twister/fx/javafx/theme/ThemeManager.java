package com.twister.fx.javafx.theme;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import com.twister.fx.javafx.concurrent.FxThread;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * The light and dark AtlantaFX themes as a bean. Delegates to AtlantaFX's manager, so that its theme options keep
 * working.
 */
public class ThemeManager {

    private final ReadOnlyObjectWrapper<ThemeMode> mode;

    public ThemeManager(ThemeMode initialMode) {
        mode = new ReadOnlyObjectWrapper<>(this, "mode", initialMode);
    }

    /** {@code SpringFxApplication} calls it before it publishes the stage. */
    public void apply() {
        apply(mode.get());
    }

    public void setMode(ThemeMode newMode) {
        apply(newMode);
        mode.set(newMode);
    }

    public ThemeMode getMode() {
        return mode.get();
    }

    public ReadOnlyObjectProperty<ThemeMode> modeProperty() {
        return mode.getReadOnlyProperty();
    }

    private static void apply(ThemeMode mode) {
        // AtlantaFX's manager sets a stylesheet when its class loads: off the FX thread that breaks it for good.
        FxThread.check();
        Theme theme = switch (mode) {
            case LIGHT -> new PrimerLight();
            case DARK -> new PrimerDark();
        };
        atlantafx.base.theme.ThemeManager.instance().setTheme(theme);
    }
}
