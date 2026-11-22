---
paths:
  - "**/src/test/**"
---

# Tests (draft)

- JUnit Jupiter + AssertJ + Mockito. Persistence tests use Testcontainers (Postgres) through `BaseIntegrationTest`.
- Every bug fix comes with a test that fails without the fix.
- Workflow changes need a scenario test across several transitions, not only per-method unit tests: mocks have hidden real cascade bugs before.
- The Jacoco gate is 80% line coverage per class in `tw-workflow` and `tw-persistence-api`; do not lower it to make a build pass.
