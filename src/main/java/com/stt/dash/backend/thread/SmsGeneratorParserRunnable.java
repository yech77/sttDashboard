package com.stt.dash.backend.thread;

import com.stt.dash.Application;
import com.stt.dash.app.OProperties;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Status;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.FilesToSendService;
import com.stt.dash.backend.util.AgendaFileUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class SmsGeneratorParserRunnable implements Runnable {

    private static Logger log = LoggerFactory.getLogger(SmsGeneratorParserRunnable.class);

    //    @Autowired
//    private SendingSmsRepository sending_repo;
    public static String baseDirectory = "src/main/resources/base/";
//    public static String baseDirectory = "/home/yech/Documents/apache-tomcat-9.0.43/webapps/odashboard-1.0/WEB-INF/classes/base/";

    private FilesToSendService files_service;

    //    @Autowired
//    private OProperties properties;
    private final int updateInterval = 500;

    private FIlesToSend fileToSend;
    private InputStream stream;
    private String systemId;
    private String messagesText;
    private String client;
    private Agenda agenda;
    /**/
    private final String userEmail;
    private final User currentUser;

    private Date date;
    private int batchSize;
    private String iso2;
    private String carrierCharCode;
    private String source;
    private int datacoding;

    public SmsGeneratorParserRunnable(OProperties properties,
                                      FilesToSendService files_service,
                                      FIlesToSend fileToSend,
                                      Agenda agenda,
                                      String systemId,
                                      String messagesText,
                                      String client,
                                      String userEmail,
                                      User currentUser) {
        this.files_service = files_service;
        this.fileToSend = fileToSend;
        this.agenda = agenda;
        this.stream = AgendaFileUtils.getFileAsStream(this.agenda.getFileName());
        this.systemId = systemId;
        this.messagesText = messagesText;
        this.client = client;
        baseDirectory = properties.getAgendafilePathBase();
        this.userEmail = userEmail;
        this.currentUser = currentUser;
        log.info("[{}] [{}] BASE DIRECTORY", getStringLog(), baseDirectory);
    }

    @Override
    public void run() {

        generateSms();

    }

    // TODO: Validar # de telefono
    public boolean validatePhone(String number) {
        return true;
    }

    public void generateSms() {
        if (stream == null || messagesText.length() == 0) {
            log.info("[{}] [{}] El archivo o el mensaje esta vacio.", getStringLog(), fileToSend.getFileName());
            fileToSend.setStatus(Status.INVALID);
//            fileToSend = files_service.save(fileToSend, userEmail);
            fileToSend = files_service.updateState(currentUser, fileToSend);
            return;
        }
        fileToSend.setStatus(Status.GENERATING_MESSAGES);
        log.info("[{}] [{}] UPDATING -> GENERATING_MESSAGES", getStringLog(),
                fileToSend.getFileName());
//        fileToSend = files_service.save(fileToSend, userEmail);
        fileToSend = files_service.updateState(currentUser, fileToSend);
        int numLine = 0;
        StringBuilder sbLine = new StringBuilder();
        try {
            char separatorChar = '0';
            InputStreamReader isr = new InputStreamReader(stream,
                    StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            /* Marcar el principio del archivo para luego regresarme. */
            br.mark(1);
            /* Buscar el primer caracter no numero de la primera columna para obtener el separador */
            while (Character.isDigit(separatorChar)) {
                separatorChar = (char) br.read();
                log.info("{} PASING CHAR '{}'", getStringLog(), separatorChar);
            }
            /* Asignar separador por defecto. */
            if (separatorChar != ';' && separatorChar != ',' && separatorChar != '|') {
                separatorChar = ',';
                log.info("{} SEPARATOR NOT FOUND. ASSIGNED ['{}']", getStringLog(), separatorChar);
            }
            log.info("{} SEPARATOR FOUND ['{}']", getStringLog(), separatorChar);
            /* Ir al inicio marcado */
            br.reset();
            /* READER */
            Iterable<CSVRecord> records = CSVFormat.newFormat(separatorChar)
                    .withQuote('"')
                    .withIgnoreEmptyLines(true)
                    .parse(br);
            log.info("[{}] [{}] PARSING CSV", getStringLog(), fileToSend.getFileName());
            for (CSVRecord record : records) {
                numLine++;
                String newMsg = messagesText;
                /* agrega el numero de telefono */
                sbLine.append(record.get(0)).append(",");
                if (record.size() > 2) {
                    /* sustituye variables por parameros */
                    for (int i = 1; i < record.size(); i++) {
                        newMsg = newMsg.replace("$" + i, record.get(i));
                    }
                }
                /* Agrega la linea para el archivo */
                if (newMsg.contains(",")) {
                    sbLine.append("\"").append(newMsg).append("\"");
                } else {
                    sbLine.append(newMsg);
                }
                sbLine.append('\n');
                System.out.println(newMsg);
                if (numLine % updateInterval == 0) {
                    fileToSend.setNumGenerated(numLine);
                    log.info("[{}] [{}] UPDATING -> NUMGENERATED", Application.getAPP_NAME(),
                            fileToSend.getFileName());
//                    fileToSend = files_service.save(fileToSend, userEmail);
                    fileToSend = files_service.updateState(currentUser, fileToSend);
                }
            }

            fileToSend.setNumGenerated(numLine);
            fileToSend.setSmsCount(numLine);
            fileToSend.setStatus(Status.PREPARING_SMS);
            log.info("[{}] [{}] UPDATING -> PREPARING_SMS", Application.getAPP_NAME(),
                    fileToSend.getFileName());
//            fileToSend = files_service.save(fileToSend, userEmail);
            fileToSend = files_service.updateState(currentUser, fileToSend);

            File targetFile = new File(baseDirectory + "/" + client + "/" + systemId + "/http/" + fileToSend.getId() + ".csv");

            log.info("[{}]  [{}] CREATING DIR", Application.getAPP_NAME(),
                    targetFile.getAbsolutePath());
            targetFile.getParentFile().mkdirs();
            try {
                FileWriter fw = new FileWriter(targetFile);
                fw.write(sbLine.toString());
                fw.close();
                log.info("[{}] [{}] FILE CREATED!", Application.getAPP_NAME(), targetFile.getAbsolutePath());
            } catch (IOException ex) {
                log.error("[{}] [{}] NOT CREATED!", Application.getAPP_NAME(), targetFile.getAbsolutePath());
                log.info("[{}] [{}] UPDATING -> INVALIDS", Application.getAPP_NAME(),
                        fileToSend.getFileName());
                fileToSend.setStatus(Status.INVALID);
//                fileToSend = files_service.save(fileToSend, userEmail);
                fileToSend = files_service.updateState(currentUser, fileToSend);
            }

        } catch (FileNotFoundException ex) {
            System.out.println("Error leyendo archivo. Cancelando proceso...");
            log.info("[{}] [{}] UPDATING -> INVALIDS", Application.getAPP_NAME(),
                    fileToSend.getFileName());
            fileToSend.setStatus(Status.INVALID);
//            fileToSend = files_service.save(fileToSend, userEmail);
            fileToSend = files_service.updateState(currentUser, fileToSend);
            log.error("", ex);
        } catch (IOException ex) {
            log.info("[{}] [{}] UPDATING -> INVALIDS", Application.getAPP_NAME(),
                    fileToSend.getFileName());
            fileToSend.setStatus(Status.INVALID);
//            fileToSend = files_service.save(fileToSend, userEmail);
            fileToSend = files_service.updateState(currentUser, fileToSend);
            log.error("", ex);
        }
    }

    private String getStringLog() {
        return "[" + Application.getAPP_NAME() + "] [" + userEmail + "] [AGENDA] [" + agenda.getName() + "]";
    }
}
