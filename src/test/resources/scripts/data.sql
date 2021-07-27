/*
  - Realiza el Drop cascade de todas las tablas
  - Inserta data base en todas las tablas y crea la relacion de data en las que se necesitan: (user has systemid, etc.)
 */
/* BASE */
truncate table oauthority cascade;
truncate table orole cascade;
truncate table client cascade;
truncate table system_id cascade;
truncate table user_info cascade;
/* BULK */
truncate table agenda cascade;
truncate table files_to_send cascade;
truncate table sending_sms cascade;
/* AUDITORIA */
truncate table odash_audit_event cascade;
/* SMS */
truncate table  jan_sms;
truncate table  feb_sms;
truncate table  mar_sms;
truncate table  apr_sms;
truncate table  may_sms;
truncate table  jun_sms;
truncate table  jul_sms;
truncate table  aug_sms;
truncate table  sep_sms;
truncate table  oct_sms;
truncate table  nov_sms;
truncate table  dec_sms;
truncate table  temp_sms;
truncate table  sms_hour;
/*********************************/
insert into public.oauthority (id, auth_desc, auth_name)
values  (12335, 'Permite ver la Pantalla AUDITEVEN', 'UI_AUDIT'),
        (12336, 'Permite ver la Pantalla ROL', 'UI_ROL'),
        (12337, 'Permite ver la Pantalla USER', 'UI_USER'),
        (12338, 'Permite ver la Pantalla AUDITEVEN', 'UI_EVOLUTION_CARRIER'),
        (12339, 'Permite ver la Pantalla ROL', 'UI_EVOLUTION_CLIENT'),
        (12340, 'Permite ver la Pantalla USER', 'UI_EVOLUTION_SYSTEMID'),
        (12341, 'Permite ver la Pantalla USER', 'UI_SEARCH_SMS'),
        (12342, 'Permite ver la Pantalla TRAFICO', 'UI_TRAFFIC_SMS'),
        (12343, 'Permite ver la Pantalla AGENDA', 'UI_AGENDA_SMS'),
        (12344, 'Permite ver la Pantalla MANEJO DE RECADOS', 'UI_PROGRAM_SMS');
/*********/
insert into public.orole (id, rol_name)
values  (12345, 'AGENDAR_SMS'),
        (12346, 'AUDITORIA'),
        (12347, 'BUSQUEDA_SMS'),
        (12348, 'EVOLUCION_CLIENTE'),
        (12349, 'EVOLUCION_OPERADORA'),
        (12350, 'EVOLUCION_PASAPORTES'),
        (12351, 'PROGRAMAR_SMS'),
        (12352, 'ROLES'),
        (12353, 'TRAFICO_SMS'),
        (12354, 'USUARIOS');
/*********/
insert into public.role_has_authority (orole_id, oauthority_id)
values  (12345, 12343),
        (12346, 12335),
        (12347, 12341),
        (12348, 12339),
        (12349, 12338),
        (12350, 12340),
        (12351, 12344),
        (12352, 12336),
        (12353, 12342),
        (12354, 12337);
/*********/
insert into public.client (id, client_cod, client_name, cuadrante, email)
values  (43, 'TEST00', 'NOMBRE TEST00', 'ALIADO', 'corre@test.com'),
        (45, 'TEST02', 'RAZON TEST02', 'ALIADO', 'itjoye@yahoo.com'),
        (53, 'TEST03', 'INVERSIONES JLC 20-20, C.A', 'EMPRESAS', 'aarongonzalezv@hotmail.com'),
        (59, 'TESTGB01', 'GLERYXA J. BANDRES B, FP', 'EMPRESAS', 'gleryxab@gmail.com');
/********/
insert into public.system_id (id, payment_type, system_id, client_id)
values  (44, 'POSTPAGO', 'CRETEST00', 43),
        (50, 'POSTPAGO', 'CRETESTALL', 43),
        (52, 'POSTPAGO', 'CRESTTEST00_1', 43),
        (54, 'POSTPAGO', 'INVJLC2020', 53),
        (55, 'PREPAGO', 'INVJLC2020_1', 53),
        (60, 'POSTPAGO', 'VXGJBB', 59),
        (61, 'POSTPAGO', 'VEGJBB', 59),
        (62, 'PREPAGO', 'VEGJBB_1', 59);
/********/
insert into public.user_info (id, version, active, created_by, created_date, email, first_name, last_name, locked, password_hash, role, user_type, user_type_ord, user_parent_id)
values  (38625, 1, true, 'enavas@soltextech.com', '2021-07-02 08:29:13.318805', 'audit5@stt.com', 'testaudit555555', 'last5', false, '$2a$10$pzG0mDumnr6pkmKQbeELaOo3dWu9/JiEo0mARXGuAbs7nUrZe9reK', null, 0, 1, null),
        (38629, 0, true, null, null, 'barista@vaadin.com', 'Malin', 'Castro', true, '$2a$10$94h5eGdmUX9yVvfDqPvae.RKDqeNjZIuxZyweDteQin.zuYVpilHS', 'barista', null, null, 894148),
        (38630, 0, true, null, null, 'admin@vaadin.com', 'Göran', 'Rich', true, '$2a$10$WrK.rWMQmDKuhfzsqxt2POmj/3ZQzJ0W3HyjE2KhC4BCchLunoByq', 'admin', null, null, 894148),
        (38631, 0, true, null, null, 'peter@vaadin.com', 'Peter', 'Bush', false, '$2a$10$w2v37i4UheSYrkhwC0lBsue4IIRhlmPTkhcPJb7EIUHqZPVyYZtZu', 'barista', null, null, 894148),
        (38632, 0, true, null, null, 'mary@vaadin.com', 'Mary', 'Ocon', true, '$2a$10$5kt.6WIfGlhPCWZ7MIyH6eb15LMYakOcadzkSOPGo47hpMvwMWlgO', 'baker', null, null, 894148),
        (38628, 1, true, null, null, 'baker@vaadin.com', 'Heidi', 'Carter', false, '$2a$10$ds0m1ClQcLAjY4x3Dufh8OqnpmN5A.hYWb1GDMey9B4m5xnuj2UaK', 'baker', 1, 0, 894148),
        (894148, 12, true, null, null, 'gbandres@soltextech.com', 'Gleryxa', 'Bandres', false, '$2a$10$C7WYdJ3ccxzTswN83rp3R./uNyfNzYRxR/hX3qreKIgs3V2K4C1Wq', null, 1, 0, 894148),
        (894004, 2, true, 'gbandres@soltextech.com', '2021-06-21 08:54:56.477942', 'yechev@gmail.com', 'Yermiii', 'Echeverriaaaa', false, '$2a$10$U/mMyAs0wD2rgQb3ENvsxu51FkHORr98/0bZkAmDwHNlFHHnDZfd2', null, 1, 0, 894148),
        (38627, 4, true, null, null, 'dsolorzano@soltextech.com', 'Denny', 'Solorzano', false, '$2a$10$MMWWj77Hu1gTuJOV/aK1euRlshRUKhTuPA36JTB34EWGItyxEVF9G', null, 1, 0, 894148);
/********************/
insert into user_has_roles (ouser_id, orole_id)
values (894148, 12345), (894148, 12346), (894148, 12347), (894148, 12348),
       (894148, 12349), (894148, 12350),
       (894148, 12351),(894148, 12352), (894148, 12353), (894148, 12354);
/********************/
insert into user_has_clients (ouser_id, client_id)
VALUES (894148, 43), (894148, 45), (894148, 53), (894148, 59);
/*******************/
insert into user_has_sids (ouser_id, systemid_id)
VALUES (894148, 44),(894148, 50),(894148, 52),(894148, 54),(894148, 55),(894148, 60),(894148, 61),(894148, 62);
