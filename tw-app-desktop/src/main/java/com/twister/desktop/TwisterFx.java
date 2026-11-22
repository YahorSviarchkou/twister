package com.twister.desktop;

import com.twister.fx.javafx.SpringFxApplication;

public class TwisterFx extends SpringFxApplication {

    @Override
    protected Class<?> springApplication() {
        return TwisterDesktopApplication.class;
    }
}
