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
values (1, 'Permite ver la Pantalla AUDITEVEN', 'UI_AUDIT'),
       (2, 'Permite ver la Pantalla ROL', 'UI_ROL'),
       (3, 'Permite ver la Pantalla USER', 'UI_USER'),
       (4, 'Permite ver la Pantalla OPERADORAS', 'UI_EVOLUTION_CARRIER'),
       (5, 'Permite ver la Pantalla ROL', 'UI_EVOLUTION_CLIENT'),
       (6, 'Permite ver la Pantalla USER', 'UI_EVOLUTION_SYSTEMID'),
       (7, 'Permite ver la Pantalla BUSCAR SMS', 'UI_SEARCH_SMS'),
       (8, 'Permite ver la Pantalla TRAFICO', 'UI_TRAFFIC_SMS'),
       (9, 'Permite ver la Pantalla AGENDA', 'UI_AGENDA_SMS'),
       (10, 'Permite ver la Pantalla MANEJO DE RECADOS', 'UI_PROGRAM_SMS'),
       (11, 'Permite ver el mensaje de text', 'VIEW_MSG_TEXT');
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
/******* USUARIOS **********/
insert into public.user_info (id, version, active, created_by, created_date, email, first_name, last_name, locked,
                              password_hash, role, user_type, user_type_ord, user_parent_id)
values (1, 0, true, null, '2021-07-23 15:00:53.987278', 'adminstt@soltextech.com', 'Name Admin', 'Last Admin', true,
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
