CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR NOT NULL
);

CREATE TABLE clients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR,
    surname VARCHAR,
    patronymic VARCHAR,
    phone VARCHAR NOT NULL UNIQUE
);

CREATE TABLE transport_types (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR NOT NULL UNIQUE
);

CREATE TABLE transports (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR NOT NULL UNIQUE,
    type_id INTEGER NOT NULL,
    FOREIGN KEY(type_id) REFERENCES transport_types(id)
);

CREATE TABLE spares (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR NOT NULL UNIQUE,
    cost NUMERIC(20, 2) CHECK (cost >= 0)
);

CREATE TABLE operations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR NOT NULL UNIQUE,
    cost NUMERIC(20, 2) CHECK (cost >= 0)
);

CREATE TABLE spare_operations (
    spare_id INTEGER NOT NULL,
    operation_id INTEGER NOT NULL,
    FOREIGN KEY(spare_id) REFERENCES spares(id),
    FOREIGN KEY(operation_id) REFERENCES operations(id),
    CONSTRAINT unq__spare_operations UNIQUE(spare_id, operation_id)
);

CREATE TABLE tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR NOT NULL,
    client_id INTEGER NOT NULL,
    transport_id INTEGER NOT NULL,
    status VARCHAR NOT NULL,
    created TEXT NOT NULL,
    updated TEXT,
    completed TEXT,
    total_cost NUMERIC(20, 2) CHECK (total_cost >= 0),
    FOREIGN KEY(client_id) REFERENCES clients(id),
    FOREIGN KEY(transport_id) REFERENCES transports(id)
);

CREATE TABLE task_operations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    task_id INTEGER NOT NULL,
    operation_id INTEGER NOT NULL,
    status VARCHAR NOT NULL,
    FOREIGN KEY(task_id) REFERENCES tasks(id),
    FOREIGN KEY(operation_id) REFERENCES operations(id),
    CONSTRAINT unq__task_operations UNIQUE(task_id, operation_id)
);