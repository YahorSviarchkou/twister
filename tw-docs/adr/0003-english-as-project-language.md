# 0003. English as the project language

- **Status:** Accepted
- **Date:** 2026-11-21
- **Deciders:** project owner

## Context

Early code, commits and design artifacts mix Russian and English. The UI of the product is localised separately through i18n bundles.

## Decision

We will use English for everything in the repository: code, identifiers, comments, commit messages, documentation, agent configuration (`CLAUDE.md`, `.claude/`). Exceptions are made only on the owner's explicit request.

User-facing UI texts are not covered: they come from i18n bundles (Russian first, English second).

## Consequences

- Exported artifacts keep their original language and are marked with `lang` in `tw-docs/manifest.json`.
- Existing Russian comments and strings in code are translated when the code is touched.
