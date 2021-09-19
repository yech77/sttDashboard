/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smstransfertoqueue.messagequeue;

import com.stt.smstransfertoqueue.SmsTransferToQueueApplication;
import com.stt.smstransfertoqueue.OProperties;
import com.stt.smstransfertoqueue.entity.AbstractSMS;
import com.sun.messaging.Queue;
import com.sun.messaging.QueueConnectionFactory;
import com.sun.messaging.jms.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.*;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;

/**
 * @author Enrique
 */
public class MQHandler implements Runnable {

    /* LOGGER */
    private static final Logger log = LogManager.getLogger(MQHandler.class);

    /*Este es el Factory del cual 'nacen' todas las conexiones al Mq.*/
    private final QueueConnectionFactory qConnectionFactory = new QueueConnectionFactory();

    /*La conexion de la cual nacen todas las Sesiones.*/
    private QueueConnection qConnection;

    /* La session de la cual se crean  los producer y los consumer del Mq*/
    private QueueSession qSession;

    private QueueBrowser myBrowser;

    /* Sender y producer de la cola/Mq */
    private QueueSender qSender;
    private QueueReceiver qReceiver;

    /* Cola/MQ sobre la cual se va a trabajar. */
    private Queue queue;

    /*Variables para ayudar manejar el mq*/
    private int mqCount = 0;
    private int mqId;
    private final String name;
    /* Propiedades */
    private final OProperties p;

    /**
     * Crear el objeto MQHandler
     *
     * @param p Archivo de propiedades
     * @throws javax.jms.JMSException
     */
    public MQHandler(OProperties p) throws JMSException {
        this.name = p.getTarget();
        this.p = p;
        mqCount++;
        mqId = mqCount;
        log.info("{} Address {}", getStringLog(), p.getImqaddresslist());
        log.info("{} Origen {}", getStringLog(), p.getOrigen());
        log.info("{} Target {}", getStringLog(), p.getTarget());
        loadFactory();
    }

    /**
     * Crea el factory para la conexion al mq.
     *
     * @throws JMSException
     */
    public void loadFactory() throws JMSException {
        /* Datos para crear el Factory de Conexiones. 
        Hasta este momento no se ha establecido una conexion real al Mq.*/
        qConnectionFactory.setProperty("imqAddressList", p.getImqaddresslist());
        log.info("{} Address from properties: [{}]", getStringLog(), p.getImqaddresslist());
        qConnectionFactory.setProperty("imqReconnectEnabled", "false");
        log.info("{} Reconnect Enabled from properties: [{}]", getStringLog(), false);
        qConnectionFactory.setProperty("imqReconnectAttempts", "-1");
        log.info("{} Reconnect Attempts from properties: [{}]", getStringLog(), -1);
        qConnectionFactory.setProperty("imqReconnectInterval", "10000");
        log.info("{} Reconnect Interval from properties: [{}]", getStringLog(), 10000);
        /*Se crea la conexion y es justo en este momento en que realmente estamos conectados
        al Broker.
        Usualmente, en lo posible, trato de tener una Connection al MQ
        y varias Session.*/

        qConnection = qConnectionFactory.createQueueConnection();
        log.info("{} [CONNECTION-CREATED] [{}]", getStringLog(), qConnection);

        /* Creando una session con auto ACK.
        Generalmente creo una Session para enviar y otra Session para recibir.
        En este ejemplo solo usare una.
         */
        qSession = qConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        log.info("{} [SESSION-CREATED] [{}] WITH [{}]", getStringLog(), qSession, qConnection);

        /*Crear el mq que va a recibir*/
        queue = new Queue(name);
        log.info("{} [QUEUE-CREATED] [{}]", getStringLog(), queue.getQueueName());
    }

    /**
     * Calcula el tamano del mq
     *
     * @return Tamano del mq
     * @throws JMSException
     */
    public int getQueueSize() throws JMSException {
        myBrowser = qSession.createBrowser(queue);
        Enumeration queueMessages = myBrowser.getEnumeration();
        int count = 0;
        while (queueMessages.hasMoreElements()) {
            queueMessages.nextElement();
            count++;
        }
        return count;
    }

    /**
     * Crea el Sender
     *
     * @throws JMSException
     */
    public void createSender() throws JMSException {
        qSender = qSession.createSender(queue);
        log.info("{} [SENDER-CREATED-QUEUE] [{}] WITH [{}]", getStringLog(), queue.getQueueName(), qSession);
    }

    /**
     * Crea el Receiver
     *
     * @throws JMSException
     */
    public void createReceiver() throws JMSException {
        qReceiver = qSession.createReceiver(queue);
        log.info("{} [RECEIVER-CREATED-QUEUE] [{}] WITH [{}]", getStringLog(), queue.getQueueName(), qSession);
    }

    /**
     * Manda un mensaje
     *
     * @param msg Mensaje para mandar
     * @throws JMSException
     */
    public void send(Serializable msg) throws JMSException {
        ObjectMessage om = qSession.createObjectMessage();
        om.setObject(msg);
        qSender.send(om);
    }

    /**
     * Recibe mensajes
     *
     * @param num Numero de mensajes para recibir
     * @throws JMSException
     */
    public void receiveMessages(int num) throws JMSException {
        if (num == 0) {
            return;
        }

        int size = getQueueSize();
        if (size == 0) {
            log.info("\n===NO SE SACA. MQ VACIO===\n");
            return;
        }
        int willRemove = size <= num ? size : num;
        log.info("\n===EL MQ TIENE [{}] MENSAJES. SE SACARAN [{}]===\n", size, willRemove);

        log.info("[{}] ===PREPARANDO PARA RECIBIR===", name);
        /* La interfaz MessageListener, escucha cuando llega un mensaje a la cola/mq y llama al metodo onMessage*/
        qReceiver.setMessageListener(new MessageListener() {
            Random r = new Random(0);
            int count = 0;

            /*Este metodo se va a llamar mientras exista un mensaje en la cola/mq*/
            @Override
            public void onMessage(Message msg) {
                try {
                    ObjectMessage o = (ObjectMessage) msg;
                    AbstractSMS newMsg;
                    if (count < num && o.getObject() instanceof AbstractSMS) {
                        newMsg = (AbstractSMS) o.getObject();
                        log.info("[{}] Mensaje -> [{}]", name, newMsg);
                        count++;
                        if (count >= willRemove) {
                            qReceiver.setMessageListener(null);
                            System.out.println("Se han terminado de sacar los mensajes.");
                        }
                    } else {
                        log.info("[{}] Objeto -> [{}]", name, o.getObject());
                    }

                    Thread.sleep(r.nextInt(100));
                } catch (JMSException ex) {
                    log.error("===ERROR OCCURRED===");
                } catch (InterruptedException ex) {
                    log.error("===ERROR OCCURRED===");
                }
            }
        });

        /* Se inicia un thread en la conexion para que se puedan recibir los mensajes */
        start();
    }

    /**
     * Inicializa el Listener
     *
     * @param l Listener
     * @throws JMSException
     */
    public void setListener(MessageListener l) throws JMSException {
        qReceiver.setMessageListener(l);
    }

    /**
     * Inicializa el Listener
     *
     * @throws JMSException
     */
    public void setListener() throws JMSException {
        MQReceiverListener rec = new MQReceiverListener();
        qReceiver.setMessageListener(rec);
    }

    /**
     * Cierra el MQ
     *
     * @throws JMSException
     */
    public void close() throws JMSException {
        log.info("CLOSING MQ...");
        try {
            if (qReceiver != null) {
                qReceiver.close();
                log.info("{} [RECEIVER-CLOSED-QUEUE] [{}] WITH [{}]", getStringLog(),
                        qReceiver.getQueue().getQueueName(),
                        qSession);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        try {
            if (qSender != null) {
                qSender.close();
                log.info("{} [SENDER-CLOSED-QUEUE] [{}] WITH [{}]", getStringLog(),
                        qSender.getQueue().getQueueName(),
                        qSession);
            }
        } catch (Exception e) {
            log.error("", e);

        }
        try {
            if (qSession != null) {
                qSession.close();
                log.info("{} [SESSION-CLOSED] [{}] WITH [{}]", getStringLog(), qSession, qConnection);
            }
        } catch (Exception e) {
            log.error("", e);

        }
        try {
            if (qConnection != null) {
                qConnection.stop();
                log.info("{} [CONNECTION-STOPPED] [{}] WITH [{}]", getStringLog(), qConnection);

            }
        } catch (Exception e) {
            log.error("", e);

        }
        try {
            if (qConnection != null) {
                qConnection.close();
                log.info("{} [CONNECTION-CLOSED] [{}] WITH [{}]", getStringLog(), qConnection);
            }
        } catch (Exception e) {
            log.error("", e);

        }
    }

    /**
     * Crea la conexion al MQ
     *
     * @throws JMSException
     */
    public void start() throws JMSException {
        qConnection.start();
        log.info("{} [CONNECTION-STARTED] [{}] WITH [{}]", getStringLog(), qConnection);
    }

    /**
     * Detiene la conexion al MQ
     *
     * @throws JMSException
     */
    public void stopConnection() throws JMSException {
        qConnection.stop();
    }

    public int getID() {
        return mqId;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static String getStringLog() {
        return "[" + SmsTransferToQueueApplication.getAPP_NAME() + "] " + "[MQ]";
    }
}
