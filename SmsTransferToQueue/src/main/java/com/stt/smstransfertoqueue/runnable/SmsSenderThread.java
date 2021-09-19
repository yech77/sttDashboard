/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smstransfertoqueue.runnable;

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
import java.util.List;
import java.util.Properties;

import static com.stt.smstransfertoqueue.SmsTransferToQueueApplication.getAPP_NAME;

/**
 * @author Enrique
 */
@SuppressWarnings("DuplicatedCode")
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

    private FilesToSend orderToBeExecuted;

    public SmsSenderThread(FilesToSend orderToBeExecuted,
                           SendingSmsService sendingSmsService,
                           FilesToSendService files_service,
                           OProperties p) {
        this.orderToBeExecuted = orderToBeExecuted;
        this.sendingSmsService = sendingSmsService;
        this.files_service = files_service;
        this.p = p;
    }

    @Override
    public void run() {

        try {
            log.info("[{}] FILE ID [{}] RUNNING: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                    orderToBeExecuted.getFileId(),
                    orderToBeExecuted.getOrderName(),
                    orderToBeExecuted.getFileName());
            mainA = new MQHandler(p);
            mainA.createSender();
            log.info("[{}] FILE ID [{}] UPDATING TO STATUS->SENDING: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                    orderToBeExecuted.getFileId(),
                    orderToBeExecuted.getOrderName(),
                    orderToBeExecuted.getFileName());
            orderToBeExecuted.setStatus(FilesToSend.Status.SENDING);
            files_service.save(orderToBeExecuted);

            log.info("[{}] FILE ID [{}] FINDING SMS TO SEND: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                    orderToBeExecuted.getFileId(),
                    orderToBeExecuted.getOrderName(),
                    orderToBeExecuted.getFileName());
        } catch (JMSException e) {
            log.error("", e);
            log.info("[{}] FILE ID [{}] UPDATING TO BEING PROCESSED->FALSE: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                    orderToBeExecuted.getFileId(),
                    orderToBeExecuted.getOrderName(),
                    orderToBeExecuted.getFileName());
            orderToBeExecuted.setBeingProcessed(false);
            files_service.save(orderToBeExecuted);
            return;
        }

        /* Entrar en un ciclo para obtener todas las paginas */
        while (true) {
            Pageable paging = PageRequest.of(0, 1000);
            Page<SendingSms> sendingSmsPage = sendingSmsService.findAllByFileToSendId(orderToBeExecuted.getId(), paging);
            List<SendingSms> smsToSendList = sendingSmsPage.getContent();
            int expectedSmsCount = smsToSendList.size();
            if (expectedSmsCount == 0) {
                break;
            }
            int sentMessageCount = 0;

            log.info("[{}] FILE ID [{}] PREPARING TO SEND - {} SMS: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                    orderToBeExecuted.getFileId(),
                    expectedSmsCount,
                    orderToBeExecuted.getOrderName(),
                    orderToBeExecuted.getFileName());

            for (SendingSms msg : smsToSendList) {
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

                    /* enviar mensaje a la cola */
                    mainA.send(newMsg);

                    /* borrar el mensaje enviado a la cola */
                    sendingSmsService.delete(newMsg.getId());
                    sentMessageCount++;
                    orderToBeExecuted.setNumSent(sentMessageCount);
                    if (sentMessageCount % 1000 == 0) {
                        log.info("[{}] FILE ID [{}] UPDATING NUMSENT {}: ORDER NAME [{}]. FILE NAME [{}]",
                                getAPP_NAME(),
                                orderToBeExecuted.getFileId(),
                                sentMessageCount,
                                orderToBeExecuted.getOrderName(),
                                orderToBeExecuted.getFileName());
                        files_service.save(orderToBeExecuted);
                    }
                } catch (ResourceAllocationException rae) {
                    log.warn("The Message could not send to the queue: {}", msg);
                } catch (JMSException jmse) {
                    log.error("", jmse);
                }
            }
            log.info("[{}] FILE ID [{}] SENDED - {} SMS: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                    orderToBeExecuted.getFileId(),
                    sentMessageCount,
                    orderToBeExecuted.getOrderName(),
                    orderToBeExecuted.getFileName());
            /**/
            if (expectedSmsCount == sentMessageCount) {
                log.info("[{}] FILE ID [{}] UPDATING COMPLETED {} SMS: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                        orderToBeExecuted.getFileId(),
                        sentMessageCount,
                        orderToBeExecuted.getOrderName(),
                        orderToBeExecuted.getFileName());
                /**/
                orderToBeExecuted.setNumSent(sentMessageCount);
                orderToBeExecuted.setStatus(FilesToSend.Status.COMPLETED);
                files_service.save(orderToBeExecuted);
                log.info("[{}] FILE ID [{}] DELETING SENT SMS: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                        orderToBeExecuted.getFileId(),
                        sentMessageCount,
                        orderToBeExecuted.getOrderName(),
                        orderToBeExecuted.getFileName());
            } else {
                //System.out.println("ERROR: Something went wrong with sending messages; Order will NOT be removed from database, and messages will be reinserted.");
                log.error("ERROR: Algo ha ocurrido mal; El recado no sera borrado de las tablas.");
                //sending_repo.saveAll(messages);
                orderToBeExecuted.setBeingProcessed(false);
                orderToBeExecuted.setStatus(FilesToSend.Status.INVALID);
                files_service.save(orderToBeExecuted);
            }
        }
        try {
            mainA.close();
        } catch (JMSException jmse) {
            log.info("[{}] ERROR CLOSING MQ FILE ID [{}] DELETING SENT SMS: ORDER NAME [{}]. FILE NAME [{}]",
                    getAPP_NAME(),
                    orderToBeExecuted.getFileId(),
                    orderToBeExecuted.getOrderName(),
                    orderToBeExecuted.getFileName());
        }
    }

    private void deleteSended(SendingSms smsSended) {

    }
}
