--liquibase formatted sql
--changeset Siarhei_Kavaleu:db localFilePath:01.000.00/users.sql
CREATE TABLE users
(
    id                  UUID                            NOT NULL,
    email               VARCHAR                         NOT NULL,
    confirmation_code   VARCHAR(10),
    CONSTRAINT pk_users PRIMARY KEY (id)
);