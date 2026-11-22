# Twister docs

Project documentation for Twister, a bicycle repair workshop management app. Everything here is plain files under version control; `index.html` is only a viewer for them.

## Open the viewer

The viewer loads files with `fetch`, so it needs a local web server. Opening `index.html` by double-click does not work.

| Option | Command |
|---|---|
| Windows | run `tw-docs\serve.cmd` |
| macOS / Linux | run `tw-docs/serve.sh` |
| Any JDK 18+ | `cd tw-docs` then `jwebserver -p 8765` |
| Python | `cd tw-docs` then `python -m http.server 8765` |

Then open <http://127.0.0.1:8765/>. Everything works offline: Mermaid and marked are vendored in `vendor/`.

## What is where

| Folder | Content | Format |
|---|---|---|
| `notes/` | Analysis, ideas, meeting and design notes | Markdown, `YYYY-MM-DD-slug.md` |
| `adr/` | Architecture decision records | Markdown, `NNNN-slug.md`, see [ADR index](adr/README.md) |
| `diagrams/` | C4, sequence, state and module diagrams | Mermaid `.mmd` |
| `artifacts/` | Exported Claude artifacts, kept as they were | One folder per artifact, `YYYY-MM-DD-slug/` |
| `app/` | Viewer code | Vanilla JS, no build step |
| `vendor/` | Third-party libraries for the viewer | Pinned versions with licenses |
| `manifest.json` | List of documents shown in the viewer | JSON |

## Adding a document

1. Put the file in the right folder, named by the convention above.
2. Add an entry to `manifest.json`:

```json
{ "id": "note-my-topic", "title": "My topic", "type": "md", "section": "notes",
  "path": "notes/2026-12-01-my-topic.md", "date": "2026-12-01", "tags": ["domain"] }
```

`type` is one of `md`, `mermaid`, `html`, `board`. Optional fields: `description`, `status`, `lang` (when not English), `source` (link to a live original).

3. Link documents to each other with relative paths, for example `[ADR 0004](../adr/0004-build-time-plugin-composition.md)`. The viewer turns such links into in-app navigation.

## Rules

- **English** for all docs. Exported artifacts keep their original language and are marked with `lang` in the manifest.
- **Text over pictures.** Diagrams that live with the code are Mermaid sources in `diagrams/`, so changes show up in diffs and code review.
- **Artifacts are snapshots.** Do not edit files under `artifacts/`; write a note or an ADR instead.
- **Decisions go to ADRs.** A note can propose; only an ADR records what was decided.

## Start here

- [Codebase analysis, 2026-11-15](notes/2026-11-15-codebase-analysis.md)
- [Product rethink and phases](notes/2026-11-15-product-rethink.md)
- [Extension architecture](notes/2026-11-15-extension-architecture.md)
- [ADR index](adr/README.md)
