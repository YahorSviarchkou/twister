CREATE TABLE customers
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

CREATE TABLE orders
(
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    notes       VARCHAR,
    customer_id INTEGER NOT_NULL,
    created     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE operation_spares
(
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    operation_id INTEGER NOT NULL,
    spare_ids    VARCHAR NOT NULL,
    CONSTRAINT unq__op_sps UNIQUE (operation_id, spare_ids)
);

CREATE TABLE order_op_sps
(
    order_id            INTEGER NOT NULL,
    operation_spares_id INTEGER NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (operation_spares_id) REFERENCES operation_spares (id),
    CONSTRAINT unq__order_op_sps UNIQUE (order_id, operation_spares_id)
);