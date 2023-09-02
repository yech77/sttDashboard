package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.sms.TempSms;
import com.stt.dash.backend.repositories.sms.TempSmsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TempSmsService {
    private final static Logger log = LoggerFactory.getLogger(TempSmsService.class);
    private TempSmsRepository temp_repo;

    public TempSmsService(@Autowired TempSmsRepository temp_repo) {
        this.temp_repo = temp_repo;
    }

    @Transactional
    public void doResume() {
        log.info("[SEARCHING LAST SMS]");
        TempSms t = temp_repo.findFirst1ByOrderByIdDesc();
        if (t == null) {
            log.info("[NO-SMS-TO-SUM]");
            return;
        }
        log.info("[{}] - [{}]. DOING SUM TOT...", t.getId(), t.getDate());
        temp_repo.insertResume(t.getId());
        log.info("[{}] - [{}]. SUM TOT DONE. DELETING SMS RESUMED...", t.getId(), t.getDate());
        temp_repo.deleteByIdIsLessThanEqual(t.getId());
        log.info("[{}] - [{}]. DELETED.", t.getId(), t.getDate());
    }
}
