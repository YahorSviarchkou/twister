plugins {
    id("twister.java-conventions")
    id("io.spring.dependency-management")
}

val catalog = the<VersionCatalogsExtension>().named("libs")

dependencyManagement {
    imports {
        val bootVersion = catalog.findVersion("spring-boot").get().requiredVersion
        mavenBom("org.springframework.boot:spring-boot-dependencies:$bootVersion")
    }
}

// The Boot plugin sets both, but libraries do not apply it. Spring binds by parameter names.
// TODO(discuss): tw-core gets neither, so its source encoding depends on the machine. Set UTF-8 for all modules?
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
