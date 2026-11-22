# 0009. Use SQLite as the embedded database

- **Status:** Accepted
- **Date:** 2026-11-22
- **Deciders:** project owner

## Context

The MVP is a Windows desktop app for one workshop PC with a few users taking turns. In phase 2 the desktop app hosts a LAN server for tablets (ADR 0006), so the database file is still opened by one process only. Workshops have no administrator, so the database must need no installation, service or passwords, and backup must be simple. The legacy app already uses SQLite with Flyway. The new modules are tested against PostgreSQL in Testcontainers, but no database has been chosen. See the [MVP architecture reference](../artifacts/2026-11-21-mvp-architecture/index.html), section "Technologies".

## Decision

We will use SQLite in WAL mode as the embedded database, with the `xerial` JDBC driver, the SQLite dialect from `hibernate-community-dialects` and Flyway migrations written by hand (`ddl-auto: validate`).

- The database file lives in `%LOCALAPPDATA%\Twister` by default; the path can be changed in settings.
- Money is stored as integer minor units, because SQLite has no exact decimal type. UUIDs are stored as text. Every aggregate table has a `version` column for optimistic locking. Tables are `STRICT`.
- Online backup with `VACUUM INTO` at startup, daily and on exit, keeping the newest 14 copies.
- SQL stays portable (no SQLite-only features beyond `STRICT`), so that moving to PostgreSQL later is a migration task, not a rewrite.
- Repository tests run against a real SQLite file in a temporary folder with the production migrations. Testcontainers is no longer needed for the persistence module.

## Options considered

- **SQLite** — one file, nothing to install, online backup, supported by Flyway. Cons: one writer at a time, same machine only, community-maintained Hibernate dialect, limited `ALTER TABLE`.
- **H2 (file mode)** — dialect in Hibernate core, server mode available. Rejected: file compatibility broke between minor versions (2.1.214 → 2.2.220 needed dump and restore), a risk for customer data on every upgrade.
- **HSQLDB** — embedded and server modes. Rejected: slow releases, small community.
- **Embedded PostgreSQL (zonky)** — same database as a future server. Rejected: described by its authors as a testing library; on Windows it runs a separate process and may need extra runtimes.
- **Installed PostgreSQL** — most robust for network and cloud use. Rejected for now: needs installation and administration in the workshop. Revisit in phase 5 if Twister runs for several workshops.

## Consequences

- No database setup for users; backup and restore are file operations.
- The Hibernate SQLite dialect is not tested in Hibernate's CI, so our own repository tests on SQLite are the safety net.
- Schema changes that SQLite cannot do with `ALTER TABLE` need the table-rebuild pattern in migrations.
- Follow-up: persistence on SQLite (migrations V1, adapters, backup) in M2; the desktop skeleton in M1 connects to an empty SQLite database.
