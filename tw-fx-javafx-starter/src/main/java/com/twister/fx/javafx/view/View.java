package com.twister.fx.javafx.view;

import javafx.scene.Parent;

/** A loaded screen: its root node and the controller to pass parameters to. */
public record View<C>(Parent root, C controller) {}
