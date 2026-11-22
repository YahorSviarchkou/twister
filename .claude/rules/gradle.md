---
paths:
  - "**/*.gradle.kts"
  - "gradle/**"
  - "settings.gradle.kts"
---

# Gradle (draft)

- Versions come from `../../gradle/libs.versions.toml`; do not hard-code versions in module build files.
- Modules that expose types of another module to consumers use `api(...)` with the `java-library` plugin.
- Java toolchain 21 in every module.
- The legacy app lives in `legacy/` with its own `settings.gradle.kts` (build: `gradlew -p legacy build`) and is not part of the main build; do not add new code to it. The root build script holds shared settings only: no plugins with versions, no dependencies.
