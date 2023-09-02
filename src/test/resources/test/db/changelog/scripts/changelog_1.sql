-- liquibase formatted sql

-- changeset liquibase:1
alter table sending_sms
    ALTER COLUMN messages_text type varchar(500);