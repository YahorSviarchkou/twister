# UI design v1: exported sources

Export of the Claude Design canvas "Twister — дизайн интерфейса" (UI in Russian), taken on 2026-11-21.

- **Live design:** <https://claude.ai/artifact/AJTesZt4JMRCbm2r5AYeK7>
- **Summary in English:** [UI design v1 note](../../notes/2026-11-15-ui-design-v1.md)

## What is in `source/`

| File | Content |
|---|---|
| `canvas.json` | Board layout, titles and sticky notes of the canvas |
| `*.dc.html` | One file per board: markup, inline styles and a small `DCLogic` class with sample data |

These files are templates for the Claude Design runtime (`<x-dc>`, `sc-if`, `sc-for`, `dc-import`) and a shared stylesheet that lives only in the live artifact. They do not render on their own, so the viewer does not list them as pages. They are kept because they hold the exact layout, sizes, colours, texts and sample data, which is useful when building the real JavaFX or web screens.

To look at the screens, open the live design. If the design changes there, export it again into a new dated folder instead of editing this one.
