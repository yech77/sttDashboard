/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Enrique
 */
public class Clients {

    private static List<String> clients = new ArrayList<>();
    private static List<String> systemIds = new ArrayList<>();
    private static Map<String, List<String>> clientsToSystemIds = new HashMap<>();

    public static Map<String, List<String>> getClientsToSystemIds() {
        return clientsToSystemIds;
    }

    public static void generateClients() {
        /*TODO: Explicar */
        if (clientsToSystemIds.isEmpty()) {
            List<String> sysIds = new ArrayList<>();
            for (int i = 1; i < 2; i++) {
                sysIds.add("NET0" + i);
            }
            clients.add("NETUNO");
            systemIds.addAll(sysIds);
            clientsToSystemIds.put("NETUNO", sysIds);

//            sysIds.clear();
            sysIds = new ArrayList<>();
            for (int i = 1; i < 5; i++) {
                sysIds.add("BDV0" + i);
            }
            clients.add("BANCO DE VENEZUELA");
            systemIds.addAll(sysIds);
            clientsToSystemIds.put("BANCO DE VENEZUELA", sysIds);

//            sysIds.clear();
            sysIds = new ArrayList<>();
            for (int i = 1; i < 3; i++) {
                sysIds.add("CUN0" + i);
            }
            clients.add("CINES UNIDOS");
            systemIds.addAll(sysIds);
            clientsToSystemIds.put("CINES UNIDOS", sysIds);

//            sysIds.clear();
            sysIds = new ArrayList<>();
            for (int i = 1; i < 5; i++) {
                sysIds.add("POL0" + i);
            }
            clients.add("POLAR");
            systemIds.addAll(sysIds);
            clientsToSystemIds.put("POLAR", sysIds);

//            sysIds.clear();
            sysIds = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                sysIds.add("QQC0" + i);
            }
            clients.add("QUIQUECORP");
            systemIds.addAll(sysIds);
            clientsToSystemIds.put("QUIQUECORP", sysIds);

        }
    }

    public static List<String> getClients() {
        return clients;
    }

    public static List<String> getAllSystemIds() {
        return systemIds;
    }

    public static List<String> getSystemIds(String client) {
        return clientsToSystemIds.get(client);
    }

    public static void addClients(String... newClients) {
        for (String client : newClients) {
            if (!clientsToSystemIds.containsKey(client)) {
                clients.add(client);
                clientsToSystemIds.put(client, new ArrayList<>());
            }
        }
    }

    public static void addSystemIds(String client, String... newSystemIds) {
        List<String> currentIds = Clients.getSystemIds(client);
        for (String sysId : newSystemIds) {
            if (!currentIds.contains(sysId)) {
                systemIds.add(sysId);
                currentIds.add(sysId);
            }
        }
        clientsToSystemIds.remove(client);
        clientsToSystemIds.put(client, currentIds);
    }
}
