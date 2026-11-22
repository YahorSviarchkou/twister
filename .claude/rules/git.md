# Git

See `tw-docs/adr/0008-github-issues-as-tracker.md` (supersedes 0007).

## Commands

- Never run state-changing git commands unless the user explicitly asks in the current task:
  `commit`, `push`, `pull`, `add`, `checkout`, `switch`, `branch -d/-D/-m`, `merge`, `rebase`, `reset`, `restore`,
  `stash`, `tag`, `cherry-pick`, `revert`, `clean`.
- Read-only commands are always fine: `status`, `diff`, `log`, `show`, `blame`, `branch` (listing).
- Never force-push.
- When asked to commit, first show the proposed message and the list of files.

## Branches

- Long-lived: `dev` → `sit` → `prod`. Never commit to them directly.
- Work branches start from `dev`: `feature/tw-<issue>-<short-name>`, `bugfix/tw-<issue>-<short-name>`.
- `<issue>` is the GitHub issue number; `<short-name>` is lowercase kebab-case, a few words: `feature/tw-12-intake-form`, `bugfix/tw-7-reject-open-order`.
- No issue for the work yet: propose a title and labels and ask the owner; never invent an issue number.

## Commit messages

```
<type>(<scope>): <description>

<optional body: why, not what>

Refs #<issue>
```

- `type`: `feat` | `fix` | `refactor` | `test` | `docs` | `chore`.
- `scope`: the Gradle module name (`tw-workflow`, `tw-persistence-api`, …); outside modules use `tw-docs`, `claude` (CLAUDE.md, .claude), `build` (root Gradle files), `ci` (.github).
- One scope per commit: split changes that touch several modules.
- `description`: imperative, lowercase, no trailing period; the whole header ≤ 72 characters.
- Reference the issue in the body: `Refs #12`; the commit (or PR) that completes it uses `Closes #12`.
- Never add `Co-Authored-By`, "Generated with", session links or any other attribution trailers.

Examples:

```
fix(tw-workflow): skip cancelled items when finishing a task
refactor(tw-persistence-api): move auto-configuration imports to META-INF
docs(tw-docs): add ADR for git conventions
```

## Issues

- Tasks live in GitHub Issues of `YahorSviarchkou/twister`.
- Title: short imperative English sentence, no type prefix: `Move auto-configuration imports to META-INF`.
- Type (the issue Type field, not a label): `Feature` for new behaviour, `Bug` for incorrect behaviour, `Task` for everything else (refactoring, tests, docs, build, CI).
- Labels: one or more `module:*`, one `phase:*`; `needs-discussion` when owner input is required, `blocked` when waiting on another issue.
- Body: **Context**, **Task** (checklist), **Done when**, **Links** (tw-docs paths, related issues).
