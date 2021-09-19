/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smstransfertoqueue;

import com.stt.smstransfertoqueue.entity.FilesToSend;
import com.stt.smstransfertoqueue.repository.SendingSmsRepository;
import com.stt.smstransfertoqueue.runnable.SmsSenderRunnable;
import com.stt.smstransfertoqueue.service.FilesToSendService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Enrique
 */
@SpringBootApplication
//@RestController
@EnableScheduling
public class SmsTransferToQueueApplication extends SpringBootServletInitializer {
    public static final int FILE_TO_SEND_POOL_SIZE = 4;
    private static String APP_NAME = "OTRANSF";
    private static Logger log = LogManager.getLogger(SmsTransferToQueueApplication.class);
    // REPO
    @Autowired
    private SendingSmsRepository sending_repo;
    @Autowired
    private FilesToSendService files_service;
    @Autowired
    private OProperties p;

    // THREAD POOL, Ejecuta un maximo de 4 archivos a la vez.
    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(FILE_TO_SEND_POOL_SIZE);
    public static void main(String[] args) {
        SpringApplication.run(SmsTransferToQueueApplication.class, args);
        log.info("[{}] - Comenzando Ejecución", getAPP_NAME());
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void checkRepo() {
        log.info("[{}] LOOKING FOR FILES TO SEND", getAPP_NAME());
        List<FilesToSend> filesToSendList = files_service.getUnsentOrders(LocalDateTime.now());
        int numberOfFilesToSend = filesToSendList.size();
        if (numberOfFilesToSend == 0) {
            log.info("[{}] NO FILES TO SEND ", getAPP_NAME());
            return;
        }
        log.info("[{}] FILES TO SEND FOUND: {}", getAPP_NAME(), numberOfFilesToSend);
        int posOfFileToSend = 0;
        FilesToSend file = filesToSendList.get(posOfFileToSend);
        while (file.getStatus() == FilesToSend.Status.INVALID) {
            posOfFileToSend++;
            if (posOfFileToSend < numberOfFilesToSend) {
                file = filesToSendList.get(posOfFileToSend);
            } else {
                log.info("No hay recados validos. Cancelando proceso...");
                return;
            }
        }
        try {
            scheduler.schedule(new SmsSenderRunnable(file, sending_repo, files_service, p),
                    1, TimeUnit.SECONDS);
            log.info("[{}] SCHEDULED TO SEND FILE ID [{}] ORDER NAME [{}]. FILE NAME [{}]",
                    getAPP_NAME(),
                    file.getFileId(),
                    file.getOrderName(),
                    file.getFileName());
            /* TODO: esta actualizacion debe realizar en el hilo que que se ejecuta. */
            file.setBeingProcessed(true);
            files_service.save(file);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static String getAPP_NAME() {
        return APP_NAME;
    }
}
