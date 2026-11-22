-- Baseline: facts about this installation. Tables are STRICT, see tw-docs/adr/0009.
CREATE TABLE app_meta (
    key   TEXT NOT NULL PRIMARY KEY,
    value TEXT NOT NULL
) STRICT;
