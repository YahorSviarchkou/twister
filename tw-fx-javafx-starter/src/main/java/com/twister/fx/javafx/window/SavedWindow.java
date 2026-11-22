package com.twister.fx.javafx.window;

import javafx.geometry.Rectangle2D;

/** @param bounds of the normal window, null if it was only seen maximized */
record SavedWindow(Rectangle2D bounds, boolean maximized) {}
