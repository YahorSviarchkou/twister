plugins {
    id("twister.java-conventions")
    id("twister.coverage-gate")
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
