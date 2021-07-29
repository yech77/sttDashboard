
/* BASE */
truncate table oauthority cascade;
truncate table orole cascade;
truncate table client cascade;
truncate table system_id cascade;
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
ALTER SEQUENCE hibernate_sequence RESTART WITH 1;