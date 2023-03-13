package com.stt.dash.backend.service;

import com.stt.dash.Application;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.Status;
import com.stt.dash.backend.data.entity.*;
import com.stt.dash.backend.repositories.FilesToSendRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class FilesToSendService implements CrudService<FIlesToSend> {

    private static final String
            NO_SE_PUEDEN_BORRAR_PROGRAMACIONES_YA_ENVIADAS = "No se pueden borrar Programaciones ya enviadas.";
    //    private MyAuditEventComponent auditEvent;
    private static String UI_CODE = "SERV";
    private FilesToSendRepository filesToSendRepository;
    private final MyAuditEventComponent auditEvent;
    private long isotherCounter = -1;
    private static final Logger log = LoggerFactory.getLogger(FilesToSendService.class.getName());

    @Autowired
    public FilesToSendService(FilesToSendRepository filesToSendRepository,
                              MyAuditEventComponent auditEvent) {
        super();
        this.filesToSendRepository = filesToSendRepository;
        this.auditEvent = auditEvent;
    }

    private static final Set<Status> notAvailableStates = Collections.unmodifiableSet(
            EnumSet.complementOf(EnumSet.of(Status.INVALID, Status.WAITING_TO_SEND)));

    @Override
    public FIlesToSend save(User currentUser, FIlesToSend fIlesToSend) {
        if (fIlesToSend == null) {
            log.warn("[{}] FilesToSend is null", Application.getAPP_NAME());
            return null;
        }
        Long id = fIlesToSend.getId();
        boolean acceptedsms = fIlesToSend.isSmsAccepted();
        FIlesToSend f = CrudService.super.save(currentUser, fIlesToSend);
        f.setSmsAccepted(acceptedsms);
        try {
//            f = filesToSendRepository.save(fIlesToSend);
            ODashAuditEvent.OEVENT_TYPE t = ODashAuditEvent.OEVENT_TYPE.CREATE_RECADO;
            if (id == null) {
                log.info("[{}] SAVED: ORDER NAME [{}] STATUS [{} - {}] BEING PRO[{}] READY TO SEND[{}]", Application.getAPP_NAME(),
                        fIlesToSend.getOrderName(),
                        fIlesToSend.getStatus().name(),
                        fIlesToSend.getStatusText(),
                        fIlesToSend.isBeingProcessed(),
                        fIlesToSend.isReadyToSend());
//                t = ODashAuditEvent.OEVENT_TYPE.CREATE_RECADO;
                try {
                    auditEvent.add(ODashAuditEvent.OEVENT_TYPE.CREATE_RECADO, f);
                } catch (Exception e) {
                    log.error("", e);
                }
            } else {
                log.info("[{}] UPDATED: ORDER NAME [{}] STATUS [{} - {}] BEING PRO[{}] READY TO SEND[{}]", Application.getAPP_NAME(),
                        fIlesToSend.getOrderName(),
                        fIlesToSend.getStatus().name(),
                        fIlesToSend.getStatusText(),
                        fIlesToSend.isBeingProcessed(),
                        fIlesToSend.isReadyToSend());
                t = ODashAuditEvent.OEVENT_TYPE.UPDATE_RECADO;
                try {
                    auditEvent.add(ODashAuditEvent.OEVENT_TYPE.UPDATE_RECADO, f);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        } catch (Exception d) {
            log.error("[{}] Error on Save:", Application.getAPP_NAME());
            log.error("", d);
        }
        return f;
    }

    public FIlesToSend updateState(User currentUser, FIlesToSend fIlesToSend) {
        if (fIlesToSend == null) {
            log.warn("[{}] FilesToSend is null", Application.getAPP_NAME());
            return null;
        }
        Long id = fIlesToSend.getId();
        FIlesToSend f = CrudService.super.save(currentUser, fIlesToSend);
        try {
            if (id == null) {
                throw new Exception("No debe venir vacio");
            } else {
                log.info("[{}] UPDATED: ORDER NAME [{}] STATUS [{} - {}] BEING PRO[{}] READY TO SEND[{}]", Application.getAPP_NAME(),
                        fIlesToSend.getOrderName(),
                        fIlesToSend.getStatus().name(),
                        fIlesToSend.getStatusText(),
                        fIlesToSend.isBeingProcessed(),
                        fIlesToSend.isReadyToSend());
            }
        } catch (Exception d) {
            log.error("[{}] Error on Save:", Application.getAPP_NAME());
            log.error("", d);
        }
        return f;
    }

    public Page<FIlesToSend> findAnyMatchingAfterDateToSend(Optional<String> optionalFilter,
                                                            Optional<Date> optionalFilterDate, Pageable pageable) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            if (optionalFilterDate.isPresent()) {
                return filesToSendRepository.findFIlesToSendByOrderNameContainingIgnoreCaseAndDateToSendAfter(
                        optionalFilter.get(), optionalFilterDate.get(), pageable);
            } else {
                return filesToSendRepository.findFIlesToSendByOrderNameContainingIgnoreCase(optionalFilter.get(), pageable);
            }
        } else {
            if (optionalFilterDate.isPresent()) {
                return filesToSendRepository.findFIlesToSendByDateToSendAfter(optionalFilterDate.get(), pageable);
            } else {
                return filesToSendRepository.findAll(pageable);
            }
        }
    }

    public Page<FIlesToSend> findAnyMatchingAfterDateToSend(CurrentUser currentUser, Optional<String> optionalFilter,
                                                            Optional<Date> optionalFilterDate, Pageable pageable) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            if (optionalFilterDate.isPresent()) {
                return filesToSendRepository.findFIlesToSendByOrderNameContainingIgnoreCaseAndDateToSendAfter(
                        optionalFilter.get(), optionalFilterDate.get(), pageable);
            } else {
                return filesToSendRepository.findFIlesToSendByOrderNameContainingIgnoreCase(optionalFilter.get(), pageable);
            }
        } else {
            List<User> lu = getUserFamily(currentUser.getUser());
            /* es para indicar que el counter debe contar este*/
            isotherCounter = lu.size();
            if (optionalFilterDate.isPresent()) {
                return filesToSendRepository.findByUserCreatorInAndDateToSendAfter(lu, optionalFilterDate.get(), pageable);
            } else {
                return filesToSendRepository.findByUserCreatorInOrderByDateToSendDesc(lu, pageable);
            }
        }
    }

    @Transactional
    public List<FileToSendSummary> findAnyMatchingStartingToday() {
        return filesToSendRepository.findFIlesToSendByDateToSendGreaterThanEqual(new Date());
    }

    public long countAnyMatchingAfterDateToSend(CurrentUser currentUser, Optional<String> optionalFilter, Optional<Date> optionalFilterDate) {
        if (optionalFilter.isPresent() && optionalFilterDate.isPresent()) {
//            return filesToSendRepository.countAllByOrderNameContainingIgnoreCaseAndDateToSendAfter(optionalFilter.get(),
//                    optionalFilterDate.get());
            return filesToSendRepository.countAllByUserCreatorInAndOrderNameContainingIgnoreCaseAndDateToSendAfter(getUserFamily(currentUser.getUser()), optionalFilter.get(),
                    optionalFilterDate.get());
        } else if (optionalFilter.isPresent()) {
//            return filesToSendRepository.countAllByOrderNameContainingIgnoreCase(optionalFilter.get());
            return filesToSendRepository.countAllByUserCreatorInAndOrderNameContainingIgnoreCase(getUserFamily(currentUser.getUser()), optionalFilter.get());
        } else if (optionalFilterDate.isPresent()) {
//            return filesToSendRepository.countAllByDateToSendAfter(optionalFilterDate.get());
            return filesToSendRepository.countAllByUserCreatorInAndDateToSendAfter(getUserFamily(currentUser.getUser()), optionalFilterDate.get());
        } else {
            return filesToSendRepository.countByUserCreatorIn(getUserFamily(currentUser.getUser()));
        }
    }

    @Override
    public JpaRepository<FIlesToSend, Long> getRepository() {
        return filesToSendRepository;
    }

    @Override
    @Transactional
    public FIlesToSend createNew(User currentUser) {
        return new FIlesToSend();
    }

    @Override
    public void delete(User currentUser, FIlesToSend entity) {
        throwIfDeletingFileAlreadySended(currentUser, entity);
        CrudService.super.delete(currentUser, entity);
        auditEvent.add(ODashAuditEvent.OEVENT_TYPE.DELETE_AGENDA, entity);
    }

    private void throwIfDeletingFileAlreadySended(User currentUser, FIlesToSend entity) {
        if (entity.getStatus() == Status.COMPLETED) {
            throw new UserFriendlyDataException(NO_SE_PUEDEN_BORRAR_PROGRAMACIONES_YA_ENVIADAS);
        }
    }

    private List<User> getUserFamily(User currentUser) {
        List<User> allUsers = new ArrayList<>();
        List<User> currentFam = new ArrayList<>();
        List<User> addingChildren = new ArrayList<>();

        currentFam.add(currentUser);
        addingChildren.addAll(currentUser.getUserChildren());
        while (addingChildren.size() > 0) {
            allUsers.addAll(currentFam);
            currentFam.clear();
            currentFam.addAll(addingChildren);
            addingChildren.clear();
            for (User user : currentFam) {
                addingChildren.addAll(user.getUserChildren());
            }
        }
        allUsers.addAll(currentFam);
        System.out.println("Usuarios en la familia de " + currentUser.getEmail());

        return allUsers;
    }
}
