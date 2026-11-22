# Twister

Management app for repair workshops of personal light vehicles (bikes, kick scooters, e-scooters), built as a product for CIS and English-speaking markets; the first goal is one real workshop running a season on it. Java, Spring Boot, JavaFX, Gradle multi-module. It is being restructured from a monolithic Windows desktop app (legacy `legacy/`) into modules; the MVP is a full desktop app without plugins (ADR 0004 MVP scope, `.claude/rules/architecture.md`).

## Language

English for everything in the repository: code, identifiers, comments, commit messages, docs and agent config. Exceptions only when the owner asks explicitly. The owner may write to you in Russian; answer in the owner's language, but keep repository content in English. UI texts live in i18n bundles, not in code.

## Map

| Path | What |
|---|---|
| `tw-core` | Domain model, no Spring |
| `tw-workflow` | Services, state machines, DAO ports |
| `tw-persistence-api` | JPA entities, repositories, specifications, mappers |
| `tw-fx-javafx-starter` | Spring Boot starter for JavaFX screens |
| `tw-app-desktop` | Desktop assembly (does not start yet) |
| `tw-app-web`, `tw-fx-plugin-*` | Empty placeholders |
| `legacy/` | Legacy monolith (own `settings.gradle.kts`, builds with `gradlew -p legacy build`), frozen: read for reference, do not extend |
| `tw-docs/` | Documentation: notes, ADRs, Mermaid diagrams, exported artifacts, viewer |
| `.claude/` | Agent rules (`rules/`) and reference context (`context/`) |

Details: `.claude/context/modules.md`, `.claude/context/architecture.md`, `.claude/context/domain.md`. Read them when the task touches those areas; they are not loaded automatically.

## Commands

Use `gradlew.bat` on Windows, `./gradlew` elsewhere.

- Check one module (tests, Spotless, Jacoco gate): `gradlew :tw-workflow:check`
- Format: `gradlew spotlessApply`
- Docs viewer: `tw-docs/serve.cmd`, then http://127.0.0.1:8765/

## Working agreements

- Decisions live in `tw-docs/adr/`. Check them before changing architecture; propose a new ADR instead of silently deviating.
- Run `git status` before editing and never discard or stash changes you did not make.
- Tasks are GitHub Issues. Git: branches, staging and commits on work branches are allowed within a task; push, merge and history rewrites need the owner's request. Branch, commit and issue conventions are in `.claude/rules/git.md`.
- Doubts found during a task go into a `// TODO(discuss): …` comment right away and into the task summary.
- Keep diagrams as Mermaid sources in `tw-docs/diagrams/`; register new docs in `tw-docs/manifest.json`.
