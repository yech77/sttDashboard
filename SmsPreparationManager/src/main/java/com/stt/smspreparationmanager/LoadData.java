/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager;

import com.stt.smspreparationmanager.repository.OctSmsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author yech
 */
@Configuration
public class LoadData {

    public enum AUTH {
        CREATE_USER_IS, CREATE_USER_HAS, CREATE_USER_BY,
        SCREEN_CLIENTS, SCREEN_USER,
        FILE_DOWNLOAD_SMS, FILE_UPLOAD
    }

    public enum ROL {
        VIEW_ALL, VIEW_CLIENT, VIEW_USER,
        CREATE_ALL, CREATE_USER_IS, CREATE_USER_HAS, CREATE_USER_BY
    }

    public static final String[] iso2 = {"VE", "CO", "ME", "BO", "NI"};
    public static final String[] sources = {"78900", "12300", "456000", "12345", "12345"};
    private static final int[] daysPerMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    @Bean
    CommandLineRunner initDatabase(OctSmsRepository oct_repo) {

        return args -> {
//            
//            //List<OctSms> messages = new ArrayList<>();
//            for (int i = 0; i < 100000; i++) {
//                OctSms msg = new OctSms();
//                StringBuilder phone = new StringBuilder();
//                StringBuilder carrierCode = new StringBuilder();
//
//                switch ((int) (Math.random() * 5)) {
//                    case 0:
//                        phone.append("58414");
//                        carrierCode.append("MOVISTAR");
//                        break;
//                    case 1:
//                        phone.append("58424");
//                        carrierCode.append("MOVISTAR");
//                        break;
//                    case 2:
//                        phone.append("58416");
//                        carrierCode.append("MOVILNET");
//                        break;
//                    case 3:
//                        phone.append("58426");
//                        carrierCode.append("MOVILNET");
//                        break;
//                    case 4:
//                        phone.append("58412");
//                        carrierCode.append("DIGITEL");
//                        break;
//                }
//                for (int j = 0; j < 7; j++) {
//                    phone.append((int) (Math.random() * 10));
//                }
//                msg.setId(i * 1l);
//                boolean isMT = Math.random() < 0.6;
//                if (isMT) {
//                    msg.setSource(phone.toString());
//                    msg.setDestination(sources[(int) (Math.random() * 5)]);
//                } else {
//                    msg.setSource(sources[(int) (Math.random() * 5)]);
//                    msg.setDestination(phone.toString());
//                }
//                
//                msg.setDatacoding(3);
//                //TODO: Crear una fecha aleatoria
//                msg.setDate(new Date());
//                msg.setIso2(iso2[(int) (Math.random() * 5)]);
//                msg.setMessageType(isMT?"MT":"MO");
//                msg.setMessagesText("Este es un mensaje artificial.");
//                msg.setMsgSended("msgSended");
//                msg.setMsgReceived("msgReceived");
//                msg.setCarrierCharCode(carrierCode.toString());
//                msg.setSystemId(Clients.getAllSystemIds().get((int) (Math.random() * Clients.getAllSystemIds().size())));
//                
//                oct_repo.save(msg);
//            }
//
//            System.out.println(oct_repo.findAll());
//            System.out.println(oct_repo.count());
        };
    }
}
