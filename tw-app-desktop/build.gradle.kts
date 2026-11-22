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
    testImplementation(libs.bundles.testing)
    testImplementation(libs.archunit)
    // TODO(tw-18): drop this once the starter is a regular dependency. It lets the architecture test see all modules.
    testImplementation(project(":tw-fx-javafx-starter"))
}

// The store is read at test time; without this a deleted or edited store can leave `test` UP-TO-DATE.
tasks.test {
    inputs.files(fileTree("archunit_store")).withPropertyName("archunitStore").withPathSensitivity(PathSensitivity.RELATIVE)
}
