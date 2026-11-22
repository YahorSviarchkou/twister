---
paths:
  - "**/*.java"
---

# Comments and Javadoc

- Comment why, not what. Write no comment when names and code already say it.
- Inline `//` comments only for non-obvious logic: business rules, workarounds, invariants, ordering constraints.
- Javadoc on classes, methods and fields that need an explanation. Link related code with `{@link}` / `{@linkplain}`, mark literals with `{@code}`, refer to decisions as `see tw-docs/adr/NNNN`.
- The first sentence is the summary. Keep the whole comment as short as possible without losing meaning.
- No Javadoc that restates a name: getters, setters, obvious constructors, Lombok-generated members.
- `@param`, `@return`, `@throws` only when they add information beyond the signature.
- Public API of `tw-api` (ports, domain events, extension points) is the exception: every public type and method has Javadoc, because plugin authors work from it.
