--liquibase formatted sql
--changeset Siarhei_Kavaleu:db localFilePath:01.000.00/users-data.sql
INSERT INTO users(id, email, confirmation_code)
VALUES ('c04bae70-acfa-48b2-8baf-f4f3f7800bd0', 'sergey@somewhere.com', '123456'),
       ('8a91369e-ec33-42ae-a682-06e315d57234', 'petia@somewhere.com', null);