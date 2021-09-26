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
    private static String APP_NAME = "OPREP";
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
        log.info("[{}] Comenzando Ejecución", getAPP_NAME());
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void findNewFiles() {
        log.info("[{}] Looking for new files in {}", getAPP_NAME(), properties.getAgendafilePathBase());
        File base = new File(properties.getAgendafilePathBase());
        File[] clients = base.listFiles();
        if (clients == null || clients.length == 0) {
            log.info("NOT DIRECTORIES FOUND. CLIENT LEVEL [{}]", properties.getAgendafilePathBase());
            return;
        }
        for (File client : clients) {
            if (!client.isDirectory()) {
                log.info("IS NOT A DIRECTORY. CLIENT LEVEL [{}]", client.getAbsolutePath());
                continue;
            }
            File[] sys_ids = client.listFiles();
            if (sys_ids == null || sys_ids.length == 0) {
                log.info("NOT DIRECTORIES FOUND. SID LEVEL [{}]", properties.getAgendafilePathBase());
                continue;
            }
            for (File sys_id : sys_ids) {
                if (!sys_id.isDirectory()) {
                    log.info("IS NOT A DIRECTORY. SID LEVEL [{}]", client.getAbsolutePath());
                    continue;
                }
                File httpFolder = new File(sys_id.getAbsolutePath() + "/http");
                if (!httpFolder.exists()) {
                    log.warn("FOLDER DOES NOT EXIST [{}]", sys_id.getAbsolutePath() + "/http");
                    continue;
                }
                File[] smsFiles = httpFolder.listFiles();
                if (smsFiles != null && smsFiles.length > 0) {
                    for (File smsFile : smsFiles) {
                        File f = new File(sys_id.getAbsolutePath() + "/processing/" + smsFile.getName());
                        f.getParentFile().mkdirs();
                        log.info("DIRECTORY CREATED: {}", f.getAbsolutePath());
                        try {
                            log.info("{} MOVING FILE FROM {} TO {} ", smsFile.getAbsolutePath(), f.getAbsolutePath());
                            Files.move(
                                    Paths.get(smsFile.getAbsolutePath()),
                                    Paths.get(f.getAbsolutePath()));
                            log.info("[{}] SCHEDULING - [{}]", SmsPreparationManagerApplication.getAPP_NAME(), f.getAbsolutePath());
                            scheduler.schedule(
                                    new SmsFileParserProcessor(f, sending_repo, files_service),
                                    1, TimeUnit.SECONDS);
                            log.error("Moving file threw exception!");
                        } catch (Exception ex) {
                            log.error("", ex);
                        }
//                        if (!completedTasks.containsKey(smsFile.getAbsolutePath())) {
//                            if (!pendingTasks.containsKey(smsFile.getAbsolutePath()) && !completedTasks.containsKey(smsFile.getAbsolutePath())) {
//                                ScheduledFuture<?> fut = scheduler.schedule(
//                                        new SmsFileProcessor(smsFile, sending_repo, files_service),
//                                        1, TimeUnit.SECONDS);
//                                pendingTasks.put(smsFile.getAbsolutePath(), fut);
//                                System.out.println("[SmsPreparationManager] - New task scheduled for file: " + smsFile.getAbsolutePath());
//                            } else if (pendingTasks.get(smsFile.getAbsolutePath()).isDone()) {
//                                completedTasks.put(smsFile.getAbsolutePath(), pendingTasks.remove(smsFile.getAbsolutePath()));
//
//                                //smsFile.delete();
//                            }
//                        }

                    }
                }
            }
        }
        log.info("DONE. Looking for new files in {}.", properties.getAgendafilePathBase());
    }
//
//    @RequestMapping(value = "/createfile/{client}/{systemid}/{filename}/{size}")
//    public void hello(@PathVariable String client, @PathVariable String systemid,
//            @PathVariable String filename, @PathVariable int size) {
//        System.out.println("Creando archivo con " + size + " mensajes llamado " + filename + ".csv con el cliente " + client + "y el systemId " + systemid);
//        File targetFile = new File(properties.getAgendafilePathBase() + "/" + client + "/" + systemid + "/http/" + filename + ".csv");
//        try {
//            FileWriter fw = new FileWriter(targetFile);
//            for (int i = 0; i < size; i++) {
//                fw.write("" + (3000 + i) + "," + "Este es un mensaje de prueba." + "\n");
//            }
//            fw.close();
//            System.out.println("Archivo creado!");
//        } catch (IOException ex) {
//            System.out.println("Error creando archivo");
//        }
//
//    }

    //    @RequestMapping(value = "/progress")
//    public void hello2(@PathVariable String pendingtasks) {
//        System.out.println("Archivos detectados por procesar:");
//        Iterator it = pendingTasks.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry mapElement = (Map.Entry) it.next();
//            System.out.println(mapElement.getKey());
//        }
//        System.out.println("\nArchivos ya procesados:");
//        Iterator it2 = completedTasks.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry mapElement = (Map.Entry) it.next();
//            System.out.println(mapElement.getKey());
//        }
//    }
    public static String getAPP_NAME() {
        return APP_NAME;
    }

}
