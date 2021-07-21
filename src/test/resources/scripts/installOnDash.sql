create function insertintotempsms() returns trigger
    language plpgsql
as
$$
BEGIN
INSERT INTO public.temp_sms(id,
	carrier_char_code, datacoding, date, destination, iso2, message_type, messages_text, msg_received, msg_sended, source, system_id)
	VALUES (nextval('hibernate_sequence'), NEW.carrier_char_code, NEW.datacoding, NEW.date, NEW.destination, NEW.iso2, NEW.message_type, NEW.messages_text, NEW.msg_received, NEW.msg_sended, NEW.source, NEW.system_id);

    RETURN new;
END;
$$;

alter function insertintotempsms() owner to postgres;

create trigger trigger_insert_tempsms
    after insert
    on jan_sms
    for each row
execute procedure insertintotempsms();

