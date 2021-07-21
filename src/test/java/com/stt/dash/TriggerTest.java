package com.stt.dash;

import com.github.javafaker.Faker;
import com.stt.dash.app.security.SecurityConfiguration;
import com.stt.dash.app.security.UserDetailsServiceImpl;
import com.stt.dash.backend.repositories.sms.JanSmsRepository;
import com.stt.dash.backend.repositories.sms.TempSmsRepository;
import com.stt.dash.backend.service.TempSmsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TriggerTest {
    @Autowired
    TempSmsRepository tempSmsRepository;
    @Autowired
    TempSmsService tempSmsService;

    @Test
    @DisplayName("Trigger de Jan hacia TempSms")
    @Sql({"/scripts/jan_sms.sql"})
    @Rollback(value = true)
    @Order(10)
    public void TriggrJan(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de Feb hacia TempSms")
    @Sql({"/scripts/feb_sms.sql"})
    @Rollback(value = true)
    @Order(20)
    public void TriggrFeb(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de Mar hacia TempSms")
    @Sql({"/scripts/mar_sms.sql"})
    @Rollback(value = true)
    @Order(30)
    public void TriggrMar(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de Apr hacia TempSms")
    @Sql({"/scripts/apr_sms.sql"})
    @Rollback(value = true)
    @Order(40)
    public void TriggrApr(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de May hacia TempSms")
    @Sql({"/scripts/may_sms.sql"})
    @Rollback(value = true)
    @Order(50)
    public void TriggrMay(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de Jun hacia TempSms")
    @Sql({"/scripts/jun_sms.sql"})
    @Rollback(value = true)
    @Order(60)
    public void TriggrJun(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de Jul hacia TempSms")
    @Sql({"/scripts/jul_sms.sql"})
    @Rollback(value = true)
    @Order(70)
    public void TriggrJul(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de Aug hacia TempSms")
    @Sql({"/scripts/aug_sms.sql"})
    @Rollback(value = true)
    @Order(80)
    public void TriggrAug(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de Sep hacia TempSms")
    @Sql({"/scripts/sep_sms.sql"})
    @Rollback(value = true)
    @Order(90)
    public void TriggrSep(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de Oct hacia TempSms")
    @Sql({"/scripts/oct_sms.sql"})
    @Rollback(value = true)
    @Order(100)
    public void TriggrOct(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de Nov hacia TempSms")
    @Sql({"/scripts/nov_sms.sql"})
    @Rollback(value = true)
    @Order(110)
    public void TriggrNov(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Trigger de Dic hacia TempSms")
    @Sql({"/scripts/dec_sms.sql"})
    @Rollback(value = true)
    @Order(120)
    public void TriggrDic(){
        long count = tempSmsRepository.count();
        tempSmsRepository.deleteAll();
        Assertions.assertEquals(1, count);
    }
    @Test
    @DisplayName("Resume TempSms")
    @Rollback(value = true)
    @Order(130)
    @Sql({"/scripts/doTestResume.sql"})
    public void TriggrResume(){
        long count = tempSmsRepository.count();
        Assertions.assertEquals(12, count, "No hay 12 registros en TempSms");
        tempSmsService.doResume();
        count = tempSmsRepository.count();
        Assertions.assertEquals(0, count, "Quedan registros en TempSms lugo dl resumen.");
        tempSmsRepository.deleteAll();
    }
}
