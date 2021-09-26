/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager.repository;

import com.stt.smspreparationmanager.entity.FilesToSend;
import com.stt.smspreparationmanager.entity.SendingSms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * @author Enrique
 */
public interface SendingSmsRepository extends JpaRepository<SendingSms, Long>{
    
    public List<SendingSms> findByFileToSend(FilesToSend fileToSend);
}
