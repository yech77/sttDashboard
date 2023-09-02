/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smstransfertoqueue.runnable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.msv.orinocosms.bean.AbstractSMSDTO;
import com.stt.smstransfertoqueue.OProperties;
import com.stt.smstransfertoqueue.entity.FilesToSend;
import com.stt.smstransfertoqueue.entity.SendingSms;
import com.stt.smstransfertoqueue.messagequeue.MQHandler;
import com.stt.smstransfertoqueue.service.FilesToSendService;
import com.stt.smstransfertoqueue.service.SendingSmsService;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.jms.JMSException;
import javax.jms.ResourceAllocationException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.stt.smstransfertoqueue.SmsTransferToQueueApplication.getAPP_NAME;

/**
 * @author Enrique
 */
//@SuppressWarnings("DuplicatedCode")
public class SmsSenderThread extends Thread {

    /*Log*/
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(SmsSenderThread.class);

    /*Properties File*/
    private static Properties prop;

    /*Message Queues y sus numbres*/
    private MQHandler mainA;

    @Autowired
    private SendingSmsService sendingSmsService;

    @Autowired
    private FilesToSendService files_service;

    private final OProperties p;

    private FilesToSend filesToSend;

    private final Gson gson = new Gson();
    Type gsonType = new TypeToken<HashMap<Integer, Integer>>() {
    }.getType();

    public SmsSenderThread(FilesToSend filesToSend,
                           SendingSmsService sendingSmsService,
                           FilesToSendService files_service,
                           OProperties p) {
        this.filesToSend = filesToSend;
        this.sendingSmsService = sendingSmsService;
        this.files_service = files_service;
        this.p = p;
    }

    @Override
    public void run() {

        try {
            log.info("[{}] [{}] BEGIN THREAD. ID [{}] FILE NAME [{}]", getAPP_NAME(),
                    filesToSend.getOrderName(),
                    filesToSend.getFileId(),
                    filesToSend.getFileName());
            mainA = new MQHandler(p);
            mainA.createSender();
            log.info("[{}] [{}] FILE ID [{}] UPDATING TO STATUS->SENDING", getAPP_NAME(),
                    filesToSend.getOrderName(),
                    filesToSend.getFileId());
            filesToSend.setStatus(FilesToSend.Status.SENDING);
            files_service.save(filesToSend);

            log.info("[{}] [{}] FILE ID [{}] FINDING SMS TO SEND", getAPP_NAME(),
                    filesToSend.getOrderName(),
                    filesToSend.getFileId());
        } catch (JMSException e) {
            log.error("", e);
            log.info("[{}] [{}] FILE ID [{}] UPDATING TO BEING PROCESSED->FALSE. FILE NAME [{}]", getAPP_NAME(),
                    filesToSend.getOrderName(),
                    filesToSend.getFileId(),
                    filesToSend.getFileName());
            filesToSend.setBeingProcessed(false);
            files_service.save(filesToSend);
            return;
        }

        /* Entrar en un ciclo para obtener todas las paginas */
        while (true) {
            Pageable paging = PageRequest.of(0, 1000);
            Page<SendingSms> sendingSmsPage = sendingSmsService.findAllByFileToSendId(filesToSend.getId(), paging);
            List<SendingSms> smsToSendList = sendingSmsPage.getContent();
            int expectedSmsCount = smsToSendList.size();
            if (expectedSmsCount == 0) {
                break;
            }
            int sentMessageCount = 0;

            log.info("[{}] [{}] FILE ID [{}]. PREPARING TO SEND - {} SMS", getAPP_NAME(),
                    filesToSend.getOrderName(),
                    filesToSend.getFileId(),
                    expectedSmsCount);

            for (SendingSms msg : smsToSendList) {
                Map<String, String> map = new HashMap<>(1000);
                try {
                    AbstractSMSDTO newMsg = new AbstractSMSDTO();

                    newMsg.setCarrier_char_code(msg.getCarrierCharCode());
                    newMsg.setDatacoding(msg.getDatacoding());
                    newMsg.setDate(msg.getDate());
                    newMsg.setDestination(msg.getDestination());
                    newMsg.setId(msg.getId());
                    newMsg.setIso2(msg.getIso2());
                    newMsg.setMessageType(msg.getMessageType());
                    newMsg.setMessagesText(msg.getMessagesText());
                    newMsg.setMsgReceived(msg.getMsgReceived());
                    newMsg.setMsgSended(msg.getMsgSended());
                    newMsg.setSource(msg.getSource());
                    newMsg.setSystem_id(msg.getSystemId());
                    newMsg.put(AbstractSMSDTO.ABSTRACT_OPTIONALS.CREATED_FROM.name(), "dash");

                    /* enviar mensaje a la cola */
                    mainA.send(newMsg);

                    /* borrar el mensaje enviado a la cola */
                    sendingSmsService.delete(newMsg.getId());
                    sentMessageCount++;
                    filesToSend.setNumSent(sentMessageCount);
                    if (sentMessageCount % 250 == 0) {
                        log.info("[{}] [{}] FILE ID [{}] UPDATING NUMSENT {}",
                                filesToSend.getOrderName(),
                                getAPP_NAME(),
                                filesToSend.getFileId(),
                                sentMessageCount);
                        files_service.save(filesToSend);
                    }
                } catch (ResourceAllocationException rae) {
                    log.warn("The Message could not send to the queue: {}", msg);
                } catch (JMSException jmse) {
                    log.error("", jmse);
                }
            }
            log.info("[{}] [{}] FILE ID [{}] SENDED - {} SMS",
                    filesToSend.getOrderName(),
                    getAPP_NAME(),
                    filesToSend.getFileId(),
                    sentMessageCount);
            /**/
            if (expectedSmsCount == sentMessageCount) {
                log.info("[{}] [{}] FILE ID [{}] UPDATING COMPLETED {} SMS", getAPP_NAME(),
                        filesToSend.getOrderName(),
                        getAPP_NAME(),
                        filesToSend.getFileId(),
                        sentMessageCount);
                /**/
                filesToSend.setNumSent(sentMessageCount);
                filesToSend.setStatus(FilesToSend.Status.COMPLETED);
                files_service.save(filesToSend);
            } else {
                //System.out.println("ERROR: Something went wrong with sending messages; Order will NOT be removed from database, and messages will be reinserted.");
                log.error("[{}] [{}] FILE ID [{}]. ERROR: mensajes enviados [{}] y mensajes esperados [{}] es diferente ", getAPP_NAME(),
                        filesToSend.getOrderName(),
                        getAPP_NAME(),
                        filesToSend.getFileId(),
                        sentMessageCount,
                        sentMessageCount);
                //sending_repo.saveAll(messages);
                filesToSend.setBeingProcessed(false);
                filesToSend.setStatus(FilesToSend.Status.INVALID);
                files_service.save(filesToSend);
            }
        }
        try {
            mainA.close();
        } catch (JMSException jmse) {
            log.info("[{}] [{}] ERROR CLOSING MQ FILE ID [{}] DELETING SENT SMS",
                    getAPP_NAME(),
                    filesToSend.getOrderName(),
                    filesToSend.getFileId(),
                    filesToSend.getFileName());
        }
    }

    private void deleteSended(SendingSms smsSended) {

    }
}
