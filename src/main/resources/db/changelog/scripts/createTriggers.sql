/*******
Crea SOLO los triggers.
 ****/
create or replace function insertintotempsms() returns trigger
    language plpgsql
as
'
    BEGIN
        INSERT INTO public.temp_sms(id,
                                    carrier_char_code, datacoding, date, destination, iso2, message_type, messages_text, msg_received, msg_sended, source, system_id)
        VALUES (nextval(''hibernate_sequence''), NEW.carrier_char_code, NEW.datacoding, NEW.date, NEW.destination, NEW.iso2, NEW.message_type, NEW.messages_text, NEW.msg_received, NEW.msg_sended, NEW.source, NEW.system_id);

        RETURN new;
    END;
';

alter function insertintotempsms() owner to postgres;


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: jan_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on jan_sms;
create trigger trigger_insert_tempsms
    after insert
    on jan_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: feb_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on feb_sms;
create trigger trigger_insert_tempsms
    after insert
    on feb_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: mar_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on mar_sms;
create trigger trigger_insert_tempsms
    after insert
    on mar_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: apr_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on apr_sms;
create trigger trigger_insert_tempsms
    after insert
    on apr_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: may_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on may_sms;
create trigger trigger_insert_tempsms
    after insert
    on may_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: jun_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on jun_sms;
create trigger trigger_insert_tempsms
    after insert
    on jun_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: jul_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on jul_sms;
create trigger trigger_insert_tempsms
    after insert
    on jul_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: aug_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on aug_sms;
create trigger trigger_insert_tempsms
    after insert
    on aug_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: sep_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on sep_sms;
create trigger trigger_insert_tempsms
    after insert
    on sep_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: oct_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on oct_sms;
create trigger trigger_insert_tempsms
    after insert
    on oct_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: nov_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on nov_sms;
create trigger trigger_insert_tempsms
    after insert
    on nov_sms
    for each row
execute procedure insertintotempsms();


/******
Crea el trigger que llama a la funcion para grabar el SMS insertado en el Mes: dec_sms
en la tabla tempSms.
*******/
drop trigger if exists trigger_insert_tempsms on dec_sms;
create trigger trigger_insert_tempsms
    after insert
    on dec_sms
    for each row
execute procedure insertintotempsms();
