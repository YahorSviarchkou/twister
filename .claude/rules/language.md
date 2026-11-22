# Language

- Write everything in the repository in English: code, identifiers, comments, commit messages, docs, agent config.
- Exceptions only when the owner asks explicitly in the current task.
- User-facing UI texts go to i18n bundles (`messages.properties` is the English base, `messages_ru.properties` the Russian translation), never hard-coded. See `i18n.md`.
- When touching code with Russian comments or strings, translate them as part of the change.
