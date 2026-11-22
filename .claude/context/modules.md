# Modules

State as of 2026-11-22 (see `../../tw-docs/notes/2026-11-15-codebase-analysis.md` for details).

| Module | Depends on | State |
|---|---|---|
| `tw-core` | — | Domain model: Customer, Transport, Spare, ServiceType, references, RepairOrder → RepairTask → RepairTaskItem, RepairInvoice, status histories. No behaviour. |
| `tw-workflow` | `tw-core` | Services, generic state-machine engine, `WorkflowOrchestrator` cascades, DAO port interfaces under `com.twister.workflow.dao`. 138 tests. |
| `tw-persistence-api` | `tw-core` | JPA entities, Spring Data repositories, specifications, MapStruct mappers (incomplete). No DAO adapters. SQLite driver and dialect, Flyway migrations in `db/migration` (so far only the `app_meta` baseline, no tables for the entities). Registers itself as auto-configuration. |
| `tw-fx-javafx-starter` | — | Spring Boot starter for JavaFX, knows nothing about Twister: `SpringFxApplication` + `StageReadyEvent`, `@FxView` + `ViewLoader` with an optional Caffeine cache, i18n over Boot's `MessageSource` (bundles and languages found on the classpath), `FxTasks`, `Navigator`, `Dialogs`, `Notifier`, `FxErrorHandler`, AtlantaFX `ThemeManager`, `WindowState`. Registered as auto-configuration. Consumers also apply the JavaFX Gradle plugin, which selects the platform jars. Tests run on the headless JavaFX platform. |
| `tw-app-desktop` | starter, workflow, persistence (`runtimeOnly`) | Starts through `Launcher`: shell window with a menu of `NavigationItem` beans and a placeholder screen, SQLite database, log and window state in `twister.data-dir`. Excludes `WorkflowConfiguration` until the DAO ports have adapters (#10). Holds the ArchUnit rules (`ArchitectureTest`, frozen violations in `archunit_store`). |
| `tw-app-web`, `tw-fx-plugin-api`, `tw-fx-plugin-excel`, `tw-fx-plugin-web-sync` | — | Empty and parked until their phase (ADR 0004): folders only, not included in `settings.gradle.kts`. |
| `../../build-logic` | — | Included build with the convention plugins `twister.java-conventions` (all five modules, directly or through the next one) and `twister.spring-conventions` (all but `tw-core`). |
| `../../legacy` | — | Legacy monolith (JavaFX 17, SQLite, Flyway), standalone build outside the main settings with its own Gradle 8.14.3 wrapper: `legacy/gradlew -p legacy build` on JDK 17 or 21. Frozen. Packaged by CI and the Inno Setup installer from `prod`, which keeps the old layout. |

Target layout after the MVP (ADR 0004, accepted): `tw-api`, `tw-kernel`, `tw-store-jpa`, `tw-store-remote`, `tw-rest`, `tw-shell-javafx`, `tw-shell-web`, `tw-plugin-*`, assemblies `app-*`.
