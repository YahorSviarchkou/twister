# Codebase analysis, 2026-11-15

A snapshot of the repository on branch `exp` (last commit `caef6bb`, 2026-03-28), made by reading every module. The interactive version with diagrams and a filterable issue list is the [project map artifact](../artifacts/2026-11-15-project-map/index.html) (in Russian).

## Summary

The most mature part is `tw-workflow`: state machines for order, task and work item, services, and 138 tests. The domain model in `tw-core` and the JPA layer in `tw-persistence-api` are mostly in place. The layer between them is missing: none of the 11 DAO ports declared by `tw-workflow` has an implementation, and the only `CustomerDaoImpl` returns `null` everywhere.

The new desktop app does not start, there is no plugin mechanism, and CI still builds only the legacy monolith with JDK 17. About 64 files (the JavaFX starter, all mappers, the desktop app) exist only in the uncommitted working copy.

## Modules

| Module | Done (estimate) | State |
|---|---|---|
| `tw-core` | 60% | Domain model without Spring. Missing: diagnostic report, payment, spare quantity per work item, invoice total and status. |
| `tw-workflow` | 45% | Solid transition engine with history. 3 of 5 orchestrator cascades fail on real data. Policies and domain events from the readme are not started. |
| `tw-persistence-api` | 40% | Entities and specifications done. Mappers incomplete (5 empty, cycles, broken `workflowItemId`). Auto-configuration file in the wrong folder. No migrations; database not chosen. |
| `tw-fx-javafx-starter` | 30% | FXML loading through Spring beans, i18n bridge, screen cache. Not registered as auto-configuration; possible NPE at startup. Uncommitted. |
| `tw-app-desktop` | 3% | Main class and a stub DAO. Cannot start: no datasource, driver, migrations or DAO beans. |
| `tw-fx-plugin-api`, `-excel`, `-web-sync`, `tw-app-web` | 0% | Build files only. |
| legacy `src/` | frozen | JavaFX 17 + SQLite + Flyway. Customer, spare and service CRUD work; editing a customer is broken; orders are read-only stubs. The only thing packaged by the installer. |
| build / CI | 25% | Version catalog, Spotless and Jacoco exist but are copied per module. CI uses JDK 17 while modules need a Java 21 toolchain. |

## Cascade bugs in `WorkflowOrchestrator`

1. **Rejecting an order in `OPEN` always fails.** The task is created only on `ACCEPTED`, so the cancel cascade throws `NoSuchElementException` and rolls back.
2. **Cancelling a task fails** when some items are already `READY` or `CANCELED`: they have no `CANCEL` transition.
3. **The task never finishes automatically.** `findAllTaskItemsStatusesByTaskId` returns the whole history, which always contains `CREATED`; cancelled items are not excluded either.
4. **No invoice is created.** `GENERATE_INVOICE` moves the order to `WAITING_PAYMENT`, but nothing calls `generateRepairInvoice`.
5. Cancelling a task directly leaves the order in `ACCEPTED`.

## Blockers to a running desktop app

- `tw-persistence-api/src/main/resources/spring/...AutoConfiguration.imports` must move to `META-INF/spring/`.
- `tw-fx-javafx-starter` has no `AutoConfiguration.imports` at all.
- No DAO adapters, no datasource, no migrations for the new schema.
- CI: Temurin 17 against a Java 21 toolchain; `jpackage` points at the legacy jar.

## Repository risks

- About 64 files (+874 / −82 without line-ending noise) are uncommitted since April; two commits on `exp` are not pushed.
- No `.gitattributes`: about half of the diff is CRLF/LF churn.
- The last `check` run had 2 failing persistence tests and a red Jacoco gate.

## Related

- [Product rethink and phases](2026-11-15-product-rethink.md)
- [Current module graph](../diagrams/modules-current.mmd)
