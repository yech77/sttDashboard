/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.smstransfertoqueue.service;


import com.stt.smstransfertoqueue.entity.FilesToSend;
import com.stt.smstransfertoqueue.repository.FilesToSendRepository;
import com.stt.smstransfertoqueue.util.ODateUitls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Enrique
 */
@Service
public class FilesToSendService {

    private FilesToSendRepository files_repo;

    public FilesToSendService(@Autowired FilesToSendRepository files_repo) {
        this.files_repo = files_repo;
    }

    public Long count() {
        return files_repo.count();
    }

    public void delete(FilesToSend file) {
        files_repo.delete(file);
    }

    public void save(FilesToSend file) {
        if (file == null) {
            return;
        }
        files_repo.save(file);
    }

    public List<FilesToSend> findAllOrders(List<String> sys_ids) {
        return files_repo.getAllOrders(sys_ids);
    }

    public List<FilesToSend> findAll(String systemId) {
        return files_repo.findBySystemId(systemId);
    }

    public List<FilesToSend> filterSearch(String filterText, String systemId) {
        if (filterText.length() < 1) {
            return findAll(systemId);
        }
        return files_repo.filterSearchNameSystemId(filterText, systemId);
    }

    /**
     * Retorma todos los archivos que estan listos para ser enviados y que no se esten enviando.
     * @param time
     * @return
     */
    public List<FilesToSend> getUnsentOrders(LocalDateTime time) {
        return files_repo.getUnsentOrders(ODateUitls.localDateTimeToDate(LocalDateTime.now()));
    }

    public List<FilesToSend> getUnsentOrders(Date now) {
        return files_repo.getUnsentOrders(now);
    }

    public List<FilesToSend> getUnsentOrders(LocalDateTime time, List<String> sys_ids) {
        return files_repo.getUnsentOrders(ODateUitls.localDateTimeToDate(LocalDateTime.now()),
                sys_ids);
    }

    public List<FilesToSend> getUnsentOrders(Date now, List<String> sys_ids) {
        return files_repo.getUnsentOrders(now, sys_ids);
    }
    public FilesToSend getFromFilePath(String path){
        List<FilesToSend> list = files_repo.findByFilePath(path);
        if(list.size()<1){
            return null;
        }
        return list.get(0);
    }
    
    public FilesToSend findById(Long id){
        Optional<FilesToSend> item = files_repo.findById(id);
        if(item.isPresent()){
            return item.get();
        }
        System.out.println("No hay FileToSend con este Id:" + id);
        return null;
    }
}
