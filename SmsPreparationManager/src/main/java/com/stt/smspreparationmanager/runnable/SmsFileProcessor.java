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

/**
 *
 * @author Enrique
 */
public class SmsFileProcessor implements Runnable {

    private static Logger log = LogManager.getLogger(SmsFileProcessor.class);
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
    private final String regex = "^(58)(412|414|416|424|426)([0-9]{7})$";
    private final Pattern regexPattern = Pattern.compile(regex);

    public SmsFileProcessor(File smsFile, SendingSmsRepository sending_repo,
            FilesToSendService files_service) {
        this.smsFile = smsFile;
        this.sending_repo = sending_repo;
        this.files_service = files_service;
        filePath = smsFile.getAbsolutePath();
        //directory = filePath.split("\\");
        directory = filePath.split(Matcher.quoteReplacement(System.getProperty("file.separator")));
        datacoding = 3;
        source = "22800";
    }

    @Override
    public void run() {
        /*Dejar solo el nombre sin su extension que es el id. */
        String id = smsFile.getName().substring(0, smsFile.getName().length() - 4);
        try {
            log.info("[{}] BEGIN THREAD. FINDING ID [{}] [{}]", SmsPreparationManagerApplication.getAPP_NAME(), id, Arrays.toString(directory));
            fileToSend = files_service.findById(Long.parseLong(id));
            log.info("[{}] FOUND [{}] [{}]", SmsPreparationManagerApplication.getAPP_NAME(), fileToSend.getFilePath(), fileToSend.getFileName());
            stream = new FileInputStream(smsFile);
        } catch (FileNotFoundException ex) {
            log.error("", ex);
            return;
        } catch (NumberFormatException ex) {
            log.error("", ex);
            return;
        }
        log.info("[{}] BEGIN PROCESSING FILE: {}/{}", SmsPreparationManagerApplication.getAPP_NAME(), fileToSend.getFilePath(), fileToSend.getFileName());
        prepareSms();
        log.info("[{}] END PROCESSING FILE: {}/{}", SmsPreparationManagerApplication.getAPP_NAME(), fileToSend.getFilePath(), fileToSend.getFileName());
        log.info("[{}] END THREAD ID [{}]", SmsPreparationManagerApplication.getAPP_NAME(), id);
    }

    public void prepareSms() {
        if (stream == null) {
            log.error("El archivo es null.");
            return;
        }

        List<SendingSms> batch;
        BufferedReader reader;
        int msgPreparedCount = 0;
        int numLine = 1;
        try {

            
            fileToSend.setStatus(FilesToSend.Status.PREPARING_SMS);
            batch = new ArrayList<>();
            InputStreamReader isr = new InputStreamReader(stream,
                    StandardCharsets.UTF_8);
            reader = new BufferedReader(isr);

            String line = reader.readLine();
            int pos = -1;
            for (int i = 0; i < directory.length; i++) {
                if (directory[i].equals("processing")) {
                    pos = i - 1;
                }
            }
            if (pos == -1) {
                log.error("Problema analizando directorio; \"processing\" no existe");
                reader.close();
                return;
            }

            while (line != null) {
                SendingSms msg = new SendingSms();
                msg.setFileToSend(fileToSend);
                msg.setMessageType("MT");
                msg.setSource(source);

                msg.setDatacoding(datacoding);
                msg.setDate(fileToSend.getDateToSend());

                // TODO: FAKE VALUE
                iso2 = "VE";
                msg.setIso2(iso2);

                String[] variables = line.split(",");
                if (variables.length != 2) {
                    log.warn("Linea " + numLine + " tiene un formato desconocido.");
                    line = reader.readLine();
                    numLine++;
                    continue;
                }

                if (!validatePhone(variables[0])) {
                    log.warn("Numero de telefono en linea {" + numLine + "} es invalido.");
                    line = reader.readLine();
                    numLine++;
                    continue;
                }

                msg.setDestination(variables[0]);

                msg.setMessagesText(variables[1]);

                StringBuilder hashCode1 = new StringBuilder();
                StringBuilder hashCode2 = new StringBuilder();
                int ranSize1 = (int) (Math.random() * 11) + 10;
                int ranSize2 = (int) (Math.random() * 11) + 10;
                for (int j = 0; j < ranSize1; j++) {
                    int r = (int) (Math.random() * 36);
                    hashCode1.append(r >= 10 ? "" + ((char) (r + 87)) : r);
                }
                for (int j = 0; j < ranSize2; j++) {
                    int r = (int) (Math.random() * 36);
                    hashCode2.append(r >= 10 ? "" + ((char) (r + 87)) : r);
                }
                msg.setMsgSended(hashCode1.toString());
                msg.setMsgReceived(hashCode2.toString());

                // TODO: FAKE VALUE
                carrierCharCode = "charCodeFalso";
                msg.setCarrierCharCode(carrierCharCode);

                msg.setSystemId(directory[pos]);

                batch.add(msg);
                msgPreparedCount++;

                if (batch.size() >= batchSize) {
                    log.info("To save in DB..");
                    sending_repo.saveAll(batch);
                    log.info("saved in DB ({})..", numLine);
                    batch.clear();
                }

                line = reader.readLine();
                numLine++;
            }
            reader.close();
        } catch (Exception ex) {
            log.error("", ex);
            return;
        }

        if (!batch.isEmpty()) {
            sending_repo.saveAll(batch);
        }
        String newPath = smsFile.getParentFile().getParentFile().getAbsolutePath() + "/success/" + smsFile.getName();

        File newLoc = new File(newPath);
        log.info("[{}] CREATING DIRECTORY [{}]", SmsPreparationManagerApplication.getAPP_NAME(), newLoc.getParentFile());
        newLoc.getParentFile().mkdirs();
        try {
            log.info("[{}] MOVING FROM [{}] TO [{}]", SmsPreparationManagerApplication.getAPP_NAME(), smsFile.getAbsolutePath(), newLoc.getAbsolutePath());
            Path temp = Files.move(
                    Paths.get(smsFile.getAbsolutePath()),
                    Paths.get(newLoc.getAbsolutePath()));
            log.info("Archivo se ha movido a la carpeta [success].");
        } catch (IOException ex) {
            log.error("", ex);
        }
        fileToSend.setBeingProcessed(false);
        fileToSend.setReadyToSend(true);
        fileToSend.setStatus(FilesToSend.Status.WAITING_TO_SEND);
//        log.info("[{}] UPDATING BEING PROCESSED->FALSE AND READY TO SEND->TRUE: [{}]", SmsPreparationManagerApplication.getAPP_NAME(), newLoc.getAbsolutePath());
        files_service.save(fileToSend);
        log.info("[{}] MOVED AND WAITING TO SEND : [{}]", SmsPreparationManagerApplication.getAPP_NAME(), newLoc.getAbsolutePath());
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
