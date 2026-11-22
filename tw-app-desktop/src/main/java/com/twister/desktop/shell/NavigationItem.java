package com.twister.desktop.shell;

/**
 * An entry of the side menu. Features declare theirs as beans, {@code @Order} gives the position, the first one
 * opens at startup.
 *
 * @param titleKey message key of the text
 * @param screen the {@code @FxView} controller of the screen it opens
 */
public record NavigationItem(String titleKey, Class<?> screen) {}
