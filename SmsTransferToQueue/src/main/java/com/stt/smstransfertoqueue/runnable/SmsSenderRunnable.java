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
import com.stt.smstransfertoqueue.repository.SendingSmsRepository;
import com.stt.smstransfertoqueue.service.FilesToSendService;
import com.sun.messaging.Queue;
import com.sun.messaging.QueueConnectionFactory;
import com.sun.messaging.jms.QueueConnection;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.stt.smstransfertoqueue.SmsTransferToQueueApplication.getAPP_NAME;

/**
 * @author Enrique
 */
public class SmsSenderRunnable implements Runnable {

    /*Log*/
    private static org.apache.logging.log4j.Logger log = LogManager.getLogger(SmsSenderRunnable.class);
    /*Este es el Factory del cual 'nacen' todas las conexiones al Mq.*/
    private final QueueConnectionFactory qConnectionFactory = new QueueConnectionFactory();

    /*La conexion de la cual nacen todas las Sesiones.*/
    private QueueConnection qConnection;

    /* La session de la cual se crean  los producer y los consumer del Mq*/
    QueueSession qSession;

    /* Sender y producer de la cola/Mq */
    QueueSender qSender;
    QueueReceiver qReceiver;

    /* Cola/MQ sobre la cual se va a trabajar. */
    Queue queue;

    /*Properties File*/
    private static Properties prop;

    /*Message Queues y sus numbres*/
    private MQHandler mainA;
    private String nameA;

    @Autowired
    private SendingSmsRepository sending_repo;

    @Autowired
    private FilesToSendService files_service;

    private final OProperties p;

    private FilesToSend orderToBeExecuted;

    public SmsSenderRunnable(FilesToSend orderToBeExecuted,
                             SendingSmsRepository sending_repo,
                             FilesToSendService files_service,
                             OProperties p) {
        this.orderToBeExecuted = orderToBeExecuted;
        this.sending_repo = sending_repo;
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
            try {
                mainA = new MQHandler(p);
            } catch (JMSException ex) {
                log.error("", ex);
                log.info("[{}] FILE ID [{}] UPDATING TO BEING PROCESSED->FALSE: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                        orderToBeExecuted.getFileId(),
                        orderToBeExecuted.getOrderName(),
                        orderToBeExecuted.getFileName());
                orderToBeExecuted.setBeingProcessed(false);
                files_service.save(orderToBeExecuted);
                return;
            }

            mainA.createSender();
            nameA = mainA.getName();
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
            List<SendingSms> messages = sending_repo.findByFileToSend(orderToBeExecuted);
            int expectedSmsCount = messages.size();

            //List<AbstractSMS> sentMessages = new ArrayList<>();
            int sentMessageCount = 0;

            if (messages == null) {
                messages = new ArrayList<>(1);
            }
            log.info("[{}] FILE ID [{}] PREPARING TO SEND - {} SMS: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                    orderToBeExecuted.getFileId(),
                    messages.size(),
                    orderToBeExecuted.getOrderName(),
                    orderToBeExecuted.getFileName());
            for (SendingSms msg : messages) {
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
                mainA.send(newMsg);
                //System.out.println("Mandando mensaje al mq: " + newMsg);
                // Ahora mismo lo añado a una lista pero la lista no se usa aún porque
                // solo se esta imprimiendo la lista.
                sentMessageCount++;
                orderToBeExecuted.setNumSent(sentMessageCount);
                if (sentMessageCount % 100 == 0) {
                    log.info("[{}] FILE ID [{}] UPDATING NUMSENT {}: ORDER NAME [{}]. FILE NAME [{}]",
                            getAPP_NAME(),
                            orderToBeExecuted.getFileId(),
                            sentMessageCount,
                            orderToBeExecuted.getOrderName(),
                            orderToBeExecuted.getFileName());
                    files_service.save(orderToBeExecuted);
                }
                /**
                 * TODO LOS QUE VAYAN ENVIANDO SE DEBEN IR BORRANDO. ESTO PARA NOBORRARLOS TODOS AFUERA.
                 */

            }
            log.info("[{}] FILE ID [{}] SENDED - {} SMS: ORDER NAME [{}]. FILE NAME [{}]", getAPP_NAME(),
                    orderToBeExecuted.getFileId(),
                    sentMessageCount,
                    orderToBeExecuted.getOrderName(),
                    orderToBeExecuted.getFileName());
            /**/
            if (messages.size() == sentMessageCount) {
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
                sending_repo.deleteAll(messages);
            } else {
                //System.out.println("ERROR: Something went wrong with sending messages; Order will NOT be removed from database, and messages will be reinserted.");
                log.error("ERROR: Algo ha ocurrido mal; El recado no sera borrado de las tablas.");
                //sending_repo.saveAll(messages);
                orderToBeExecuted.setBeingProcessed(false);
                orderToBeExecuted.setStatus(FilesToSend.Status.INVALID);
                files_service.save(orderToBeExecuted);
            }
            log.info("Hay " + mainA.getQueueSize() + " mensajes en la cola.");

            log.info("Cerrando hilo para mandar mensajes");
            mainA.close();

        } catch (ResourceAllocationException rae) {
            log.warn("cannot send sms to queue. Queue is full. [{}]");
        } catch (JMSException jmse) {

        }

    }

    private void deleteSended(SendingSms smsSended) {

    }
}
