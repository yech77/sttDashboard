-- liquibase formatted sql

-- changeset liquibase:1
alter table files_to_send
    alter column message_with_param type varchar(500);