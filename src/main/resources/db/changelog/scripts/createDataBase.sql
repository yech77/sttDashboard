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
truncate client cascade;
truncate system_id cascade;
/* BASE */
truncate table oauthority cascade;
truncate table orole cascade;
truncate table user_info cascade;
truncate table carrier cascade;
/* BULK */
truncate table agenda cascade;
truncate table files_to_send cascade;
-- truncate table sending_sms cascade;
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
/******* USUARIOS **********/
insert into public.user_info (id, version, active, created_by, created_date, email, first_name, last_name, locked,
                              password_hash, role, user_type, user_type_ord, user_parent_id)
values (51, 0, true, null, '2021-07-23 15:00:53.987278', 'adminstt@soltextech.com', 'Name Admin', 'Last Admin', true,
        '$2a$10$KcQI.OQnX3/obH8W0/X8weVVoK2A/GPW1SK6EveNRODDfXb0rmpzm', null, 1, 0, null),
       (52, 0, true, 'adminstt@soltextech.com', '2021-07-23 15:03:17.433041', 'gbandres@soltextech.com', 'Gleryxa',
        'Bandres', false, '$2a$10$niedArOXTyf7tEHYogfs4.Ku.WYyn4a2MSdb7Dr7I/b7KSBhhZaOG', null, 1, 0, 51),
       (53, 0, true, 'adminstt@soltextech.com', '2021-07-23 18:12:12.353113', 'enavas@soltextech.com', 'Elizabeth',
        'Navas', false, '$2a$10$HcyK0gjx7yGXjD/DWcG.xeT/9zxcTSHL99CqwJfgPWspjTNizxzKe', null, 1, 0, 51),
       (54, 0, true, 'adminstt@soltextech.com', '2021-07-23 18:15:34.585924', 'dsolorzano@soltextech.com', 'Denny',
        'Solorzano', false, '$2a$10$szMOmQz7j9nMnK4b8tJQsOhiGtAjwDGdkxwfC7SsTinw/BzdHkmzO', null, 1, 0, 51),
       (2, 0, true, 'gbandres@soltextech.com', '2021-07-26 07:44:12.040646', 'adminnet@soltextech.com', 'Adminnet', '',
        false, '$2a$10$.U25bW8rfnewSXxqXrM/ae21.SZzg0FZaEc4tkwVJTyz7wo7ZlsAq', null, 0, 1, 52),
       (6, 0, true, 'gbandres@soltextech.com', '2021-07-26 07:49:19.554942', 'userbqtnet@soltextech.com', 'userbqtnet',
        '', false, '$2a$10$jhksTEd4bMaqhj3UNZvTq.R4miVDU0gYFamgQuifEW4RdzXGJNkNq', null, 2, 3, 4),
       (8, 0, true, 'gbandres@soltextech.com', '2021-07-26 07:52:32.954888', 'adminbco@soltextech.com', 'adminbco', '',
        false, '$2a$10$rRnHHr6loJ.sYJn95fkx5O..IL9BLfepA6OPrpCXhzYa7pjsHh4CS', null, 0, 1, 52),
       (10, 0, true, 'gbandres@soltextech.com', '2021-07-26 07:54:13.572442', 'userbco01@soltextech.com', 'UserBco01',
        '', false, '$2a$10$4SZUy.lXlRMsGohF0ptFoOTX8ZJu5GmXzoxEDbjBqJeV2LhaZBT/.', null, 2, 3, 8),
       (4, 0, true, 'gbandres@soltextech.com', '2021-07-26 07:47:21.076028', 'empresanet@soltextech.com', 'empresanet',
        '', false, '$2a$10$G4bSEx9q.Kz1qp0wqJP1WemqnxC.I1VcIsrvsprscDBnmNgY5U2We', null, 2, 2, 2);/**********/
insert into public.user_has_roles (ouser_id, orole_id)
values (51, 30),
       (51, 29),
       (51, 28),
       (51, 27),
       (51, 26),
       (51, 25),
       (51, 24),
       (51, 23),
       (51, 22),
       (51, 21),
       (51, 20),
       (52, 30),
       (52, 29),
       (52, 28),
       (52, 27),
       (52, 26),
       (52, 25),
       (52, 24),
       (52, 23),
       (52, 22),
       (52, 21),
       (52, 20),
       (53, 30),
       (53, 29),
       (53, 27),
       (53, 28),
       (53, 26),
       (53, 25),
       (53, 24),
       (53, 23),
       (53, 21),
       (53, 22),
       (53, 20),
       (54, 30),
       (54, 29),
       (54, 27),
       (54, 28),
       (54, 26),
       (54, 25),
       (54, 24),
       (54, 23),
       (54, 21),
       (54, 22),
       (54, 20),
       (2, 30),
       (2, 29),
       (2, 28),
       (2, 26),
       (2, 25),
       (2, 24),
       (2, 23),
       (2, 22),
       (2, 21),
       (2, 20),
       (4, 30),
       (4, 29),
       (4, 28),
       (4, 26),
       (4, 25),
       (4, 24),
       (4, 23),
       (4, 22),
       (4, 20),
       (6, 30),
       (6, 28),
       (6, 26),
       (6, 25),
       (6, 24),
       (6, 22),
       (6, 20),
       (8, 30),
       (8, 29),
       (8, 28),
       (8, 26),
       (8, 25),
       (8, 24),
       (8, 23),
       (8, 22),
       (8, 21),
       (8, 20),
       (10, 30),
       (10, 29),
       (10, 28),
       (10, 26),
       (10, 25),
       (10, 24),
       (10, 23),
       (10, 22),
       (10, 20);
-- OPERADORAS
insert into public.carrier (id, carrier_charcode, carrier_name, country_iso2)
values (1, 'MOVILNET', 'MOVILNET, C.A', 'VE'),
       (2, 'DIGITEL', 'DIGITEL, C.A', 'VE'),
       (3, 'MOVISTAR', 'MOVISTAR, C.A', 'VE');