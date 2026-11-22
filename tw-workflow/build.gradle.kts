plugins {
    id("twister.spring-conventions")
}

dependencies {
    implementation(project(":tw-core"))
    implementation(libs.bundles.spring.jpa)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.bundles.testing)
}
