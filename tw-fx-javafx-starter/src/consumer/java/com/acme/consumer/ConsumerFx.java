package com.acme.consumer;

import com.twister.fx.javafx.SpringFxApplication;

public class ConsumerFx extends SpringFxApplication {

    @Override
    protected Class<?> springApplication() {
        return ConsumerApplication.class;
    }
}
