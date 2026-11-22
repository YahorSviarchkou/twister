# 0007. Git workflow and commit conventions

- **Status:** Superseded by [0008](0008-github-issues-as-tracker.md)
- **Date:** 2026-11-21
- **Deciders:** project owner

## Context

The repository has `main`, a stale `dev` and the working branch `exp` with a large uncommitted change set. Commit messages mix styles. Coding agents will work in the repository and must not change git state on their own.

## Decision

**Branches.** Long-lived `dev` → `sit` → `prod`. Work happens in `feature/tw-<short-name>` and `bugfix/tw-<short-name>`, created from `dev`; `<short-name>` is lowercase kebab-case. Nothing is committed to `dev`, `sit` or `prod` directly. Migration: `main` becomes `prod`, `exp` is merged into `dev`.

**Commits.** `<type>(<scope>): <description>`, optional body with the reason after a blank line.

- `type`: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`.
- `scope`: the Gradle module name; outside modules `tw-docs`, `claude`, `build`, `ci`. One scope per commit.
- `description`: imperative, lowercase, no trailing period; header up to 72 characters.
- No `Co-Authored-By` or other attribution trailers.

**Agents.** No state-changing git command without an explicit request in the current task. Enforced twice: `.claude/rules/git.md` and `.claude/settings.json` (`ask` for write commands, `deny` for force-push, empty `attribution`).

**Open questions in code.** Doubts found during a task are left as `// TODO(discuss): …` at the exact place and listed in the task summary. Once a tracker exists, the ticket key replaces `discuss`.

## Consequences

- History reads by module: `git log --grep "(tw-workflow)"`.
- Cross-module changes take several commits.
- When a ticket tracker is chosen, branch names and TODOs get the ticket key; this ADR will be superseded then.
