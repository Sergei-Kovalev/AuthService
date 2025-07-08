--liquibase formatted sql
--changeset Siarhei_Kavaleu:db localFilePath:01.000.00/users-data.sql
INSERT INTO users(id, username, password, email, confirmation_code)
VALUES ('c04bae70-acfa-48b2-8baf-f4f3f7800bd0', 'Sergey', '$2a$12$o4WvumQBVIGKscwWus79aOU54qe.pfAHQUTjU42HTRaQg3qlNqoh2', 'sergey@somewhere.com', null),
       ('8a91369e-ec33-42ae-a682-06e315d57234', 'Petia', '$2a$12$26tj9AlS9AtBZ9E47BlQ5uUacq5Qa/I8GsbaYKFs6vVVkUY4OleJW', 'petia@somewhere.com', null);