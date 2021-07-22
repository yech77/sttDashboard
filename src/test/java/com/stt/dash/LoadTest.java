package com.stt.dash;

import com.stt.dash.backend.repositories.sms.TempSmsRepository;
import com.stt.dash.backend.service.TempSmsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoadTest {
    @Autowired
    TempSmsService tempSmsService;
    @Autowired
    TempSmsRepository tempSmsRepository;

    //    @Test
//    @DisplayName("Trigger de Jan hacia TempSms")
//    @Sql({"/scripts/jun_sms_23k.sql", "/scripts/jul_sms_10k.sql", "/scripts/may_sms_17k.sql"})
//    @Rollback(value = true)
//    @Order(10)
    @Test
    @DisplayName("Trigger de Jan hacia TempSms")
    @Sql(value = {"/scripts/oauthority.sql", "/scripts/orole.sql", "/scripts/client.sql",
    "/scripts/system_id.sql", "/scripts/user_info.sql"})
    @Rollback(value = true)
    @Order(10)
    public void TriggrJan(){
        long count = tempSmsRepository.count();
        Assertions.assertEquals(50000, count);
    }
}
