plugins {
    id("twister.spring-conventions")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.javafx.plugin)
}

dependencies {
    implementation(project(":tw-fx-javafx-starter"))
    implementation(project(":tw-workflow"))
    runtimeOnly(project(":tw-persistence-api")) // screens cannot import JPA classes, ADR 0004

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.archunit)
}

// Required by the starter: the plugin selects the JavaFX jars of the platform.
javafx {
    version = libs.versions.javafx.asProvider().get()
    modules("javafx.controls", "javafx.fxml")
}

// TODO(discuss): on the classpath JavaFX prints "Unsupported JavaFX configuration" at every start, as a Boot jar
// cannot have a module path. Put JavaFX into the runtime image when the installer is built?
springBoot {
    mainClass = "com.twister.desktop.Launcher"
}

// JavaFX and SQLite load native libraries, which Java 25 warns about unless allowed.
tasks.bootJar {
    manifest.attributes("Enable-Native-Access" to "ALL-UNNAMED")
}

tasks.bootRun {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

// The store is read at test time; without this a deleted or edited store can leave `test` UP-TO-DATE.
tasks.test {
    inputs.files(fileTree("archunit_store")).withPropertyName("archunitStore").withPathSensitivity(PathSensitivity.RELATIVE)
}
