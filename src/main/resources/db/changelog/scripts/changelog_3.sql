-- liquibase formatted sql

-- changeset liquibase:1
create table if not exists odash_conf
(
    id                 bigint  not null
        primary key,
    sync_id            integer not null,
    sync_data          varchar(255),
    version            integer default 0,
    last_modified_date timestamp
);

alter table odash_conf
    owner to odash;
