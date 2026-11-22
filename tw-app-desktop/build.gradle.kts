plugins {
    id("twister.spring-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":tw-core"))
    implementation(project(":tw-workflow"))
    implementation(project(":tw-persistence-api"))
//    implementation(project(":tw-fx-javafx-starter"))

    implementation(libs.spring.boot.data.jpa)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}
