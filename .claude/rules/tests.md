---
paths:
  - "**/src/test/**"
---

# Tests

Test business logic and integrations, nothing else. Do not test Java, the JDK, libraries or generated code.

## What to test

- Domain (`tw-core`): aggregate invariants, state-machine transitions and guards, value-object validation. Plain JUnit, no Spring.
- Use cases (`tw-workflow`): scenario tests across several transitions with in-memory fakes of the ports; mocks have hidden real cascade bugs before.
- Persistence adapters: against a real SQLite file in a temporary folder with the production migrations (ADR 0009). Cover queries with logic and optimistic locking. Testcontainers stays in the build for future PostgreSQL work; SQLite is the default.
- Starter: a context test that starts with no properties and loads a screen.
- UI: view models with plain JUnit; one or two TestFX smoke tests per key screen at most.
- Architecture: ArchUnit rules (`ArchitectureTest`).
- Every bug fix comes with a test that fails without the fix.

## What not to test

- Getters, setters, records and DTOs without logic, Lombok and MapStruct output.
- Spring wiring as such, JPA entities, configuration and properties classes.
- FXML controllers (`*View`): test the view model instead.

## Coverage gate

- Only `tw-core` and `tw-workflow` have a gate: 80% lines and 80% branches per module (`BUNDLE`), configured in `twister.java-conventions`.
- Excluded from measurement everywhere: Lombok-generated code (`lombok.addLombokGeneratedAnnotation`), `*MapperImpl`, JPA entities, `*Configuration`, `*AutoConfiguration`, `*Properties`, `Launcher`, `*Application`, `*View`.
- Other modules report coverage without failing the build.
- Do not lower the gate or add exclusions to make a build pass; if a class seems to need an exclusion, add a `// TODO(discuss)` and mention it in the summary.
