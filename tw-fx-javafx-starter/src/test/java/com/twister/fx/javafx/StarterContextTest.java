package com.twister.fx.javafx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.acme.app.CachedView;
import com.acme.app.Greeter;
import com.acme.app.MainView;
import com.acme.app.NoControllerView;
import com.acme.app.NoFxmlView;
import com.acme.app.TestApp;
import com.twister.fx.javafx.stray.StrayComponent;
import com.twister.fx.javafx.testing.Fx;
import com.twister.fx.javafx.view.View;
import com.twister.fx.javafx.view.ViewLoadException;
import com.twister.fx.javafx.view.ViewLoader;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

class StarterContextTest {

    @BeforeAll
    static void startToolkit() throws Exception {
        Fx.start();
    }

    // Fails without the imports file, without @DefaultValue on the nested records and with setController.
    @Test
    void startsWithNoPropertiesAndLoadsAScreenWithANestedInclude() throws Exception {
        try (var context = TestApp.start()) {
            MainView main = Fx.call(
                    () -> context.getBean(ViewLoader.class).load(MainView.class).controller());

            assertThat(main.title()).isEqualTo("Orders");
            assertThat(main.detail().text()).isEqualTo("Parts");
            assertThat(main.detail().greeter()).isSameAs(context.getBean(Greeter.class));
        }
    }

    @Test
    void twoLoadsOfANonCachedViewGiveTwoControllers() throws Exception {
        try (var context = TestApp.start("--twister.javafx.cache.enabled=true")) {
            var views = context.getBean(ViewLoader.class);
            View<MainView> first = Fx.call(() -> views.load(MainView.class));
            View<MainView> second = Fx.call(() -> views.load(MainView.class));

            assertThat(second.controller()).isNotSameAs(first.controller());
            assertThat(second.controller().detail())
                    .isNotSameAs(first.controller().detail());
            assertThat(second.root()).isNotSameAs(first.root());
        }
    }

    @Test
    void cacheEnabledAloneCachesRootAndController() throws Exception {
        try (var context = TestApp.start("--twister.javafx.cache.enabled=true")) {
            var views = context.getBean(ViewLoader.class);
            View<CachedView> first = Fx.call(() -> views.load(CachedView.class));
            View<CachedView> second = Fx.call(() -> views.load(CachedView.class));

            assertThat(second.root()).isSameAs(first.root());
            assertThat(second.controller()).isNotNull().isSameAs(first.controller());
        }
    }

    @Test
    void nothingIsCachedUntilTheCacheIsEnabled() throws Exception {
        try (var context = TestApp.start()) {
            var views = context.getBean(ViewLoader.class);

            assertThat(Fx.call(() -> views.load(CachedView.class)).controller())
                    .isNotSameAs(Fx.call(() -> views.load(CachedView.class)).controller());
        }
    }

    @Test
    void loadsOffTheFxThreadAndShowsOnIt() throws Exception {
        try (var context = TestApp.start();
                var background = Executors.newVirtualThreadPerTaskExecutor()) {
            View<MainView> view = background
                    .submit(() -> {
                        assertThat(Platform.isFxApplicationThread()).isFalse();
                        return context.getBean(ViewLoader.class).load(MainView.class);
                    })
                    .get(Fx.TIMEOUT_SECONDS, TimeUnit.SECONDS);

            Fx.run(() -> {
                var stage = new Stage();
                stage.setScene(new Scene(view.root(), 300, 200));
                stage.show();
                assertThat(view.controller().title()).isEqualTo("Orders");
                stage.hide();
            });
        }
    }

    @Test
    void saysWhatIsWrongWithAView() {
        try (var context = TestApp.start()) {
            var views = context.getBean(ViewLoader.class);

            assertThatThrownBy(() -> views.load(NoFxmlView.class))
                    .isInstanceOf(ViewLoadException.class)
                    .hasMessageContaining("NoFxmlView.fxml");
            assertThatThrownBy(() -> views.load(NoControllerView.class))
                    .isInstanceOf(ViewLoadException.class)
                    .hasMessageContaining("fx:controller");
            assertThatThrownBy(() -> views.load(Greeter.class))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("@FxView");
        }
    }

    @Test
    void messageSourceIsTheOneOfBoot() {
        try (var context = TestApp.start()) {
            assertThat(context.getBeanFactory()
                            .getBeanDefinition("messageSource")
                            .getFactoryBeanName())
                    .isEqualTo(MessageSourceAutoConfiguration.class.getName());
        }
    }

    @Test
    void doesNotScanItsPackage() {
        runner().run(context -> assertThat(context).doesNotHaveBean(StrayComponent.class));
    }

    @TestFactory
    Stream<DynamicTest> everyBeanBacksOffForTheOneOfTheApplication() {
        return Arrays.stream(JavaFxAutoConfiguration.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(method -> DynamicTest.dynamicTest(method.getName(), () -> backsOff(method.getReturnType())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"UIColumn", "Editable", "FXController"})
    void oldAnnotationsAreGone(String name) {
        assertThatThrownBy(() -> Class.forName("com.twister.fx.javafx.annotation." + name))
                .isInstanceOf(ClassNotFoundException.class);
    }

    private static <T> void backsOff(Class<T> type) {
        T own = mock(type);
        runner().withBean("applicationsOwn", type, () -> own).run(context -> {
            assertThat(context).hasSingleBean(type);
            assertThat(context.getBean(type)).isSameAs(own);
        });
    }

    private static ApplicationContextRunner runner() {
        return new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JavaFxAutoConfiguration.class));
    }
}
