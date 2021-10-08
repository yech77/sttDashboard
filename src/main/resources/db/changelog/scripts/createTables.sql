create sequence hibernate_sequence;
alter sequence hibernate_sequence owner to postgres;

create table if not exists sending_sms
(
    id                bigint  not null
        constraint sending_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20),
    file_to_send_id   bigint
);

alter table sending_sms
    owner to postgres;

create table if not exists apr_sms
(
    id                bigint  not null
        constraint apr_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table apr_sms
    owner to postgres;

create table if not exists aug_sms
(
    id                bigint  not null
        constraint aug_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table aug_sms
    owner to postgres;

create table if not exists carrier
(
    id               bigint       not null
        constraint carrier_pkey
            primary key,
    version          integer default 0,
    carrier_charcode varchar(20)  not null
        constraint uk_15wx1s9j6r5blnkua63l40nps
            unique,
    carrier_name     varchar(100) not null,
    country_iso2     varchar(3)   not null
);

alter table carrier
    owner to postgres;

create table if not exists client
(
    id          bigint       not null
        constraint client_pkey
            primary key,
    version     integer default 0,
    client_cod  varchar(20)
        constraint uk_mrkgta4fbmtmk6uvhqtdndjmb
            unique,
    client_name varchar(100),
    cuadrante   varchar(255) not null,
    email       varchar(75)
        constraint uk_bfgjs3fem0hmjhvih80158x29
            unique
);

alter table client
    owner to postgres;

create table if not exists customer
(
    id           bigint not null
        constraint customer_pkey
            primary key,
    version      integer default 0,
    details      varchar(255),
    full_name    varchar(255),
    phone_number varchar(20)
);

alter table customer
    owner to postgres;

create table if not exists dec_sms
(
    id                bigint  not null
        constraint dec_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table dec_sms
    owner to postgres;

create table if not exists feb_sms
(
    id                bigint  not null
        constraint feb_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table feb_sms
    owner to postgres;

create table if not exists jan_sms
(
    id                bigint  not null
        constraint jan_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table jan_sms
    owner to postgres;

create table if not exists jul_sms
(
    id                bigint  not null
        constraint jul_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table jul_sms
    owner to postgres;

create table if not exists jun_sms
(
    id                bigint  not null
        constraint jun_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table jun_sms
    owner to postgres;

create table if not exists mar_sms
(
    id                bigint  not null
        constraint mar_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table mar_sms
    owner to postgres;

create table if not exists may_sms
(
    id                bigint  not null
        constraint may_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table may_sms
    owner to postgres;

create table if not exists nov_sms
(
    id                bigint  not null
        constraint nov_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table nov_sms
    owner to postgres;

create table if not exists oauthority
(
    id        bigint not null
        constraint oauthority_pkey
            primary key,
    version   integer default 0,
    auth_desc varchar(255),
    auth_name varchar(255)
);

alter table oauthority
    owner to postgres;

create table if not exists oct_sms
(
    id                bigint  not null
        constraint oct_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table oct_sms
    owner to postgres;

create table if not exists odash_audit_event
(
    id         bigint not null
        constraint odash_audit_event_pkey
            primary key,
    version    integer default 0,
    event_date timestamp,
    event_desc varchar(255),
    event_type integer,
    principal  varchar(100)
);

alter table odash_audit_event
    owner to postgres;

create table if not exists orole
(
    id       bigint       not null
        constraint orole_pkey
            primary key,
    version  integer default 0,
    rol_name varchar(150) not null
        constraint uk_1qkctr6ta6uh8pxqygt27b43
            unique
);

alter table orole
    owner to postgres;

create table if not exists ouser
(
    id             bigint       not null
        constraint ouser_pkey
            primary key,
    version        integer default 0,
    active         boolean      not null,
    created_by     varchar(255),
    created_date   timestamp,
    locked         boolean      not null,
    password_hash  varchar(255) not null,
    user_email     varchar(75)
        constraint uk_pbxnkvvqvwr4q53cn8jmxq7rd
            unique,
    user_lastname  varchar(20),
    user_name      varchar(20),
    user_password  varchar(15),
    user_type      integer,
    user_type_ord  integer      not null,
    user_parent_id bigint
        constraint fk96ykmtl4jloirnpa5lgw8qpha
            references ouser
);

alter table ouser
    owner to postgres;

create index if not exists idxj26wq1cukqdd00d6t36kxqmlg
    on ouser (user_email);

create table if not exists pickup_location
(
    id      bigint not null
        constraint pickup_location_pkey
            primary key,
    version integer default 0,
    name    varchar(255)
        constraint uk_hpm8q7wxb5r5tjtac1glmlx9f
            unique
);

alter table pickup_location
    owner to postgres;

create table if not exists order_info
(
    id                 bigint  not null
        constraint order_info_pkey
            primary key,
    version            integer default 0,
    due_date           date    not null,
    due_time           time    not null,
    state              integer not null,
    customer_id        bigint  not null
        constraint fkkgr3op99u82sgutpyys8klgl0
            references customer,
    pickup_location_id bigint  not null
        constraint fkemqrbfaf7hs1nx4ldylfq6292
            references pickup_location
);

alter table order_info
    owner to postgres;

create index if not exists idxd8bqjebi2o4fxpq5d3fp55ub7
    on order_info (due_date);

create table if not exists product
(
    id      bigint not null
        constraint product_pkey
            primary key,
    version integer default 0,
    name    varchar(255)
        constraint uk_jmivyxk9rmgysrmsqw15lqr5b
            unique,
    price   integer
        constraint product_price_check
            check ((price <= 100000) AND (price >= 0))
);

alter table product
    owner to postgres;

create table if not exists order_item
(
    id          bigint  not null
        constraint order_item_pkey
            primary key,
    version     integer default 0,
    comment     varchar(255),
    quantity    integer not null
        constraint order_item_quantity_check
            check (quantity >= 1),
    product_id  bigint  not null
        constraint fk551losx9j75ss5d6bfsqvijna
            references product,
    items_id    bigint
        constraint fkohhydblc3by0xdl950lhtprrv
            references order_info,
    items_order integer
);

alter table order_item
    owner to postgres;

create table if not exists role_has_authority
(
    orole_id      bigint not null
        constraint fk400c6uiy66t2mram3ougy9qhv
            references orole,
    oauthority_id bigint not null
        constraint fkamfcy389hj0888ccwly5eghxy
            references oauthority,
    constraint role_has_authority_pkey
        primary key (orole_id, oauthority_id)
);

alter table role_has_authority
    owner to postgres;

create table if not exists sep_sms
(
    id                bigint  not null
        constraint sep_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table sep_sms
    owner to postgres;

create table if not exists sms_hour
(
    id                bigint      not null
        constraint sms_hour_pkey
            primary key,
    version           integer default 0,
    carrier_char_code varchar(20) not null,
    client_cod        varchar(20) not null,
    day               integer     not null,
    hour              integer     not null,
    message_type      varchar(3),
    month             integer     not null,
    system_id         varchar(20) not null,
    total             integer     not null,
    year              integer     not null
);

alter table sms_hour
    owner to postgres;

create table if not exists system_id
(
    id           bigint       not null
        constraint system_id_pkey
            primary key,
    version      integer default 0,
    payment_type varchar(255) not null,
    system_id    varchar(20)  not null
        constraint uk_o45wb27gh5knyuftnf8y0s5ru
            unique,
    client_id    bigint
        constraint fk1xgasp81vgt4j84rq8fj4bsfg
            references client
);

alter table system_id
    owner to postgres;

create table if not exists temp_sms
(
    id                bigint  not null
        constraint temp_sms_pkey
            primary key,
    carrier_char_code varchar(20),
    datacoding        integer not null,
    date              timestamp,
    destination       varchar(30),
    iso2              varchar(2),
    message_type      varchar(3),
    messages_text     varchar(170),
    msg_received      varchar(50),
    msg_sended        varchar(50),
    source            varchar(30),
    system_id         varchar(20)
);

alter table temp_sms
    owner to postgres;

create table if not exists user_info
(
    id             bigint       not null
        constraint user_info_pkey
            primary key,
    version        integer default 0,
    active         boolean      not null,
    created_by     varchar(255),
    created_date   timestamp,
    email          varchar(100)
        constraint uk_gnu0k8vv6ptioedbxbfsnan9g
            unique,
    first_name     varchar(100) not null,
    last_name      varchar(100),
    locked         boolean      not null,
    password_hash  varchar(255) not null,
    role           varchar(255),
    user_type      integer,
    user_type_ord  integer,
    user_parent_id bigint
        constraint fk1a6e7l5mpbquw3abidqn1p2li
            references user_info
);

alter table user_info
    owner to postgres;

create table if not exists agenda
(
    id                 bigint  not null
        constraint agenda_pkey
            primary key,
    version            integer default 0,
    creator_email      varchar(75),
    date_created       timestamp,
    description        varchar(200),
    file_name          varchar(255),
    file_name_original varchar(255),
    first_line         varchar(255),
    invalid_item_count integer not null,
    item_count         integer not null,
    name               varchar(100)
        constraint uk_30gj9tww7mcxajpljflsjmokt
            unique,
    status             integer,
    creator_id         bigint
        constraint fkc84dpcjrbeuckpd7m1yn2agf5
            references user_info
);

alter table agenda
    owner to postgres;

create table if not exists files_to_send
(
    id                 bigint       not null
        constraint files_to_send_pkey
            primary key,
    version            integer default 0,
    being_processed    boolean      not null,
    date_to_send       timestamp    not null,
    file_id            varchar(255),
    file_name          varchar(255),
    file_path          varchar(255),
    message_with_param varchar(255) not null,
    num_generated      integer      not null,
    num_sent           integer      not null,
    order_description  varchar(255),
    order_name         varchar(100) not null
        constraint uk_fji3p4s3cvae0m1nuy9m46yys
            unique,
    ready_to_send      boolean      not null,
    sms_count          integer      not null,
    status             integer,
    system_id          varchar(20)  not null,
    agenda_id          bigint
        constraint fkoe9unhvqxl96mbyxfn6tc0cxn
            references agenda,
    user_creator_id    bigint
        constraint fk5kj6u1cxa9c3uk2c7fs3m30uo
            references user_info
);

alter table files_to_send
    owner to postgres;

create table if not exists history_item
(
    id            bigint    not null
        constraint history_item_pkey
            primary key,
    version       integer default 0,
    message       varchar(255),
    new_state     integer,
    timestamp     timestamp not null,
    created_by_id bigint    not null
        constraint fk6332cbps51f00vp5e2qciw0o4
            references user_info,
    history_id    bigint
        constraint fklxc24xica6pqpahs8g4yejnh7
            references order_info,
    history_order integer
);

alter table history_item
    owner to postgres;

create table if not exists user_has_clients
(
    ouser_id  bigint not null
        constraint fkdi7c8a4uji0ynd9ns8hqxk28j
            references user_info,
    client_id bigint not null
        constraint fk5fcs83ur3041nhmr2rr895m8j
            references client,
    constraint user_has_clients_pkey
        primary key (ouser_id, client_id)
);

alter table user_has_clients
    owner to postgres;

create table if not exists user_has_roles
(
    ouser_id bigint not null
        constraint fk36ri0ayj10u6c08yrnn5u4kxm
            references user_info,
    orole_id bigint not null
        constraint fkk5de4vcivt462f5chq7w2axh2
            references orole,
    constraint user_has_roles_pkey
        primary key (ouser_id, orole_id)
);

alter table user_has_roles
    owner to postgres;

create table if not exists user_has_sids
(
    ouser_id    bigint not null
        constraint fkd1mw6cmjla0j1f2nxdj0iewq3
            references user_info,
    systemid_id bigint not null
        constraint fk8pal8juw4uq6vc25yua90uv9o
            references system_id,
    constraint user_has_sids_pkey
        primary key (ouser_id, systemid_id)
);

alter table user_has_sids
    owner to postgres;

create function insertintotempsms() returns trigger
    language plpgsql
as
$$
BEGIN
    INSERT INTO public.temp_sms(id,
                                carrier_char_code, datacoding, date, destination, iso2, message_type, messages_text,
                                msg_received, msg_sended, source, system_id)
    VALUES (nextval('hibernate_sequence'), NEW.carrier_char_code, NEW.datacoding, NEW.date, NEW.destination, NEW.iso2,
            NEW.message_type, NEW.messages_text, NEW.msg_received, NEW.msg_sended, NEW.source, NEW.system_id);

    RETURN new;
END;
$$;

alter function insertintotempsms() owner to postgres;

