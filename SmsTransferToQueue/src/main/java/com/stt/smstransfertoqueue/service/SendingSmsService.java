package com.stt.smstransfertoqueue.service;

import com.stt.smstransfertoqueue.entity.FilesToSend;
import com.stt.smstransfertoqueue.entity.SendingSms;
import com.stt.smstransfertoqueue.repository.SendingSmsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SendingSmsService {
    private SendingSmsRepository repository;

    public SendingSmsService(SendingSmsRepository repository) {
        this.repository = repository;
    }

    public Page<SendingSms> findAllByFileToSendId(Long id, Pageable pageable) {
        return repository.findAllByFileToSend_Id(id, pageable);
    }

    /**
     * @param filesToSend
     * @return
     * @use findAllByFileToSendId
     * @deprecated
     */
    public List<SendingSms> findByFileToSend(FilesToSend filesToSend) {
        return repository.findByFileToSend(filesToSend);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
