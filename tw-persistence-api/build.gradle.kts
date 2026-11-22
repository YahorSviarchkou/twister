plugins {
    id("twister.spring-conventions")
}

dependencies {
    implementation(project(":tw-core"))
    implementation(libs.bundles.spring.jpa)
    implementation(libs.mapstruct)
    runtimeOnly(libs.spring.boot.flyway)
    runtimeOnly(libs.sqlite.jdbc)
    runtimeOnly(libs.hibernate.community.dialects)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstruct.processor)

    testRuntimeOnly(libs.postgresql)
    testImplementation(libs.spring.boot.flyway)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.mapstruct.processor)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.spring.boot.data.jpa.test)
    testImplementation(libs.bundles.testcontainers)
}

// The migration test loads the native library of SQLite.
tasks.test {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
