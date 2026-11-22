---
paths:
  - "**/*.gradle.kts"
  - "gradle/**"
  - "settings.gradle.kts"
  - "gradle.properties"
  - "build-logic/**"
---

# Gradle (draft)

- Versions come from `../../gradle/libs.versions.toml`; do not hard-code versions in module build files or `build-logic`. Exceptions: settings plugins (foojay in `settings.gradle.kts`), which cannot read the catalog, and the product `group` and `version` in `gradle.properties`.
- Shared build settings live in the included build `build-logic/`. Module build files apply a convention and keep only their own dependencies and plugins:
  - `twister.java-conventions`: java-library, Java toolchain, repositories, sources jar, tests, Jacoco gate, Spotless;
  - `twister.spring-conventions`: the same plus the Spring Boot BOM, `-parameters` and UTF-8 compilation.
- Only `tw-app-desktop` applies `org.springframework.boot`; libraries get Spring versions from `twister.spring-conventions`.
- Modules that expose types of another module to consumers use `api(...)`.
- The legacy app lives in `legacy/` with its own `settings.gradle.kts` (build: `gradlew -p legacy build`) and is not part of the main build; do not add new code to it. The root build script stays empty: no shared settings, no plugins with versions, no dependencies.
