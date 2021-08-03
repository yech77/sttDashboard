package com.stt.dash;

import com.stt.dash.backend.repositories.SmsHourRepository;
import com.stt.dash.backend.repositories.sms.AugSmsRepository;
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
public class SearchSmsTest {
    @Autowired
    Environment environment;
    @Autowired
    TempSmsRepository tempSmsRepository;
    @Autowired
    TempSmsService tempSmsService;
    @Autowired
    AugSmsRepository augSmsRepository;
    @Autowired
    SmsHourRepository smsHourRepository;

    @Test
    @DisplayName("Busqueda Sms August")
    @Order(10)
    @Sql({"/data/data.sql", "/data/aug_sms.sql"})
    public void AugustTest() {
        Assertions.assertNotEquals(0, augSmsRepository.count());
        tempSmsService.doResume();
        Assertions.assertNotEquals(0, smsHourRepository.count());

    }
}
