plugins {
    id("twister.java-conventions")
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
