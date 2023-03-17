/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager.runnable;

import com.stt.smspreparationmanager.SmsPreparationManagerApplication;
import com.stt.smspreparationmanager.entity.FilesToSend;
import com.stt.smspreparationmanager.entity.SendingSms;
import com.stt.smspreparationmanager.repository.SendingSmsRepository;
import com.stt.smspreparationmanager.service.FilesToSendService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.stt.smspreparationmanager.SmsPreparationManagerApplication.APP_NAME;

/**
 * Crear las líneas del archivo recibido, creado por Dashboard,  en la tabla SendingSms.
 *
 * @author Enrique
 */
public class SmsFileParserProcessor implements Runnable {

    private static Logger log = LogManager.getLogger(SmsFileParserProcessor.class);
    private FilesToSendService files_service;

    private SendingSmsRepository sending_repo;

    private final int batchSize = 25;
    private File smsFile;
    private InputStream stream;
    private FilesToSend fileToSend;
    private final int datacoding;
    private String carrierCharCode;
    private String source;
    private String iso2;
    private String filePath;
    private String[] directory;
    /* TODO: Llamar al servicio */
    private final String regex = "^(58)(412|414|416|424|426)([0-9]{7})$";
    private final Pattern regexPattern = Pattern.compile(regex);

    public SmsFileParserProcessor(File smsFile, SendingSmsRepository sending_repo,
                                  FilesToSendService files_service) {
        this.smsFile = smsFile;
        this.sending_repo = sending_repo;
        this.files_service = files_service;
        filePath = smsFile.getAbsolutePath();
        directory = filePath.split(Matcher.quoteReplacement(System.getProperty("file.separator")));
        datacoding = 3;
        source = "22800";
    }

    @Override
    public void run() {
        try {
            /*Dejar solo el nombre sin su extension que es el id. */
            String id = smsFile.getName().substring(0, smsFile.getName().length() - 4);
            fileToSend = files_service.findById(Long.parseLong(id));
            log.info("[{}] [{}] BEGIN THREAD. FINDING ID [{}] [{}]", APP_NAME, fileToSend.getOrderName(), id, Arrays.toString(directory));
            /* TODO: Tomar en cuneta que no se encuentra el archivo. */
            log.info("[{}] [{}] FOUND [{}] [{}]", APP_NAME, fileToSend.getOrderName(), fileToSend.getFilePath(), fileToSend.getFileName());
            stream = new FileInputStream(smsFile);
            log.info("[{}] [{}] BEGIN PROCESSING FILE", APP_NAME, fileToSend.getOrderName());
            prepareSms();
            log.info("[{}] [{}] END PROCESSING FILE", APP_NAME, fileToSend.getOrderName());
            log.info("[{}] [{}] [{}] END THREAD ID [{}]", APP_NAME, fileToSend.getOrderName(), id);
        } catch (FileNotFoundException ex) {
            log.error("", ex);
        } catch (NumberFormatException ex) {
            log.error("", ex);
        }catch (Exception ex) {
            log.error("", ex);
        }
    }

    public void prepareSms() {
        if (stream == null) {
            log.error("[{}] [{}] [{}] El archivo es null.", APP_NAME, fileToSend.getOrderName(), smsFile.getName());
            return;
        }

        List<SendingSms> batch;
        BufferedReader reader;
        int msgPreparedCount = 0;
        int numLine = 1;
        try {
            fileToSend.setStatus(FilesToSend.Status.PREPARING_SMS);
            batch = new ArrayList<>();
            int pos = -1;
            for (int i = 0; i < directory.length; i++) {
                if (directory[i].equals("processing")) {
                    pos = i - 1;
                }
            }
            if (pos == -1) {
                log.error("[{}] Problema analizando directorio; \"processing\" no existe", APP_NAME);
                return;
            }

            InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
            /* LEER EL ARCHIVO HECHO POR ODAHS */
            Iterable<CSVRecord> records = CSVFormat.newFormat(',')
                    .withQuote('"')
                    .withIgnoreEmptyLines(true)
                    .parse(new BufferedReader(isr));

            log.info("[{}] [{}] [{}] PARSING CSV", APP_NAME, fileToSend.getOrderName(), fileToSend.getFileName());
            for (CSVRecord record : records) {
                SendingSms msg = new SendingSms();
                msg.setFileToSend(fileToSend);
                msg.setMessageType("MT");
                msg.setSource(source);
                msg.setDatacoding(datacoding);
                msg.setDate(fileToSend.getDateToSend());
                msg.setIso2("VE");
                msg.setDestination(record.get(0));
                msg.setMessagesText(record.get(1));
                msg.setMsgSended("");
                msg.setMsgReceived("");
                msg.setCarrierCharCode("DAHS");
                /* TODO: Puedo colocar este valor en una variable */
                msg.setSystemId(directory[pos]);
                batch.add(msg);
                msgPreparedCount++;

                log.info("KEEPED [{}] ", msg.toString());
                if (batch.size() >= batchSize) {
                    log.info("To save in DB..");
                    sending_repo.saveAll(batch);
                    log.info("[{}] [{}] saved in DB ({})..", APP_NAME, fileToSend.getOrderName(), numLine);
                    batch.clear();
                }
                /* Todo puede ser un acumulativo de batch.size() */
                numLine++;
            }
            log.info("[{}] [{}] [{}] Cerrando Arhivo", APP_NAME, fileToSend.getOrderName(), smsFile.getName());
            isr.close();
        } catch (Exception ex) {
            log.error("", ex);
            return;
        }

        if (!batch.isEmpty()) {
            sending_repo.saveAll(batch);
            log.info("[{}] [{}] saved in DB ({})..", APP_NAME, fileToSend.getOrderName(), numLine);
            batch.clear();
        }
        String newPath = smsFile.getParentFile().getParentFile().getAbsolutePath() + "/success/" + smsFile.getName();

        File newLoc = new File(newPath);
        log.info("[{}] [{}] [{}] CREATING DIRECTORY", APP_NAME, fileToSend.getOrderName(), newLoc.getParentFile());
        newLoc.getParentFile().mkdirs();
        try {
            log.info("[{}] [{}] MOVING FROM [{}] TO [{}]", APP_NAME, fileToSend.getOrderName(), smsFile.getAbsolutePath(), newLoc.getAbsolutePath());
            Path temp = Files.move(
                    Paths.get(smsFile.getAbsolutePath()),
                    Paths.get(newLoc.getAbsolutePath()));
            log.info("[{}] [{}] MOVED TO DIRECTORY [success]. [{}]", APP_NAME, fileToSend.getOrderName(), smsFile.getName());
        } catch (IOException ex) {
            log.error("", ex);
        }
        fileToSend.setBeingProcessed(false);
        fileToSend.setReadyToSend(true);
        fileToSend.setStatus(FilesToSend.Status.WAITING_TO_SEND);
        files_service.save(fileToSend);
        log.info("[{}] [{}] MOVED AND WAITING TO SEND : [{}]", APP_NAME, fileToSend.getOrderName(), newLoc.getAbsolutePath());
    }

    @Deprecated
    public boolean validatePhone(String number) {
        return true;
    }

    public boolean validatePhoneRegex(String number) {
        Matcher m = regexPattern.matcher(number);
        return m.matches();
    }
}
