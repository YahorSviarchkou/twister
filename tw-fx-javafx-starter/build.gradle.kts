plugins {
    id("twister.spring-conventions")
    alias(libs.plugins.javafx.plugin)
}

dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.bundles.cache)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

javafx {
    version = libs.versions.java.get()
    modules("javafx.controls", "javafx.fxml", "javafx.base", "javafx.graphics")
    configurations = arrayOf("compileOnly", "implementation")
}
