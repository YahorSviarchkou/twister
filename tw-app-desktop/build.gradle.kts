plugins {
    id("java")
    id("jacoco")
    alias(libs.plugins.spotless.plugin)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
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
    implementation(project(":tw-core"))
    implementation(project(":tw-workflow"))
    implementation(project(":tw-persistence-api"))
//    implementation(project(":tw-fx-javafx-starter"))
//    implementation(project(":tw-fx-plugin-api"))

    implementation(libs.spring.boot.data.jpa)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

tasks.test {
    useJUnitPlatform()
}