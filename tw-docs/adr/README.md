# Architecture decision records

An ADR records one decision: the context, what was decided, and the consequences. ADRs are short and never rewritten after acceptance; a later ADR supersedes an earlier one.

## Status values

- **Proposed** — written down for discussion, not decided yet.
- **Accepted** — decided; code and docs follow it.
- **Superseded by NNNN** — replaced by a later ADR.
- **Rejected** — considered and declined; kept for the record.

## Index

| # | Title | Status | Date |
|---|---|---|---|
| [0001](0001-record-architecture-decisions.md) | Record architecture decisions | Accepted | 2026-11-21 |
| [0002](0002-docs-as-code-in-tw-docs.md) | Documentation as code in `tw-docs` | Accepted | 2026-11-21 |
| [0003](0003-english-as-project-language.md) | English as the project language | Accepted | 2026-11-21 |
| [0004](0004-build-time-plugin-composition.md) | Build-time plugin composition | Accepted | 2026-11-21 |
| [0005](0005-merge-task-into-order.md) | Merge repair task into the order: two state machines | Accepted | 2026-11-21 |
| [0006](0006-desktop-hosts-lan-server.md) | Desktop app hosts a LAN server for tablets | Proposed | 2026-11-21 |
| [0007](0007-git-workflow-and-commit-conventions.md) | Git workflow and commit conventions | Superseded by 0008 | 2026-11-21 |
| [0008](0008-github-issues-as-tracker.md) | GitHub Issues as the task tracker | Accepted | 2026-11-21 |
| [0009](0009-sqlite-as-embedded-database.md) | Use SQLite as the embedded database | Accepted | 2026-11-22 |

## Writing a new ADR

Copy [the template](0000-template.md) to `NNNN-short-slug.md` with the next free number, fill it in, add a row above and an entry in `tw-docs/manifest.json`.
