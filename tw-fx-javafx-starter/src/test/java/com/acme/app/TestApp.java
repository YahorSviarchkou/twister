package com.acme.app;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import({Greeter.class, MainView.class, DetailView.class, CachedView.class, NoControllerView.class, NoFxmlView.class})
public class TestApp {

    /** The way an application starts, so that environment post-processors run. */
    public static ConfigurableApplicationContext start(String... args) {
        return new SpringApplicationBuilder(TestApp.class)
                .web(WebApplicationType.NONE)
                .registerShutdownHook(false)
                .run(args);
    }
}
