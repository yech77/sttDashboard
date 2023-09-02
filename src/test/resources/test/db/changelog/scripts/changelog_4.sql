-- liquibase formatted sql

-- changeset liquibase:1
alter table files_to_send
    add total_sms_to_send integer default 0;

comment on column files_to_send.total_sms_to_send is 'Total sms a enviar ';