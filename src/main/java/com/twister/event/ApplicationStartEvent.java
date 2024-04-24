package com.twister.event;

import org.springframework.context.event.ApplicationContextEvent;

public interface ApplicationStartEvent {

    void onApplicationStartEvent();

    void handleApplicationContextReady(ApplicationContextEvent event);
}
