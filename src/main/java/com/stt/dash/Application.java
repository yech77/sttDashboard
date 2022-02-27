package com.stt.dash;

import com.stt.dash.app.security.SecurityConfiguration;
import com.stt.dash.backend.data.ClientCopycatDTO;
import com.stt.dash.backend.data.SystemIdCopycatDTO;
import com.stt.dash.backend.data.bean.SyncDTO;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.UserRepository;
import com.stt.dash.backend.service.TempSmsService;
import com.stt.dash.backend.service.UserService;
import com.stt.dash.backend.util.ws.SyncClientWebClient;
import com.stt.dash.backend.util.ws.SyncDataOWebClient;
import com.stt.dash.backend.util.ws.SyncSystemIdWebClient;
import com.stt.dash.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

/**
 * Spring boot web application initializer.
 */
@SpringBootApplication(scanBasePackageClasses = {SecurityConfiguration.class, MainView.class, Application.class,
        UserService.class}, exclude = ErrorMvcAutoConfiguration.class)
@EnableJpaRepositories(basePackageClasses = {UserRepository.class})
@EntityScan(basePackageClasses = {User.class})
@EnableScheduling
public class Application extends SpringBootServletInitializer {
    @Autowired
    TempSmsService temp_serv;

    public static WebClient webClient;

    private final static String ORINOCO_HOST = "http://localhost:8081";

    private static void builderWebclient(String host) {
        webClient = WebClient.builder()
                .baseUrl(host)
                .defaultHeaders(header -> header.setBasicAuth("orinoco", "0R1n0coRIv3r$"))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private static String APP_NAME = "ODASH";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        builderWebclient(ORINOCO_HOST);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Scheduled(cron = "0 */1 * * * ?")
    /* TODO: Agregar ScheduledLock */
    public void runResumeSms() {
        temp_serv.doResume();
    }

    @Scheduled(cron = "0 */1 * * * ?")
    /* TODO: Agregar ScheduledLock */
    public void runSyncData() {
        SyncSystemIdWebClient syncSystemIdWebClient = new SyncSystemIdWebClient(webClient, SystemIdCopycatDTO.class);
        SyncClientWebClient syncClientWebClient = new SyncClientWebClient(webClient, ClientCopycatDTO.class);
        /* TODO: Buscar los ultimos ids buscados en conf */
        try {
            Flux<SystemIdCopycatDTO> syncDTOMono = syncSystemIdWebClient.callSyncData(0);
            Flux<ClientCopycatDTO> syncCliDTO = syncClientWebClient.callSyncData(0);
            syncCliDTO.doOnTerminate(() -> {
                syncDTOMono.subscribe(s -> {
                    System.out.println("Este: " + s.getSystemId());
                });
            }).doOnEach(s -> {
                if (s.hasValue()) {
                    System.out.println("On: " + s.get().getClientName());
                }
            }).subscribe();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAPP_NAME() {
        return APP_NAME;
    }
}
