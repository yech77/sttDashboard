/*
  - Realiza el Drop cascade de todas las tablas
  - Inserta data base en las tablas:
    - Authority
    - Roles
    - Users
    - Operadoras
    - y sus relaciones.
 */

/* BASE: Obtenida de otro sitio */
truncate table client cascade;
truncate table system_id cascade;
truncate table odash_conf cascade;
/* BASE */
truncate table oauthority cascade;
truncate table orole cascade;
truncate table user_info cascade;
truncate table carrier cascade;
/* BULK */
truncate table agenda cascade;
truncate table files_to_send cascade;
truncate table sending_sms cascade;
/* AUDITORIA */
truncate table odash_audit_event cascade;
/* SMS */
truncate table jan_sms;
truncate table feb_sms;
truncate table mar_sms;
truncate table apr_sms;
truncate table may_sms;
truncate table jun_sms;
truncate table jul_sms;
truncate table aug_sms;
truncate table sep_sms;
truncate table oct_sms;
truncate table nov_sms;
truncate table dec_sms;
truncate table temp_sms;
truncate table sms_hour;
/* Reinicializar la secuencia */
ALTER SEQUENCE hibernate_sequence RESTART WITH 100;
/*********************************/
insert into public.oauthority (id, version, auth_desc, auth_name)
values (1, 0, 'Auditoria', 'UI_AUDIT'),
       (2, 0, 'Roles', 'UI_ROL'),
       (3, 0, 'Usuarios', 'UI_USER'),
       (4, 0, 'Evolución Operadora', 'UI_EVOLUTION_CARRIER'),
       (5, 0, 'Evolución Cliente', 'UI_EVOLUTION_CLIENT'),
       (6, 0, 'Evolución Credenciales', 'UI_EVOLUTION_SYSTEMID'),
       (7, 0, 'Búsqueda de mensaje', 'UI_SEARCH_SMS'),
       (8, 0, 'Tráfico por Cliente', 'UI_TRAFFIC_SMS'),
       (9, 0, 'Crear Masivos', 'UI_AGENDA_SMS'),
       (10, 0, 'Programar Masivos', 'UI_PROGRAM_SMS'),
       (11, 0, 'Ver mensaje de Texto', 'VIEW_MSG_TEXT'),
       (12, 0, 'Dashboard', 'UI_DASHBOARD'),
       (13, 0, 'Permisos', 'UI_PERMISSIONS'),
       (14, 0, 'Balance', 'UI_BALANCE');
/*********/
insert into public.orole (id, version, rol_name)
values (20, 0, 'AGENDAR_SMS'),
       (21, 0, 'AUDITORIA'),
       (22, 0, 'BUSQUEDA_SMS'),
       (23, 0, 'EVOLUCION_CLIENTE'),
       (24, 0, 'EVOLUCION_OPERADORA'),
       (25, 0, 'EVOLUCION_PASAPORTES'),
       (26, 0, 'PROGRAMAR_SMS'),
       (27, 0, 'ROLES'),
       (28, 0, 'TRAFICO_SMS'),
       (29, 0, 'USUARIOS'),
       (30, 0, 'VER_MSGTEXT'),
       (31, 0, 'PERMISOS'),
       (32, 0, 'rol-yecheverria@soltextech.com');

/*********/
insert into public.role_has_authority (orole_id, oauthority_id)
values (20, 9),
       (21, 1),
       (29, 3),
       (27, 2),
       (22, 7),
       (23, 5),
       (24, 4),
       (25, 6),
       (26, 10),
       (28, 8),
       (30, 11),
       (31, 13),
       (32, 1),
       (32, 13),
       (32, 14),
       (32, 12),
       (32, 11),
       (32, 10),
       (32, 9),
       (32, 8),
       (32, 7),
       (32, 6),
       (32, 5),
       (32, 4),
       (32, 3),
       (32, 2);
/******* OPERADORAS **********/
insert into public.carrier (id, carrier_charcode, carrier_name, country_iso2)
values (1, 'MOVILNET', 'MOVILNET, C.A', 'VE'),
       (2, 'DIGITEL', 'DIGITEL, C.A', 'VE'),
       (3, 'MOVISTAR', 'MOVISTAR, C.A', 'VE');
/******* USUARIOS **********/
insert into public.user_info (id, version, active, created_by, created_date, email,
                              first_name, last_name, locked, password_hash, role,
                              user_type, user_type_ord, user_parent_id)
values (1, 0, true, null, '2021-07-23 15:00:53.987278', 'adminstt@soltextech.com',
        'Name Admin', 'Last Admin', true,
        '$2a$10$KcQI.OQnX3/obH8W0/X8weVVoK2A/GPW1SK6EveNRODDfXb0rmpzm', null, 1, 0, null);
insert into public.user_info (id, version, active, created_by, created_date, email, first_name, last_name, locked,
                              password_hash, role, user_type, user_type_ord, user_parent_id)
values (2, 0, true, 'adminstt@soltextech.com', '2023-03-09 17:04:33.272108', 'gbandres@soltextech.com', 'Gleryxa',
        'Bandes', false, '$2a$10$8BxSBQ.6Zt.YJONWFiLFiuo2PH7MiUbknbUfbXIEQfiidtG/Cf05u', null, 1, 0, 1);
insert into public.user_info (id, version, active, created_by, created_date, email, first_name, last_name, locked,
                              password_hash, role, user_type, user_type_ord, user_parent_id)
values (3, 0, true, 'adminstt@soltextech.com', '2023-03-09 17:13:07.781157', 'enavas@soltextech.com', 'Elizabeth',
        'Navas', false, '$2a$10$HnezPllTBww6PVW2fVa20.ZLtwcg342rDsij6Golotv/GYKKShIz.', null, 1, 0, 1);
insert into public.user_info (id, version, active, created_by, created_date, email, first_name, last_name, locked,
                              password_hash, role, user_type, user_type_ord, user_parent_id)
values (4, 0, true, 'adminstt@soltextech.com', '2023-03-09 17:12:12.103042', 'dsolorzano@soltextech.com', 'Denny',
        'Solorzano', false, '$2a$10$6jxcKQoBgYhAH82p1yETk.OANiUbNjGpHapi4AkLZkr1DsjOPbUsu', null, 1, 0, 1);
insert into public.user_info (id, version, active, created_by, created_date, email, first_name, last_name, locked,
                              password_hash, role, user_type, user_type_ord, user_parent_id)
values (5, 0, true, 'adminstt@soltextech.com', '2023-03-09 17:16:01.087207', 'yecheverria@soltextech.com', 'Yermi',
        'Echeverria', false, '$2a$10$A12a4uG8HM7cQKTdXPT3/u53OUNj0PrxNxJmJvbVKQowdWema2eyq', null, 1, 0, 1);

insert into public.user_info (id, version, active, created_by, created_date, email, first_name, last_name, locked,
                              password_hash, role, user_type, user_type_ord, user_parent_id)
values (6, 1, true, 'adminstt@soltextech.com', '2023-03-10 01:02:25.096307', 'lsuarez@soltextech.com',
        'Luis', 'Suarez', false, '$2a$10$9YokIhhpcBaV.9utwt6Qru7aV7F7JWQ5IUP9ZkHqJxS7Sd8gyst42', null, 1, 0, 1);
/* Roles */
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 30),
       (1, 29),
       (1, 28),
       (1, 27),
       (1, 26),
       (1, 25),
       (1, 24),
       (1, 23),
       (1, 22),
       (1, 21),
       (1, 20),
       (2, 32),
       (3, 32),
       (4, 32),
       (5, 32),
       (6, 32);