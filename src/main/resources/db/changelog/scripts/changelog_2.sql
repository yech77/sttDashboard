-- liquibase formatted sql

-- changeset liquibase:1
alter table Agenda
    add size_of_lines varchar(500);