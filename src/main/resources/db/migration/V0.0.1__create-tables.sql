CREATE SCHEMA IF NOT EXISTS twister;
SET search_path TO twister;

CREATE TYPE status_type AS ENUM ('DRAFT', 'IN PROGRESS', 'BLOCKED', 'DONE');

CREATE TABLE users (
    id serial PRIMARY KEY NOT NULL,
    login varchar(50) NOT NULL UNIQUE,
    password varchar NOT NULL
);

CREATE TABLE clients (
    id serial PRIMARY KEY NOT NULL,
    name varchar,
    surname varchar,
    patronymic varchar,
    phone varchar NOT NULL UNIQUE
);

CREATE TABLE transport_types (
    id serial PRIMARY KEY NOT NULL,
    title varchar NOT NULL UNIQUE
);

CREATE TABLE transports (
    id serial PRIMARY KEY NOT NULL,
    title varchar NOT NULL UNIQUE,
    type_id bigint REFERENCES transport_types(id) NOT NULL
);

CREATE TABLE spares (
    id serial PRIMARY KEY NOT NULL,
    title varchar NOT NULL UNIQUE,
    cost NUMERIC(20, 2) CHECK (cost >= 0)
);

CREATE TABLE services (
    id serial PRIMARY KEY NOT NULL,
    title varchar NOT NULL UNIQUE,
    cost NUMERIC(20, 2) CHECK (cost >= 0)
);

CREATE TABLE spare_services (
    spare_id bigint REFERENCES spares(id) NOT NULL,
    service_id bigint REFERENCES services(id) NOT NULL
);

CREATE TABLE tasks (
    id serial PRIMARY KEY NOT NULL,
    title varchar NOT NULL,
    client_id bigint REFERENCES clients(id) NOT NULL,
    transport_id bigint REFERENCES transports(id) NOT NULL,
    status status_type,
    created timestamp NOT NULL,
    updated timestamp,
    completed timestamp,
    total_cost NUMERIC(20, 2) CHECK (total_cost >= 0)
);

CREATE TABLE task_services (
    id serial PRIMARY KEY NOT NULL,
    task_id bigint REFERENCES tasks(id) NOT NULL,
    service_id bigint REFERENCES services(id) NOT NULL,
    status status_type
);

ALTER TABLE task_services
ADD CONSTRAINT unq__task_services UNIQUE(task_id, service_id);

ALTER TABLE spare_services
ADD CONSTRAINT unq__spare_services UNIQUE(spare_id, service_id);