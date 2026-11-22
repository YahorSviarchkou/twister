# 0002. Documentation as code in `tw-docs`

- **Status:** Accepted
- **Date:** 2026-11-21
- **Deciders:** project owner

## Context

Analysis, designs and diagrams were produced as Claude artifacts (HTML pages, a whiteboard, a design canvas). They live outside the repository, can't be reviewed with the code, and need a claude.ai account to open.

## Decision

We will keep all documentation in the `tw-docs/` folder of the repository:

- notes, ADRs and diagrams as plain text (Markdown, Mermaid);
- exported artifacts as snapshots in `tw-docs/artifacts/`, unchanged;
- a small viewer (`tw-docs/index.html`, vanilla JS, vendored libraries, no build step) that lists documents from `manifest.json` and renders Markdown, Mermaid, HTML pages and whiteboard snapshots.

## Options considered

- **Wiki or Confluence** — separate from code, no review in pull requests.
- **Static site generator (MkDocs, Docusaurus)** — adds a Python or Node toolchain and a build step; cannot show the exported interactive artifacts as they are.
- **Plain files + small viewer** — chosen: no toolchain, works offline, artifacts keep their interactivity.

## Consequences

- The viewer needs a local web server (`jwebserver`, `python -m http.server`), because browsers block `fetch` from `file://`.
- Diagrams are Mermaid sources, so changes show in diffs.
- Exported whiteboards are read-only in the viewer; editing happens on the live board and is exported again.
