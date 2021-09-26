/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager.entity;

import javax.persistence.Entity;

/**
 * @since @author yech77
 */
@Entity
public class FebSms extends AbstractSMS {

    @Override
    public String toString() {
        return "FebSms{" + super.toString() + '}';
    }

}
