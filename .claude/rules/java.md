---
paths:
  - "**/*.java"
---

# Java (draft)

- Java 21. Prefer records for immutable data and `switch` pattern matching where it reads better.
- `tw-core` (future `tw-api`) stays free of Spring, JPA and JavaFX imports.
- Constructor injection only; no field `@Autowired` in new code.
- Lombok is allowed for boilerplate; do not put `@Data`, `@EqualsAndHashCode` or `@ToString` with lazy collections on JPA entities.
- DAO ports must not expose Spring Data types (`Page`, `Pageable`) in new code; see ADR 0004.
- Run `gradlew spotlessApply` before finishing; formatting is Palantir Java Format.
