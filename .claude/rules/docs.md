---
paths:
  - "tw-docs/**"
---

# Docs (draft)

- Follow `../../tw-docs/README.md`: notes `notes/YYYY-MM-DD-slug.md`, ADRs `adr/NNNN-slug.md`, diagrams as Mermaid `.mmd`.
- Register every new document in `../../tw-docs/manifest.json` and every ADR in `tw-docs/adr/README.md`.
- Never edit files under `../../tw-docs/artifacts`; they are snapshots.
- In Mermaid sequence diagrams avoid `;` in message and note text: it ends the statement.
- After changing the viewer (`../../tw-docs/app`), open it through `serve.cmd` and check one document of each type.
