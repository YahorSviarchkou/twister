# Architecture

## Now

Layered Gradle modules: `tw-core` (domain) ← `tw-workflow` (services, state machines, DAO ports) and `tw-persistence-api` (JPA). The bridge between DAO ports and repositories does not exist yet. Applications are wired with Spring Boot auto-configuration (`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`).

## Direction (proposed, see ADRs)

- **Build-time plugins** (ADR 0004): plain-Java `tw-api` with ports, events and extension points; `tw-kernel` with use cases and the event bus; adapters, shells and plugins depend only on `tw-api`; desktop and web are different assemblies.
- **Two state machines** (ADR 0005): order and work item; `RepairTask` disappears.
- **Tablets over Wi-Fi** (ADR 0006): the desktop app embeds a LAN server (REST + WebSocket + mobile web UI); devices pair by QR code.

## Where to look

- Diagrams: `../../tw-docs/diagrams` (C4 levels 1–3, sequences, state machines, module graphs).
- Rationale: `../../tw-docs/notes/2026-11-15-extension-architecture.md`, `tw-docs/notes/2026-11-15-product-rethink.md`.
