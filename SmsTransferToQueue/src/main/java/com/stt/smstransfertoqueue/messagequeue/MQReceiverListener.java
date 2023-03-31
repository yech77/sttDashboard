/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smstransfertoqueue.messagequeue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Random;

/**
 *
 * @author Enrique
 */
public class MQReceiverListener implements MessageListener{
    private Random r = new Random();
    private static Logger log = LoggerFactory.getLogger(MQReceiverListener.class);
    
    @Override
    public void onMessage(Message msg) {
        try {
            String s;
            ObjectMessage o = (ObjectMessage) msg;
            s = (String) o.getObject();
            log.info("Objeto-> [{}]" , s);
            Thread.sleep(r.nextInt(100));
        } catch (JMSException ex) {
            log.error("===ERROR OCCURRED===");
        } catch (InterruptedException ex) {
            log.error("===ERROR OCCURRED===");
        }
    }
}
