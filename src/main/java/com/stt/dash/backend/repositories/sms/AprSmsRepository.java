package com.stt.dash.backend.repositories.sms;

import com.stt.dash.backend.data.entity.sms.AprSms;
import liquibase.pro.packaged.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface AprSmsRepository extends BaseSmsRepository<AprSms, Long> {
//    Page<AprSms> findByDateBetweenAndMessageTypeInAndSystemIdInAndDestinationEquals(Date dateStart, Date dateEnd, Collection<String> messageTypes, Collection<String> systemIds, String destination, Pageable pageable);

}
