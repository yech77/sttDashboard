-- liquibase formatted sql

-- changeset liquibase:7
alter table jan_sms
    add id_file_to_send bigint default 0;
comment on column jan_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table feb_sms
    add id_file_to_send bigint default 0;
comment on column feb_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table mar_sms
    add id_file_to_send bigint default 0;
comment on column mar_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table apr_sms
    add id_file_to_send bigint default 0;
comment on column apr_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table may_sms
    add id_file_to_send bigint default 0;
comment on column may_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table jun_sms
    add id_file_to_send bigint default 0;
comment on column jun_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table jul_sms
    add id_file_to_send bigint default 0;
comment on column jul_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table aug_sms
    add id_file_to_send bigint default 0;
comment on column aug_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table sep_sms
    add id_file_to_send bigint default 0;
comment on column sep_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table oct_sms
    add id_file_to_send bigint default 0;
comment on column oct_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table nov_sms
    add id_file_to_send bigint default 0;
comment on column nov_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table dec_sms
    add id_file_to_send bigint default 0;
comment on column dec_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table temp_sms
    add id_file_to_send bigint default 0;
comment on column temp_sms.id_file_to_send is 'Programacion de la cual es el mensaje';

alter table sms_hour
    add id_file_to_send bigint default 0;
comment on column sms_hour.id_file_to_send is 'Programacion de la cual es el mensaje';