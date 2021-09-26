/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager.helper;

import com.stt.smspreparationmanager.Clients;

import java.util.List;

/**
 *
 * @author Enrique
 */

public class SmsHelper {
    public static final String[] iso2 = {"VE", "CO", "ME", "BO", "NI"};
    public static final String[] sources = {"78900", "12300", "456000", "12345", "12345"};
    public static final int batchSize = 10;
    public static int amt;

    public List<String> getAllSystemIds(){
        return Clients.getAllSystemIds();
    }
}
