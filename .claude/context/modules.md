# Modules

State as of 2026-11-15 (see `../../tw-docs/notes/2026-11-15-codebase-analysis.md` for details).

| Module | Depends on | State |
|---|---|---|
| `tw-core` | — | Domain model: Customer, Transport, Spare, ServiceType, references, RepairOrder → RepairTask → RepairTaskItem, RepairInvoice, status histories. No behaviour. |
| `tw-workflow` | `tw-core` | Services, generic state-machine engine, `WorkflowOrchestrator` cascades, DAO port interfaces under `com.twister.workflow.dao`. 138 tests. |
| `tw-persistence-api` | `tw-core` | JPA entities, Spring Data repositories, specifications, MapStruct mappers (incomplete). No DAO adapters. Registers itself as auto-configuration. |
| `tw-fx-javafx-starter` | — | Spring Boot starter: `ScreenManager`, `@FXController`, i18n bridge, Caffeine screen cache. Not registered as auto-configuration. Uncommitted. |
| `tw-app-desktop` | core, workflow, persistence | Main class and a stub `CustomerDaoImpl`. Does not start. Uncommitted. |
| `tw-app-web`, `tw-fx-plugin-api`, `tw-fx-plugin-excel`, `tw-fx-plugin-web-sync` | — | Empty. |
| root `../../src` | — | Legacy monolith (JavaFX 17, SQLite, Flyway). Frozen. Packaged by CI and the Inno Setup installer. |

Proposed target layout (ADR 0004, not decided yet): `tw-api`, `tw-kernel`, `tw-store-jpa`, `tw-store-remote`, `tw-rest`, `tw-shell-javafx`, `tw-shell-web`, `tw-plugin-*`, assemblies `app-*`.
