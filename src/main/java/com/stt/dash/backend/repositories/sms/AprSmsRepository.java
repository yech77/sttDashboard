package com.stt.dash.backend.repositories.sms;

import com.stt.dash.backend.data.entity.sms.AprSms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface AprSmsRepository extends BaseSmsRepository<AprSms, Long> {
}
