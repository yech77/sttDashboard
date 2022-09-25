package com.stt.smspreparationmanager;

import com.stt.smspreparationmanager.repository.SendingSmsRepository;
import com.stt.smspreparationmanager.runnable.SmsFileParserProcessor;
import com.stt.smspreparationmanager.service.FilesToSendService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;

@SpringBootApplication
@EnableScheduling
public class SmsPreparationManagerApplication {
    public static String APP_NAME = "OPREP";
    private static Logger log = LogManager.getLogger(SmsPreparationManagerApplication.class);
    // REPO
    @Autowired
    private SendingSmsRepository sending_repo;
    @Autowired
    private FilesToSendService files_service;
    @Autowired
    private OProperties properties;

    // THREAD POOL
    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    // CONSTANTES
//    public static String baseDirectory = "C:/Users/Enrique/Documents/NetBeansProjects/odashboard/src/main/resources/base/";
//    public static String baseDirectory = "/home/yech/Documents/apache-tomcat-9.0.43/webapps/odashboard-1.0/WEB-INF/classes/base/";
    public static String baseDirectory = "";
    //private List<ScheduledFuture<?>> pendingTasks = new ArrayList<>();
    //private List<String> pendingFiles = new ArrayList<>();
    public static ConcurrentHashMap<String, ScheduledFuture<?>> pendingTasks = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, ScheduledFuture<?>> completedTasks = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(SmsPreparationManagerApplication.class, args);
//        Clients.generateClients();
//        scheduler = Executors.newScheduledThreadPool(4);
        log.info("[{}] Comenzando Ejecución", APP_NAME);
    }

    @Scheduled(cron = "0 */1 * * * ?")
    /* TODO: Agregar ScheduledLock */
    public void findNewFiles() {
        log.info("[{}] Looking for new files in {}", APP_NAME, properties.getAgendafilePathBase());
        File base = new File(properties.getAgendafilePathBase());
//        @TODO: Nullpointer de base.
        File[] clients = base.listFiles();
        if (clients == null || clients.length == 0) {
            log.info("[{}] NOT DIRECTORIES FOUND. CLIENT LEVEL [{}]", APP_NAME, properties.getAgendafilePathBase());
            return;
        }
        /* Recorre los subdirectorios que representan a los clientes: CODPRO*/
        for (File client : clients) {
            if (!client.isDirectory()) {
                log.info("[{}] IS NOT A DIRECTORY. CLIENT LEVEL [{}]", APP_NAME, client.getAbsolutePath());
                continue;
            }
            File[] sys_ids = client.listFiles();
            if (sys_ids == null || sys_ids.length == 0) {
                log.info("[{}] NOT DIRECTORIES FOUND. SID LEVEL [{}]", APP_NAME, properties.getAgendafilePathBase());
                continue;
            }
            /* Recorre los subdirectorios que representan a los SystemIds: SYSTEMID */
            for (File sys_id : sys_ids) {
                if (!sys_id.isDirectory()) {
                    log.info("[{}] IS NOT A DIRECTORY. SID LEVEL [{}]", APP_NAME, client.getAbsolutePath());
                    continue;
                }
                File httpFolder = new File(sys_id.getAbsolutePath() + "/http");
                if (!httpFolder.exists()) {
                    log.warn("[{}] FOLDER DOES NOT EXIST [{}]", APP_NAME, sys_id.getAbsolutePath() + "/http");
                    continue;
                }
                File[] smsFiles = httpFolder.listFiles();
                if (smsFiles != null && smsFiles.length > 0) {
                    for (File smsFile : smsFiles) {
                        File f = new File(sys_id.getAbsolutePath() + "/processing/" + smsFile.getName());
                        f.getParentFile().mkdirs();
                        log.info(" [{}] DIRECTORY CREATED: {}", APP_NAME, f.getAbsolutePath());
                        try {
                            log.info("[{}] MOVING FILE FROM {} TO {} ", APP_NAME, smsFile.getAbsolutePath(), f.getAbsolutePath());
                            Files.move(
                                    Paths.get(smsFile.getAbsolutePath()),
                                    Paths.get(f.getAbsolutePath()));
                            log.info("[{}] SCHEDULING - [{}]", APP_NAME, f.getAbsolutePath());
                            scheduler.schedule(
                                    new SmsFileParserProcessor(f, sending_repo, files_service),
                                    2, TimeUnit.SECONDS);
                        } catch (Exception ex) {
                            log.error("Moving file threw exception!", ex);
                        }
                    }
                }
            }
        }
        log.info("[{}] DONE. Looking for new files in {}.", APP_NAME, properties.getAgendafilePathBase());
    }
    public static String getAPP_NAME() {
        return APP_NAME;
    }

}
