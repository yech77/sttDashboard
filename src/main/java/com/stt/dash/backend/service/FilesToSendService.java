package com.stt.dash.backend.service;

import com.stt.dash.Application;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.FilesToSendRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class FilesToSendService implements FilterableCrudService<FIlesToSend>{

//    private MyAuditEventComponent auditEvent;
    private static String UI_CODE = "SERV";
    private FilesToSendRepository filesToSendRepository;
    private FilesToSendRepository files_repo;

    private static final Logger log = LoggerFactory.getLogger(FilesToSendService.class.getName());
    public FilesToSendService(FilesToSendRepository filesToSendRepository) {
        this.filesToSendRepository = filesToSendRepository;
    }

    @Override
    public FIlesToSend save(User currentUser, FIlesToSend entity) {
        try {
            return FilterableCrudService.super.save(currentUser, entity);
        } catch (DataIntegrityViolationException e) {
            throw new UserFriendlyDataException(
                    "There is already a Masivo Agendado with that name. Please select a unique name for the Masivo a enviar.");
        }
    }

    public void save(FIlesToSend files, String user) {
        if (files == null) {
            log.warn("[{}] FilesToSend is null", Application.getAPP_NAME());
            return;
        }
        try {
            Long id = files.getId();
            FIlesToSend f = files_repo.save(files);
//            ODashAuditEvent.OEVENT_TYPE t = ODashAuditEvent.OEVENT_TYPE.CREATE_RECADO;
            if (id == null) {
                log.info("[{}] SAVED: ORDER NAME [{}] STATUS [{} - {}] BEING PRO[{}] READY TO SEND[{}]", Application.getAPP_NAME(),
                        files.getOrderName(),
                        files.getStatus().name(),
                        files.getStatusText(),
                        files.isBeingProcessed(),
                        files.isReadyToSend());
//                t = ODashAuditEvent.OEVENT_TYPE.CREATE_RECADO;
//                try {
//                    auditEvent.add(ODashAuditEvent.OEVENT_TYPE.CREATE_RECADO, files);
//                } catch (Exception e) {
//                    log.error("", e);
//                }
            } else {
                log.info("[{}] UPDATED: ORDER NAME [{}] STATUS [{} - {}] BEING PRO[{}] READY TO SEND[{}]", Application.getAPP_NAME(),
                        files.getOrderName(),
                        files.getStatus().name(),
                        files.getStatusText(),
                        files.isBeingProcessed(),
                        files.isReadyToSend());
//                t = ODashAuditEvent.OEVENT_TYPE.UPDATE_RECADO;
//                try {
//                    auditEvent.add(ODashAuditEvent.OEVENT_TYPE.UPDATE_RECADO, files);
//                } catch (Exception e) {
//                    log.error("", e);
//                }
            }
//            try {
//                auditEvent.add(t, f, user);
//            } catch (Exception e) {
//                log.error("", e);
//            }
        } catch (Exception d) {
            log.error("[{}] Error on Save:", Application.getAPP_NAME());
            log.error("", d);
        }
    }


    @Override
    public JpaRepository<FIlesToSend, Long> getRepository() {
        return filesToSendRepository;
    }

    @Override
    public FIlesToSend createNew(User currentUser) {
        return new FIlesToSend();
    }

    @Override
    public Page<FIlesToSend> findAnyMatching(Optional<String> filter, Pageable pageable) {
        return null;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }
}
