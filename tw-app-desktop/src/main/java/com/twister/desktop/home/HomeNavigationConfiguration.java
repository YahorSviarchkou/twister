package com.twister.desktop.home;

import com.twister.desktop.shell.NavigationItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration(proxyBeanMethods = false)
class HomeNavigationConfiguration {

    @Bean
    @Order(0)
    NavigationItem homeNavigationItem() {
        return new NavigationItem("navigation.home", HomeView.class);
    }
}
