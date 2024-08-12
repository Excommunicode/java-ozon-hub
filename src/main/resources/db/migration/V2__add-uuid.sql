
drop table if exists user_roles cascade;

drop table if exists users cascade;

drop table if exists roles cascade;


CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users
(
    id       UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    login    VARCHAR(64) UNIQUE   NOT NULL,
    password VARCHAR(1024)        NOT NULL,
    email    VARCHAR(1024) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS roles
(
    id   UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE user_roles
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);


