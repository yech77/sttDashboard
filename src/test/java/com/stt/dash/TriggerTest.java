package com.stt.dash;

import com.stt.dash.backend.repositories.sms.TempSmsRepository;
import com.stt.dash.backend.service.TempSmsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

//@SpringBootTest(
//        properties = {"spring.datasource.url=jdbc:postgresql://localhost:5432/onlyfortesting"})
/* En caso de que use un properties especifico para las pruebas. Debe estar dentro de /test/resources */
//@TestPropertySource(locations = "classpath:db-test.properties")
@DataJpaTest(properties = {"spring.datasource.url=jdbc:postgresql://localhost:5432/onlyfortesting"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
@Sql({"/scripts/schema.sql"})
@Import(value = {TempSmsService.class})
@Rollback
@Transactional
public class TriggerTest {
    @Autowired
    Environment environment;
    @Autowired
    TempSmsRepository tempSmsRepository;
    @Autowired
    TempSmsService tempSmsService;

    @Test
    @DisplayName("Trigger de Jan hacia TempSms")
    @Order(10)
    @Sql({"/scripts/jan_sms.sql"})
    public void TriggrJan() {
        Arrays.stream(environment.getDefaultProfiles()).forEach(System.out::println);
        System.out.println("Ambiente:  *********** ************** ************** **************** ");
        Arrays.stream(environment.getActiveProfiles()).forEach(System.out::println);
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de Feb hacia TempSms")
    @Sql({"/scripts/feb_sms.sql"})
    @Order(20)
    public void TriggrFeb() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de Mar hacia TempSms")
    @Sql({"/scripts/mar_sms.sql"})
    @Rollback(value = true)
    @Order(30)
    public void TriggrMar() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de Apr hacia TempSms")
    @Sql({"/scripts/apr_sms.sql"})
    @Rollback(value = true)
    @Order(40)
    public void TriggrApr() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de May hacia TempSms")
    @Sql({"/scripts/may_sms.sql"})
    @Rollback(value = true)
    @Order(50)
    public void TriggrMay() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de Jun hacia TempSms")
    @Sql({"/scripts/jun_sms.sql"})
    @Rollback(value = true)
    @Order(60)
    public void TriggrJun() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de Jul hacia TempSms")
    @Sql({"/scripts/jul_sms.sql"})
    @Rollback(value = true)
    @Order(70)
    public void TriggrJul() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de Aug hacia TempSms")
    @Sql({"/scripts/aug_sms.sql"})
    @Rollback(value = true)
    @Order(80)
    public void TriggrAug() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de Sep hacia TempSms")
    @Sql({"/scripts/sep_sms.sql"})
    @Rollback(value = true)
    @Order(90)
    public void TriggrSep() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de Oct hacia TempSms")
    @Sql({"/scripts/oct_sms.sql"})
    @Rollback(value = true)
    @Order(100)
    public void TriggrOct() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de Nov hacia TempSms")
    @Sql({"/scripts/nov_sms.sql"})
    @Rollback(value = true)
    @Order(110)
    public void TriggrNov() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Trigger de Dic hacia TempSms")
    @Sql({"/scripts/dec_sms.sql"})
    @Rollback(value = true)
    @Order(120)
    public void TriggrDic() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("Resume TempSms")
    @Rollback(value = true)
    @Order(130)
    @Sql({"/scripts/doTestResume.sql"})
    @Disabled("Hay que cambiar el archivo a data existente")
    public void TriggrResume() {
        long count = tempSmsRepository.count();
        Assertions.assertEquals(12, count, "No hay 12 registros en TempSms");
        tempSmsService.doResume();
        count = tempSmsRepository.count();
        Assertions.assertEquals(0, count, "Quedan registros en TempSms lugo dl resumen.");
    }
}
