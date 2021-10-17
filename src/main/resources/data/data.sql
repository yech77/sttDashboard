
truncate table client cascade;
truncate table system_id cascade;

insert into public.client (id, client_cod, client_name, cuadrante, email)
values (30, 'BCOTEST', 'BANCO PRUEBA VZLA', 'ALIADO', 'corre@test.com'),
       (31, 'NETTEST', 'TELECOMUNICACION NET', 'ALIADO', 'itjoye@yahoo.com'),
       (32, 'INVJLC', 'INVERSIONES JLC 20-20, C.A', 'EMPRESAS', 'aarongonzalezv@hotmail.com'),
       (33, 'TESTGB01', 'GLERYXA J. BANDRES B, FP', 'EMPRESAS', 'gleryxab@gmail.com');
-- /********/
insert into public.system_id (id, payment_type, system_id, client_id)
values (40, 'POSTPAGO', 'BCOTEST04', 30),
       (41, 'POSTPAGO', 'BCOTEST05', 30),
       (42, 'POSTPAGO', 'BCOTEST06', 30),
       (43, 'POSTPAGO', 'INVJLC2020', 32),
       (44, 'PREPAGO', 'INVJLC2020_1', 32),
       (45, 'POSTPAGO', 'VXGJBB', 33),
       (46, 'POSTPAGO', 'VEGJBB', 33),
       (47, 'PREPAGO', 'VEGJBB_1', 33),
       (48, 'POSTPAGO', 'NETCCS01', 31),
       (49, 'POSTPAGO', 'NETBQTO01', 31),
       (50, 'POSTPAGO', 'NETMBO01', 31);
/****************/
insert into public.user_has_clients (ouser_id, client_id)
values  (51, 30),
        (51, 31),
        (51, 32),
        (51, 33),
        (52, 33),
        (52, 32),
        (52, 31),
        (52, 30),
        (53, 33),
        (53, 32),
        (53, 31),
        (53, 30),
        (54, 33),
        (54, 32),
        (54, 31),
        (54, 30),
        (2, 31),
        (8, 30);
-- /*******************/
insert into public.user_has_sids (ouser_id, systemid_id)
values  (2, 49),
        (2, 48),
        (2, 50),
        (4, 49),
        (4, 48),
        (4, 50),
        (6, 49),
        (8, 42),
        (8, 41),
        (8, 40),
        (10, 42),
        (10, 41),
        (10, 40);