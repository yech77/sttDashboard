-- liquibase formatted sql

-- changeset liquibase:5
create index jan_sms_date_system_id_index
    on jan_sms (date desc, system_id asc);

create index feb_sms_date_system_id_index
    on feb_sms (date desc, system_id asc);

create index mar_sms_date_system_id_index
    on mar_sms (date desc, system_id asc);

create index apr_sms_date_system_id_index
    on apr_sms (date desc, system_id asc);

create index may_sms_date_system_id_index
    on may_sms (date desc, system_id asc);

create index jun_sms_date_system_id_index
    on jun_sms (date desc, system_id asc);

create index jul_sms_date_system_id_index
    on jul_sms (date desc, system_id asc);

create index aug_sms_date_system_id_index
    on aug_sms (date desc, system_id asc);

create index sep_sms_date_system_id_index
    on sep_sms (date desc, system_id asc);

create index oct_sms_date_system_id_index
    on oct_sms (date desc, system_id asc);

create index nov_sms_date_system_id_index
    on nov_sms (date desc, system_id asc);

create index dec_sms_date_system_id_index
    on dec_sms (date desc, system_id asc);

create index sms_hour_year_month_day_hour_system_id_index
    on sms_hour (year desc, month desc, day desc, hour asc, system_id asc);