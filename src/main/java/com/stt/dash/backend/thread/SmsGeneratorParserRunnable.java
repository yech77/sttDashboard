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
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

/**
 * Crea el archivo con el mensaje final que se va a enviar al usuario. Este archivo sirve de insumo para
 * el preparingSmsMessagge.
 */
public class SmsGeneratorParserRunnable implements Runnable {

    private static Logger log = LoggerFactory.getLogger(SmsGeneratorParserRunnable.class);

    //    @Autowired
//    private SendingSmsRepository sending_repo;
    public static String baseDirectory = "src/main/resources/base/";
//    public static String baseDirectory = "/home/yech/Documents/apache-tomcat-9.0.43/webapps/odashboard-1.0/WEB-INF/classes/base/";

    private FilesToSendService files_service;

    //    @Autowired
//    private OProperties properties;
    private final int updateInterval = 1000;

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
    private final String msg;
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
        msg = "[" + Application.getAPP_NAME() + "] [" + userEmail + "] [AGENDA] [" + agenda.getName() + "]";
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
        if (!isValidData()) return;
        log.info("[{}] [{}] UPDATING -> GENERATING_MESSAGES", getStringLog(), fileToSend.getFileName());
        fileToSend = updateFileToSend_status(Status.GENERATING_MESSAGES);
        int numLine = 0;
        StringBuilder sbLine = new StringBuilder(agenda.getFirstLine().length() * agenda.getItemCount());
        try {
            char separatorChar = '0';
            InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            /* Marcar el principio del archivo para luego regresarme. */
            br.mark(1);

            /* Buscar el primer caracter no numero de la primera columna para obtener el separador */
            while (Character.isDigit(separatorChar)) {
                separatorChar = (char) br.read();
            }

            /* TODO: Esto debe enviar error, tener un separador por defecto no parece ser util. VALIDAR. */
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
            /**/
            File targetFile = new File(baseDirectory + "/" + client + "/" + systemId + "/http/" + fileToSend.getId() + ".csv");
            log.info("[{}]  [{}] CREATING DIR", Application.getAPP_NAME(), targetFile.getAbsolutePath());
            targetFile.getParentFile().mkdirs();
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(targetFile.getAbsolutePath()));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            /* crea nuevas lineas  del archivo con el mensaje final */
            for (CSVRecord record : records) {
                numLine++;

                /* Este mensaje puede ser el mensaje con parametros o solo el mensaje */
                String newMsg = messagesText;

                /* agrega el numero de telefono */
                sbLine.append(record.get(0)).append(",");


                /* sustituye variables por parameros en todos el mensaje */
                newMsg = formatedSmsMsg(record, newMsg);

                /* Se encierra entre comillas por si viene con coma el msg */
                sbLine.append("\"").append(newMsg).append("\"").append('\n');
                csvPrinter.printRecord(record.get(0), newMsg);
                if (numLine % updateInterval == 0) {
                    fileToSend = updateFileToSend_numLine(numLine);
                    csvPrinter.flush();
                }
            }
            fileToSend.setNumGenerated(numLine);
            fileToSend.setSmsCount(numLine);
            fileToSend.setStatus(Status.PREPARING_SMS);
            log.info("[{}] [{}] UPDATING -> PREPARING_SMS", Application.getAPP_NAME(),
                    fileToSend.getFileName());
            fileToSend = files_service.updateState(currentUser, fileToSend);
            try {
                csvPrinter.flush();
                csvPrinter.close();
                log.info("[{}] [{}] FILE CREATED!", Application.getAPP_NAME(), targetFile.getAbsolutePath());
            } catch (IOException ex) {
                log.error("[{}] [{}] NOT CREATED!", Application.getAPP_NAME(), targetFile.getAbsolutePath());
                log.info("[{}] [{}] UPDATING -> INVALIDS", Application.getAPP_NAME(),
                        fileToSend.getFileName());
                fileToSend = updateFileToSend_status(Status.INVALID);
            }

        } catch (FileNotFoundException ex) {
            System.out.println("Error leyendo archivo. Cancelando proceso...");
            log.info("[{}] [{}] UPDATING -> INVALIDS", Application.getAPP_NAME(),
                    fileToSend.getFileName());
            fileToSend = updateFileToSend_status(Status.INVALID);
            log.error("", ex);
        } catch (IOException ex) {
            log.info("[{}] [{}] UPDATING -> INVALIDS", Application.getAPP_NAME(),
                    fileToSend.getFileName());
            fileToSend = updateFileToSend_status(Status.INVALID);
            log.error("", ex);
        }
    }

    private static String formatedSmsMsg(CSVRecord record, String newMsg) {
        if (record.size() > 2) {
            for (int i = 1; i < record.size(); i++) {
                newMsg = newMsg.replace("$" + i, record.get(i));
            }
        } else if (record.size() == 2) {
            newMsg = record.get(1);
        }
        return newMsg;
    }

    private boolean isValidData() {
        if (Objects.isNull(stream) || ObjectUtils.isEmpty(messagesText.length())) {
            log.info("[{}] [{}] El archivo o el mensaje esta vacio.", getStringLog(), fileToSend.getFileName());
            fileToSend = updateFileToSend_status(Status.INVALID);
            return false;
        }
        return true;
    }

    private FIlesToSend updateFileToSend_status(Status status) {
        fileToSend.setStatus(status);
        fileToSend = files_service.updateState(currentUser, fileToSend);
        return fileToSend;
    }

    private FIlesToSend updateFileToSend_numLine(int numLine) {
        fileToSend.setNumGenerated(numLine);
        fileToSend = files_service.updateState(currentUser, fileToSend);
        log.info("[{}] [{}] UPDATING -> NUMGENERATED", Application.getAPP_NAME(),
                fileToSend.getFileName());
        return fileToSend;
    }

    private String getStringLog() {
        return msg;
    }
}
