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
insert into public.oauthority (id, auth_desc, auth_name)
values (1, 'Auditoria', 'UI_AUDIT'),
       (2, 'Permisos', 'UI_ROL'),
       (3, 'Usuarios', 'UI_USER'),
       (4, 'Evolución Operadora', 'UI_EVOLUTION_CARRIER'),
       (5, 'Evolución Cliente', 'UI_EVOLUTION_CLIENT'),
       (6, 'Evolución Credenciales', 'UI_EVOLUTION_SYSTEMID'),
       (7, 'Búsqueda de mensaje', 'UI_SEARCH_SMS'),
       (8, 'Tráfico por Cliente', 'UI_TRAFFIC_SMS'),
       (9, 'Crear Masivos', 'UI_AGENDA_SMS'),
       (10, 'Programar Masivos', 'UI_PROGRAM_SMS'),
       (11, 'Ver mensaje de Texto', 'VIEW_MSG_TEXT'),
       (12, 'Dashboard', 'UI_DASHBOARD');
/*********/
insert into public.orole (id, rol_name)
values (20, 'AGENDAR_SMS'),
       (21, 'AUDITORIA'),
       (22, 'BUSQUEDA_SMS'),
       (23, 'EVOLUCION_CLIENTE'),
       (24, 'EVOLUCION_OPERADORA'),
       (25, 'EVOLUCION_PASAPORTES'),
       (26, 'PROGRAMAR_SMS'),
       (27, 'ROLES'),
       (28, 'TRAFICO_SMS'),
       (29, 'USUARIOS'),
       (30, 'VER_MSGTEXT');
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
       (30, 11);
/******* OPERADORAS **********/
insert into public.carrier (id, carrier_charcode, carrier_name, country_iso2)
values (1, 'MOVILNET', 'MOVILNET, C.A', 'VE'),
       (2, 'DIGITEL', 'DIGITEL, C.A', 'VE'),
       (3, 'MOVISTAR', 'MOVISTAR, C.A', 'VE');
/******* USUARIOS **********/insert into public.user_info (id, version, active, created_by, created_date, email,
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
values (6, 1, true, 'yecheverria@soltextech.com', '2023-03-10 01:02:25.096307', 'adminempresaa@soltextech.com',
        'AdminEmpresaA', '', false, '$2a$10$9YokIhhpcBaV.9utwt6Qru7aV7F7JWQ5IUP9ZkHqJxS7Sd8gyst42', null, 0, 1, 5);
insert into public.user_info (id, version, active, created_by, created_date, email, first_name, last_name, locked,
                              password_hash, role, user_type, user_type_ord, user_parent_id)
values (7, 0, true, 'yecheverria@soltextech.com', '2023-03-10 01:04:35.328847', 'empresaa@soltextech.com', 'EmpreA01',
        '', false, '$2a$10$avbngBul8J3MoZUXG0YsJ.0X3pArXn8hH1vtkHpYOYYvmLn2rw/P2', null, 2, 2, 6);
insert into public.user_info (id, version, active, created_by, created_date, email, first_name, last_name, locked,
                              password_hash, role, user_type, user_type_ord, user_parent_id)
values (8, 1, true, 'yecheverria@soltextech.com', '2023-03-10 01:19:05.888815', 'adminempresab@soltextech.com',
        'AdminEmpresaB', '', false, '$2a$10$RFxsAh6WV3SL4lWNvVEWDu.1Ufju7qt31nsNpkE9c3kDXuNdZRP/S', null, 0, 1, 5);

/* Roles */
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 30);
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 29);
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 28);
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 27);
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 26);
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 25);
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 24);
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 23);
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 22);
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 21);
insert into public.user_has_roles (ouser_id, orole_id)
values (1, 20);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 30);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 29);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 28);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 27);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 26);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 25);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 24);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 23);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 22);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 21);
insert into public.user_has_roles (ouser_id, orole_id)
values (2, 20);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 30);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 29);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 28);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 27);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 26);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 25);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 24);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 23);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 22);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 21);
insert into public.user_has_roles (ouser_id, orole_id)
values (3, 20);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 30);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 29);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 28);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 27);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 26);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 25);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 24);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 23);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 22);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 21);
insert into public.user_has_roles (ouser_id, orole_id)
values (4, 20);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 30);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 29);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 28);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 27);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 26);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 25);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 24);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 23);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 22);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 21);
insert into public.user_has_roles (ouser_id, orole_id)
values (5, 20);
insert into public.user_has_roles (ouser_id, orole_id)
values (6, 30);
insert into public.user_has_roles (ouser_id, orole_id)
values (6, 29);
insert into public.user_has_roles (ouser_id, orole_id)
values (6, 28);
insert into public.user_has_roles (ouser_id, orole_id)
values (6, 26);
insert into public.user_has_roles (ouser_id, orole_id)
values (6, 25);
insert into public.user_has_roles (ouser_id, orole_id)
values (6, 24);
insert into public.user_has_roles (ouser_id, orole_id)
values (6, 23);
insert into public.user_has_roles (ouser_id, orole_id)
values (6, 22);
insert into public.user_has_roles (ouser_id, orole_id)
values (6, 21);
insert into public.user_has_roles (ouser_id, orole_id)
values (6, 20);
insert into public.user_has_roles (ouser_id, orole_id)
values (7, 30);
insert into public.user_has_roles (ouser_id, orole_id)
values (7, 28);
insert into public.user_has_roles (ouser_id, orole_id)
values (7, 26);
insert into public.user_has_roles (ouser_id, orole_id)
values (7, 25);
insert into public.user_has_roles (ouser_id, orole_id)
values (7, 24);
insert into public.user_has_roles (ouser_id, orole_id)
values (7, 23);
insert into public.user_has_roles (ouser_id, orole_id)
values (7, 22);
insert into public.user_has_roles (ouser_id, orole_id)
values (7, 20);
insert into public.user_has_roles (ouser_id, orole_id)
values (8, 30);
insert into public.user_has_roles (ouser_id, orole_id)
values (8, 29);
insert into public.user_has_roles (ouser_id, orole_id)
values (8, 28);
insert into public.user_has_roles (ouser_id, orole_id)
values (8, 26);
insert into public.user_has_roles (ouser_id, orole_id)
values (8, 25);
insert into public.user_has_roles (ouser_id, orole_id)
values (8, 24);
insert into public.user_has_roles (ouser_id, orole_id)
values (8, 23);
insert into public.user_has_roles (ouser_id, orole_id)
values (8, 22);
insert into public.user_has_roles (ouser_id, orole_id)
values (8, 21);
insert into public.user_has_roles (ouser_id, orole_id)
values (8, 20);