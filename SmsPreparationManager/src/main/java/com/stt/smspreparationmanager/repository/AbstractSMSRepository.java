/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager.repository;

import com.stt.smspreparationmanager.entity.AbstractSMS;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Enrique
 */
public interface AbstractSMSRepository extends JpaRepository<AbstractSMS, Long>{
    
}
