CREATE TABLE clients
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       VARCHAR,
    surname    VARCHAR,
    patronymic VARCHAR,
    phone      VARCHAR NOT NULL UNIQUE
);

CREATE TABLE spares
(
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR NOT NULL UNIQUE,
    cost  NUMERIC(20, 2) CHECK (cost >= 0)
);

CREATE TABLE operations
(
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR NOT NULL UNIQUE,
    cost  NUMERIC(20, 2) CHECK (cost >= 0)
);

CREATE TABLE works
(
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    notes     VARCHAR,
    client_id INTEGER NOT_NULL,
    created   DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE work_operations
(
    work_id      INTEGER NOT NULL,
    operation_id INTEGER NOT NULL,
    FOREIGN KEY (work_id) REFERENCES works (id),
    FOREIGN KEY (operation_id) REFERENCES operations (id),
    CONSTRAINT unq__work_operations UNIQUE (work_id, operation_id)
);

CREATE TABLE spare_operations
(
    spare_id     INTEGER NOT NULL,
    operation_id INTEGER NOT NULL,
    FOREIGN KEY (spare_id) REFERENCES spares (id),
    FOREIGN KEY (operation_id) REFERENCES operations (id),
    CONSTRAINT unq__spare_operations UNIQUE (spare_id, operation_id)
);