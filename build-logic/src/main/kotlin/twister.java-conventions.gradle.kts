import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-library`
    jacoco
    id("com.diffplug.spotless")
}

// The generated `libs` accessors are not available in precompiled script plugins.
val catalog = the<VersionCatalogsExtension>().named("libs")

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(catalog.findVersion("java").get().requiredVersion)
    }
    withSourcesJar()
}

repositories {
    // TODO(discuss): mavenLocal() comes first, so artifacts in ~/.m2 shadow Maven Central. Is it still needed?
    mavenLocal()
    mavenCentral()
}

// Not withType<Jar>(): bootJar must not downgrade duplicate entries to warnings.
listOf("jar", "sourcesJar").forEach { name ->
    tasks.named<Jar>(name) {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}

jacoco {
    toolVersion = catalog.findVersion("jacoco").get().requiredVersion
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.SHORT
    }

    finalizedBy(tasks.jacocoTestReport)
}

// Class-file patterns shared by the report and the gate. Only business logic is measured (.claude/rules/tests.md).
// Lombok output is skipped by `lombok.addLombokGeneratedAnnotation` in lombok.config.
val coverageExcludes = listOf(
    "**/*MapperImpl*",
    "**/entity/**",
    "**/*Configuration*",
    "**/*AutoConfiguration*",
    "**/*Properties*",
    "**/Launcher*",
    "**/*Application*",
    // `*View` and its nested classes, but not `*ViewModel`.
    "**/*View.class",
    "**/*View\$*.class",
)

val measuredClasses = provider {
    sourceSets.main.get().output.classesDirs.elements.get().map { fileTree(it) { exclude(coverageExcludes) } }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(measuredClasses)
    reports {
        xml.required = false
        csv.required = false
        html.required = true
    }
}

tasks.jacocoTestCoverageVerification {
    classDirectories.setFrom(measuredClasses)
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

tasks.check {
    dependsOn(tasks.named("spotlessCheck"))
}
