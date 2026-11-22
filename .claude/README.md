# .claude

Configuration and context for Claude Code. The root `CLAUDE.md` stays short; details live here.

| Path | Loaded | Purpose |
|---|---|---|
| `../CLAUDE.md` | Always, at session start | Project summary, map, commands, working agreements |
| `rules/*.md` without `paths` | Always | Short global rules: language, git, TODOs |
| `rules/*.md` with `paths` frontmatter | Only when Claude works on matching files | Conventions per area: Java and comments, Gradle, tests, docs |
| `context/*.md` | Never automatically; read on demand | Reference: modules, architecture, domain |
| `settings.json` | Always | No commit/PR attribution; git write commands need confirmation, force-push is denied |
| `settings.local.json` | Always, personal, git-ignored | Local overrides |

## Status

Agreed rules: `language.md`, `git.md`, `todo.md`, `comments.md`. Drafts to review: `java.md`, `gradle.md`, `tests.md`, `docs.md`. Still to design: hooks, slash commands (`commands/`) and subagents (`agents/`).

## Conventions

- English only (see `tw-docs/adr/0003-english-as-project-language.md`).
- One topic per rule file, a few short bullets each. Long explanations go to `context/` or `tw-docs/`.
- Scope a rule with `paths` whenever it applies only to some files, to keep the always-loaded context small.
