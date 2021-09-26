/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager.helper;

import com.stt.smspreparationmanager.entity.SepSms;
import com.stt.smspreparationmanager.repository.SepSmsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Enrique
 */
public class SepSmsHelper extends SmsHelper{
    
    private static final int monthInt = 8;
    private static final int daysThisMonth = 30;
    private static final String nameOfMonth = "Septiembre";

    @Autowired
    private SepSmsRepository repo;

    public SepSmsHelper(SepSmsRepository repo, int amt) {
        this.repo = repo;
        this.amt = amt;
    }

    public void start(String systemId, String carrierCharCode, String messageType, int amt) {
        List<SepSms> batch = new ArrayList<>();
        for (int i = 0; i < amt; i++) {
            SepSms msg = new SepSms();
            StringBuilder phone = new StringBuilder();

            switch ((int) (Math.random() * 5)) {
                case 0:
                    phone.append("58414");
                    break;
                case 1:
                    phone.append("58424");
                    break;
                case 2:
                    phone.append("58416");
                    break;
                case 3:
                    phone.append("58426");
                    break;
                case 4:
                    phone.append("58412");
                    break;
                default:
                    phone.append("11111");
            }
            for (int j = 0; j < 7; j++) {
                phone.append((int) (Math.random() * 10));
            }
            boolean isMT = messageType.equals("MT");
            if (!isMT) {
                msg.setSource(phone.toString());
                msg.setDestination(sources[(int) (Math.random() * 5)]);
            } else {
                msg.setSource(sources[(int) (Math.random() * 5)]);
                msg.setDestination(phone.toString());
            }

            msg.setDatacoding(3);
            
            Calendar cal = Calendar.getInstance();
            cal.set(
                    cal.get(Calendar.YEAR),
                    monthInt,
                    (int)(Math.random()*daysThisMonth)+1,
                    (int)(Math.random()*24),
                    (int)(Math.random()*60),
                    (int)(Math.random()*60)
            );
            
            msg.setDate(cal.getTime());
            msg.setIso2(iso2[(int) (Math.random() * 5)]);
            msg.setMessageType(messageType);
            msg.setMessagesText("Este es un mensaje en Septiembre; " +cal.toString());
            
            StringBuilder hashCode1 = new StringBuilder();
            StringBuilder hashCode2 = new StringBuilder();
            int ranSize1 = (int)(Math.random()*11)+10;
            int ranSize2 = (int)(Math.random()*11)+10;
            for(int j=0;j<ranSize1;j++){
                int r =(int)(Math.random()*36);
                hashCode1.append( r>=10 ? "" + ((char)(r+87)) : r);
            }
            for(int j=0;j<ranSize2;j++){
                int r =(int)(Math.random()*36);
                hashCode2.append( r>=10 ? "" + ((char)(r+87)) : r);
            }
            msg.setMsgSended(hashCode1.toString());
            msg.setMsgReceived(hashCode2.toString());
            msg.setCarrierCharCode(carrierCharCode);
            msg.setSystemId(systemId);
            System.out.println(msg);
            batch.add(msg);
            if(batch.size()>=batchSize){
                repo.saveAll(batch);
                batch.clear();
            }
        }
        if(!batch.isEmpty()){
            repo.saveAll(batch);
        }
        
        System.out.println("Mensajes en "+nameOfMonth+": " + repo.count());
        System.out.println("Mensajes en "+nameOfMonth+": \n" + repo.findAll());
    }
    
    public void start(int amt) {
        List<SepSms> batch = new ArrayList<>();
        for (int i = 0; i < amt; i++) {
            SepSms msg = new SepSms();
            StringBuilder phone = new StringBuilder();
            String carrierCode;

            switch ((int) (Math.random() * 5)) {
                case 0:
                    phone.append("58414");
                    carrierCode="MOVISTAR";
                    break;
                case 1:
                    phone.append("58424");
                    carrierCode="MOVISTAR";
                    break;
                case 2:
                    phone.append("58416");
                    carrierCode="MOVILNET";
                    break;
                case 3:
                    phone.append("58426");
                    carrierCode="MOVILNET";
                    break;
                case 4:
                    phone.append("58412");
                    carrierCode="DIGITEL";
                    break;
                default:
                    phone.append("11111");
                    carrierCode="BadGeneration";
            }
            for (int j = 0; j < 7; j++) {
                phone.append((int) (Math.random() * 10));
            }
            boolean isMT = Math.random() < 0.6;
            if (!isMT) {
                msg.setSource(phone.toString());
                msg.setDestination(sources[(int) (Math.random() * 5)]);
            } else {
                msg.setSource(sources[(int) (Math.random() * 5)]);
                msg.setDestination(phone.toString());
            }

            msg.setDatacoding(3);
            
            Calendar cal = Calendar.getInstance();
            cal.set(
                    cal.get(Calendar.YEAR),
                    monthInt,
                    (int)(Math.random()*daysThisMonth)+1,
                    (int)(Math.random()*24),
                    (int)(Math.random()*60),
                    (int)(Math.random()*60)
            );
            
            msg.setDate(cal.getTime());
            msg.setIso2(iso2[(int) (Math.random() * 5)]);
            msg.setMessageType(isMT ? "MT" : "MO");
            msg.setMessagesText("Este es un mensaje artificial.");
            msg.setMsgSended("msgSent");
            msg.setMsgReceived("msgReceived");
            msg.setCarrierCharCode(carrierCode);
            msg.setSystemId(getAllSystemIds().get((int) (Math.random() * getAllSystemIds().size())));
            System.out.println(msg);
            batch.add(msg);
            if(batch.size()>=batchSize){
                repo.saveAll(batch);
                batch.clear();
            }
        }
        if(!batch.isEmpty()){
            repo.saveAll(batch);
        }
        
        System.out.println("Mensajes en "+nameOfMonth+": " + repo.count());
        System.out.println("Mensajes en "+nameOfMonth+": \n" + repo.findAll());
    }
}
