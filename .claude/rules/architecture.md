---
paths:
  - "**/*.java"
---

# Architecture: extend without rewriting

ADR 0004 (MVP scope) and ADR 0005. Reference code: `tw-docs/artifacts/2026-11-21-mvp-architecture/index.html`.

- Dependencies point only towards the domain: `tw-app-desktop` -> `tw-workflow` -> `tw-core`; `tw-persistence-api` -> `tw-workflow`. The desktop app depends on persistence only as `runtimeOnly`.
- `tw-core` has no Spring, JPA, JavaFX or Lombok imports.
- Ports in `tw-workflow` are plain interfaces with domain types only; no `Page`, `Pageable`, entities or JavaFX types in their signatures.
- Packages by feature (`order`, `customer`, `vehicle`, `stock`, `billing`, `access`, `shared`), same names in every module.
- One use case = one command record + one handler (`@UseCase`). New features add handlers; do not grow existing ones.
- Side effects after a state change go into `@TransactionalEventListener(phase = AFTER_COMMIT)` listeners of domain events.
- Variable behaviour goes behind a port with implementations chosen by configuration; never branch on the concrete implementation.
- Order, work item, state machine, stock and billing code never import `..vehicle..` (ADR 0005).
- Each module wires its beans through its own auto-configuration (`META-INF/spring/...AutoConfiguration.imports`), without `@ComponentScan` in auto-configurations.
- Screens and navigation items are beans collected by the shell; do not hard-code the menu.
- If a change seems to need editing many existing classes to add one feature, stop and add a `// TODO(discuss)` with the reason.
