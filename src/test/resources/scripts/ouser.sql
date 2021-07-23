insert into public.ouser (id, user_email, user_lastname, user_name, user_password, user_type, user_parent_id, user_type_ord, user_status, created_by, created_date)
values  (15, 'enavas@soltextech.com', 'Navas', 'Elizabeth', 'enavas', 'HAS', null, 0, null, null, null),
        (17, 'lsuarez@soltextech.com', 'Suarez', 'Luis', 'lsuarez', 'HAS', 15, 0, null, null, null),
        (19, 'bdvlastname@soltextech.com', 'bdv_lastname', 'bdv_namee', 'bdvlastname', 'IS', 16, 1, null, null, null),
        (12448, 'carmenlsilva@gmail.com', 'Silva', 'Carmen Luisa', '1234', 'IS', 15, 1, null, null, null),
        (12530, 'gabybandres@gmail.com', 'Bandres', 'Gabriela', '9876', 'IS', 16, 1, null, null, null),
        (12626, 'userye@gmail.com', 'ye', 'userye', 'userye', 'BY', 12530, 2, 0, null, null),
        (12578, 'USUARIO1@GMAIL.COM', 'AP', 'USUARIO1', '123456', 'BY', 12530, 3, null, null, null),
        (18, 'dsolorzano@soltextech.com', 'Solorzano', 'Denny', 'dsolorzano', 'HAS', 15, 0, 0, null, null),
        (16, 'gbandres@soltextech.com', 'Bandres', 'Gleryxa', 'gbandres', 'HAS', 15, 0, 0, null, null),
        (14714, 'empresauno@empresauno.com', 'UNO', 'Empresa UNO', 'empresauno', 'IS', 16, 1, 0, 'gbandres@soltextech.com', '2021-05-12 11:08:51.150000'),
        (12787, 'usergb@gmail.com', 'GB', 'UserGB', '112233', 'BY', 12530, 3, 1, null, null);