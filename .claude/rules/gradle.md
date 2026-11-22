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
- The root project still builds the legacy app; do not add new code to it.
