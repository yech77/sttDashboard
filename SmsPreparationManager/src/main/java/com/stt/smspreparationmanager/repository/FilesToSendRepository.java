/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smspreparationmanager.repository;


import com.stt.smspreparationmanager.entity.FilesToSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Enrique
 */
public interface FilesToSendRepository extends JpaRepository<FilesToSend, Long> {

    @Query("select f from FilesToSend f "
            + "WHERE lower(f.fileName) like lower(concat('%', :filterText, '%'))"
            + " AND f.systemId is :systemId")
    List<FilesToSend> filterSearchNameSystemId(String filterText, String systemId);

    List<FilesToSend> findBySystemId(String systemId);

    @Query("select f from FilesToSend f "
            + "WHERE f.systemId IN (:list_sid) "
            + "ORDER BY f.dateToSend ASC ")
    List<FilesToSend> getAllOrders(@Param("list_sid") List<String> list_sid);

    @Query("select f from FilesToSend f "
            + "WHERE f.dateToSend <= :now "
            + "AND f.readyToSend = true "
            + "AND f.beingProcessed = false "
            + "AND f.systemId IN (:list_sid) "
            + "ORDER BY f.dateToSend ASC ")
    List<FilesToSend> getUnsentOrders(@Param("now") Date now,
            @Param("list_sid") List<String> list_sid);

    @Query("select f from FilesToSend f "
            + "WHERE f.dateToSend <= :now "
            + "AND f.readyToSend = true "
            + "AND f.beingProcessed = false "
            + "ORDER BY f.dateToSend ASC ")
    List<FilesToSend> getUnsentOrders(@Param("now") Date now);
    
    List<FilesToSend> findByFilePath(String filePath);
}
