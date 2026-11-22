# Architecture

## Now

Layered Gradle modules: `tw-core` (domain) ← `tw-workflow` (services, state machines, DAO ports) and `tw-persistence-api` (JPA). The bridge between DAO ports and repositories does not exist yet. Applications are wired with Spring Boot auto-configuration (`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`).

## Direction (see ADR statuses)

- **Build-time plugins** (ADR 0004, accepted as the target; MVP keeps five modules and follows `.claude/rules/architecture.md`): plain-Java `tw-api` with ports, events and extension points; `tw-kernel` with use cases and the event bus; adapters, shells and plugins depend only on `tw-api`; desktop and web are different assemblies.
- **Two state machines** (ADR 0005, accepted): order and work item; `RepairTask` disappears; vehicle-specific code stays behind the `vehicle` package boundary.
- **Embedded database** (ADR 0009, accepted): SQLite in WAL mode, Flyway, portable SQL, money as minor units, UUIDs as text, `VACUUM INTO` backups.
- **Tablets over Wi-Fi** (ADR 0006): the desktop app embeds a LAN server (REST + WebSocket + mobile web UI); devices pair by QR code.

## Where to look

- Diagrams: `../../tw-docs/diagrams` (C4 levels 1–3, sequences, state machines, module graphs).
- Rationale: `../../tw-docs/notes/2026-11-15-extension-architecture.md`, `tw-docs/notes/2026-11-15-product-rethink.md`.
