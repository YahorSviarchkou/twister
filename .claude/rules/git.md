# Git

See `tw-docs/adr/0008-github-issues-as-tracker.md` (supersedes 0007).

## Commands

- Within a task, without asking: create the work branch from `dev` with `git switch -c`, switch branches, `git add`, `git commit` following the conventions below. Commit only on work branches, never on `dev`, `sit`, `prod`.
- Ask first: push, pull, merge, rebase, reset, restore, checkout, stash, tag, cherry-pick, revert, clean, `commit --amend`, deleting or renaming branches.
- Read-only commands are always fine: `status`, `diff`, `log`, `show`, `blame`, `branch` (listing).
- Never force-push.
- List the commits made (hash and header) in the task summary.

## Branches

- Long-lived: `dev` → `sit` → `prod`. Never commit to them directly.
- Work branches start from `dev`: `feature/tw-<issue>-<short-name>`, `bugfix/tw-<issue>-<short-name>`.
- `<issue>` is the GitHub issue number; `<short-name>` is lowercase kebab-case, a few words: `feature/tw-12-intake-form`, `bugfix/tw-7-reject-open-order`.
- No issue for the work yet: propose a title and labels and ask the owner; never invent an issue number.

## Commit messages

```
<type>(<scope>): <description> TW-<issue>

<optional body: why, not what>
```

- `type`: `feat` | `fix` | `refactor` | `test` | `docs` | `chore`.
- `scope`: the Gradle module name (`tw-workflow`, `tw-persistence-api`, …); outside modules use `tw-docs`, `claude` (CLAUDE.md, .claude), `build` (root Gradle files), `ci` (.github).
- One scope per commit: split changes that touch several modules.
- `description`: imperative, lowercase, no trailing period.
- The header ends with the issue key `TW-<issue>` (uppercase, the same number as in the branch name). The whole header, key included, is ≤ 72 characters.
- Body: optional, wrapped at 72 characters, explains why. No `Refs #` footer: the key in the header links the commit to the issue.
- Closing issues: the PR description says `Closes #<issue>` (GitHub closes issues only through `#<issue>`, not through the key).
- Never add `Co-Authored-By`, "Generated with", session links or any other attribution trailers.

Examples:

```
fix(tw-persistence-api): fix specification tests and mapper cycle TW-7

Filters need persisted relations, the order/invoice mapper cycle kept
the context from starting, and Boot downgraded Testcontainers below
what Docker 29 accepts.
```

```
fix(tw-persistence-api): move auto-config imports to META-INF TW-6
docs(tw-docs): record accepted architecture decisions TW-13
```

## Issues

- Tasks live in GitHub Issues of `YahorSviarchkou/twister`.
- Title: short imperative English sentence, no type prefix: `Move auto-configuration imports to META-INF`.
- Type (the issue Type field, not a label): `Feature` for new behaviour, `Bug` for incorrect behaviour, `Task` for everything else (refactoring, tests, docs, build, CI).
- Labels: one or more `module:*`, one `phase:*`; `needs-discussion` when owner input is required, `blocked` when waiting on another issue.
- Body: **Context**, **Task** (checklist), **Done when**, **Links** (tw-docs paths, related issues).
