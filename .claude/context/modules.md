# Modules

State as of 2026-11-22 (see `../../tw-docs/notes/2026-11-15-codebase-analysis.md` for details).

| Module | Depends on | State |
|---|---|---|
| `tw-core` | — | Domain model: Customer, Transport, Spare, ServiceType, references, RepairOrder → RepairTask → RepairTaskItem, RepairInvoice, status histories. No behaviour. |
| `tw-workflow` | `tw-core` | Services, generic state-machine engine, `WorkflowOrchestrator` cascades, DAO port interfaces under `com.twister.workflow.dao`. 138 tests. |
| `tw-persistence-api` | `tw-core` | JPA entities, Spring Data repositories, specifications, MapStruct mappers (incomplete). No DAO adapters. Auto-configuration file is in `resources/spring/` instead of `META-INF/spring/`. |
| `tw-fx-javafx-starter` | — | Spring Boot starter: `ScreenManager`, `@FXController`, i18n bridge, Caffeine screen cache. Not registered as auto-configuration (#8). |
| `tw-app-desktop` | core, workflow, persistence | Main class and a stub `CustomerDaoImpl`. Does not start. |
| `tw-app-web`, `tw-fx-plugin-api`, `tw-fx-plugin-excel`, `tw-fx-plugin-web-sync` | — | Empty. |
| root `../../src` | — | Legacy monolith (JavaFX 17, SQLite, Flyway). Frozen. Packaged by CI and the Inno Setup installer. |

Target layout after the MVP (ADR 0004, accepted): `tw-api`, `tw-kernel`, `tw-store-jpa`, `tw-store-remote`, `tw-rest`, `tw-shell-javafx`, `tw-shell-web`, `tw-plugin-*`, assemblies `app-*`.
