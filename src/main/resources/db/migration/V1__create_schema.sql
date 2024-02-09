CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id            uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre        varchar(255),
    apellido      varchar(255),
    email         varchar(255),
    password      varchar(255),
    password_salt varchar(255),
    rol           varchar(255) CHECK ( rol IN ('ADMIN', 'USER') ) DEFAULT 'USER'
);
