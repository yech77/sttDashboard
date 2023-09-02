/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smstransfertoqueue.repository;

import com.stt.smstransfertoqueue.entity.FilesToSend;
import com.stt.smstransfertoqueue.entity.SendingSms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Enrique
 */
public interface SendingSmsRepository extends JpaRepository<SendingSms, Long> {

    public List<SendingSms> findByFileToSend(FilesToSend fileToSend);

    Page<SendingSms> findAllByFileToSend_Id(Long id, Pageable pageable);

}
