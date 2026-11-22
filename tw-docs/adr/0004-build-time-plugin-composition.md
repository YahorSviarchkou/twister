# 0004. Build-time plugin composition

- **Status:** Proposed
- **Date:** 2026-11-21
- **Deciders:** project owner (pending)

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

## Options considered

- **Runtime plugins now (PF4J)** — flexible, but class-loader isolation and version compatibility cost a lot before the core works.
- **No plugin structure** — simplest, but desktop and web would duplicate wiring.
- **Build-time composition** — proposed.

## Consequences

- Module renames: `tw-core` → base of `tw-api`, `tw-workflow` → `tw-kernel`, `tw-persistence-api` → `tw-store-jpa`, `tw-fx-javafx-starter` → `tw-shell-javafx`.
- DAO ports lose `Page`/`Pageable`; `tw-kernel` stops depending on `spring-boot-starter-data-jpa`.
- An ArchUnit test enforces that plugins depend only on `tw-api`.
