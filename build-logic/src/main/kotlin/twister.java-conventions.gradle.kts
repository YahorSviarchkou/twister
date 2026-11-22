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

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = false
        csv.required = false
        html.required = true
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            excludes = listOf("**Mock**")
            limit {
                // TODO(discuss): this counts instructions (the Jacoco default), not lines. Is that intended?
                minimum = "0.8".toBigDecimal()
            }
        }
    }
}

spotless {
    java {
        target("src/*/java/**/*.java")
        targetExclude("**/build/generated/**")
        palantirJavaFormat(catalog.findVersion("palantir-java-format").get().requiredVersion)
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
    dependsOn(tasks.named("spotlessCheck"), tasks.jacocoTestCoverageVerification)
}
