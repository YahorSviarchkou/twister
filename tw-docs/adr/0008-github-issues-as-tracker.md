# 0008. GitHub Issues as the task tracker

- **Status:** Accepted
- **Date:** 2026-11-21
- **Deciders:** project owner
- **Supersedes:** [0007](0007-git-workflow-and-commit-conventions.md)

## Context

ADR 0007 set branch and commit conventions without a task tracker. The owner does not want a separate Jira. The repository is on GitHub (`YahorSviarchkou/twister`).

## Decision

We will track tasks in **GitHub Issues** with a **GitHub Projects** board. Everything from ADR 0007 stays, with these changes:

- **Branches:** `feature/tw-<issue>-<short-name>`, `bugfix/tw-<issue>-<short-name>`, created from `dev`. Long-lived `dev` → `sit` → `prod`; `main` becomes `prod`, `exp` is merged into `dev`, `dev` becomes the default branch so that `Closes #N` closes issues on merge.
- **Commits:** `<type>(<scope>): <description>`, body references the issue: `Refs #12`; the completing commit or PR says `Closes #12`. Types `feat`, `fix`, `refactor`, `test`, `docs`, `chore`; scope is the Gradle module, or `tw-docs`, `claude`, `build`, `ci`. No attribution trailers.
- **TODOs:** `// TODO(tw-<issue>): …` inside an existing issue's scope, `// TODO(discuss): …` for new doubts; agents list both in the task summary.
- **Issue type:** the built-in Type field: `Feature`, `Bug`, `Task` (refactoring, tests, docs, build, CI). The finer commit types stay in commit messages only.
- **Labels:**
  - `module:tw-core`, `module:tw-workflow`, `module:tw-persistence-api`, `module:tw-fx-javafx-starter`, `module:tw-app-desktop`, `module:tw-docs`, `module:build`, `module:ci`, `module:claude`
  - `phase:0` … `phase:5` (see [product rethink](../notes/2026-11-15-product-rethink.md))
  - `needs-discussion`, `blocked`
- **Issue forms:** `.github/ISSUE_TEMPLATE/` (feature, bug, task). Labels are created on first use when an issue gets them (grey, no description); `.github/scripts/setup-github.ps1` optionally sets colours and descriptions. Phase-0 issues: #2-#10.

## Options considered

- **GitHub Issues + Projects** — chosen: free, next to the code, `Closes #N`, usable by agents through `gh` or the GitHub MCP server.
- **Linear, YouTrack** — better UX, but another service and account.
- **Markdown backlog in `tw-docs/`** — no extra tool, but no board, notifications or auto-close.

## Consequences

- Every branch starts from an issue; agents ask for an issue instead of inventing a number.
- Labels are the shared vocabulary between issues, commits and the board.
