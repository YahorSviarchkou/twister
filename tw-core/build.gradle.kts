plugins {
    id("java")
    alias(libs.plugins.spotless.plugin)
}

group = "com.twister"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

spotless {
    java {
        target("src/*/java/**/*.java")
        targetExclude("**/build/generated/**")
        palantirJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.named("spotlessApply") {
    group = "verification"
    description = "Formats code and moves the task to the Verification group."
}

tasks.check{
    dependsOn(tasks.spotlessCheck)
}