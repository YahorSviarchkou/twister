plugins {
    id("twister.spring-conventions")
    alias(libs.plugins.javafx.plugin)
}

// An application that sees only the starter: compiles against its API and runs in a child JVM of the tests.
val consumer: SourceSet = sourceSets.create("consumer")

dependencies {
    api(libs.spring.boot.starter)
    implementation(libs.atlantafx.base)
    implementation(libs.caffeine)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    "consumerImplementation"(project(path))

    testImplementation(libs.bundles.testing)
}

// Consumers apply the JavaFX plugin too: it selects the jars of the platform.
javafx {
    version = libs.versions.javafx.asProvider().get()
    modules("javafx.controls", "javafx.fxml", "javafx.base", "javafx.graphics")
    configurations = arrayOf("api")
}

// A real jar, as a language pack is: a second resources folder would hide classpath scanning problems.
val languagePackJar = tasks.register<Jar>("languagePackJar") {
    archiveClassifier = "test-language-pack"
    destinationDirectory = layout.buildDirectory.dir("test-libs")
    from("src/languagePack/resources")
}

dependencies {
    testRuntimeOnly(files(languagePackJar))
}

tasks.test {
    val consumerClasspath = consumer.runtimeClasspath + files(languagePackJar)
    inputs.files(consumerClasspath).withNormalizer(ClasspathNormalizer::class)
    jvmArgumentProviders.add { listOf("-Dconsumer.classpath=${consumerClasspath.asPath}") }

    // No windows on the screen and no writes to the profile of whoever runs the tests.
    val home = layout.buildDirectory.dir("test-home").get().asFile.absolutePath
    systemProperty("user.home", home)
    environment("LOCALAPPDATA", home)
    systemProperty("glass.platform", "Headless")
    systemProperty("prism.order", "sw")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
