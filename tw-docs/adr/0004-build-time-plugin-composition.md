# 0004. Build-time plugin composition

- **Status:** Accepted (target architecture; MVP scope below)
- **Date:** 2026-11-21
- **Deciders:** project owner (accepted 2026-11-22)

## Context

The owner wants to assemble both a desktop and a web app from plugins with different adapters. The current modules have no plugin mechanism; `tw-fx-plugin-api` is empty and tied to JavaFX by name. Details: [extension architecture](../notes/2026-11-15-extension-architecture.md).

## Decision

We will compose applications at build time:

- `tw-api` holds the domain, ports, domain events and extension-point interfaces, in plain Java without Spring types;
- `tw-kernel` holds use cases, state machines, the extension registry and the event bus;
- adapters (`tw-store-jpa`, `tw-store-remote`), shells (`tw-shell-javafx`, `tw-shell-web`, `tw-rest`) and plugins depend only on `tw-api`;
- an application is an assembly module that picks the kernel, adapters, a shell and plugins; plugins register through Spring Boot auto-configuration and are switched by properties;
- plugins contribute behaviour through domain events and `TransitionGuard`, integrations through provider interfaces, and UI through declarative contributions rendered by each shell.

Runtime loading of plugin jars (PF4J) is out of scope until third parties write plugins.

## MVP scope (accepted 2026-11-22)

Until phase 2 the build keeps five modules: `tw-core`, `tw-workflow`, `tw-persistence-api`, `tw-fx-javafx-starter`, `tw-app-desktop`. There is no `tw-api` module, no plugin modules, no UI slots and no module renames; ports live in `tw-workflow`. The full layout above is the target, reached when a second assembly or the first plugin appears.

Condition of acceptance: adding a feature later must not require rewriting existing modules. The MVP code therefore follows these rules (also in `.claude/rules/architecture.md`):

- Dependencies point only towards the domain. Gradle module boundaries and ArchUnit tests enforce this.
- Ports are plain Java interfaces with domain types only (no Spring Data, JPA or JavaFX types), so moving them to `tw-api` later is a package move.
- Packages are split by feature (`order`, `customer`, `vehicle`, `stock`, `billing`, `access`) with the same names in every module, so a feature can be extracted into its own module by moving packages.
- One use case is one handler class. A new feature adds handlers; it does not change existing ones.
- Side effects of a state change are listeners of domain events (after commit), not code added to an existing use case.
- Behaviour that varies (documents, notifications, fiscal registers, approval policy) sits behind a port with implementations chosen by configuration; the core never checks which implementation it has.
- Each module registers its beans through its own Spring Boot auto-configuration; the assembly chooses features by its dependencies.
- Navigation items and screens are beans collected by the shell, not a hard-coded menu, so they become the `nav.item` contribution later.

## Options considered

- **Runtime plugins now (PF4J)** — flexible, but class-loader isolation and version compatibility cost a lot before the core works.
- **No plugin structure** — simplest, but desktop and web would duplicate wiring.
- **Build-time composition** — proposed.

## Consequences

- Module renames (deferred until the target layout, see MVP scope): `tw-core` → base of `tw-api`, `tw-workflow` → `tw-kernel`, `tw-persistence-api` → `tw-store-jpa`, `tw-fx-javafx-starter` → `tw-shell-javafx`.
- DAO ports lose `Page`/`Pageable`; `tw-kernel` stops depending on `spring-boot-starter-data-jpa`.
- An ArchUnit test enforces that plugins depend only on `tw-api`.
