plugins {
    id("twister.spring-conventions")
}

// Boot's BOM would otherwise downgrade Testcontainers core below the catalog version.
extra["testcontainers.version"] = libs.versions.testcontainers.get()

dependencies {
    implementation(project(":tw-core"))
    implementation(libs.bundles.spring.jpa)
    implementation(libs.mapstruct)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstruct.processor)

    testRuntimeOnly(libs.postgresql)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.mapstruct.processor)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testcontainers)
}
