pluginManagement {
    includeBuild("build-logic")
}

plugins {
    // Downloads missing JDK toolchains. The version catalog cannot be used in settings plugins.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "twister"

include("tw-core")
include("tw-workflow")
include("tw-persistence-api")
include("tw-app-desktop")
include("tw-fx-javafx-starter")

// Parked until their phase (ADR 0004), folders kept:
// tw-app-web, tw-fx-plugin-api, tw-fx-plugin-excel, tw-fx-plugin-web-sync
