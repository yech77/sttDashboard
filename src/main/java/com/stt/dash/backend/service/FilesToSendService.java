package com.stt.dash.backend.service;

import com.stt.dash.Application;
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
import java.util.function.BiConsumer;

@Service
public class FilesToSendService implements CrudService<FIlesToSend> {

    private static final String NO_SE_PUEDEN_BORRAR_PROGRAMACIONES_YA_ENVIADAS = "No se pueden borrar Programaciones ya enviadas.";
    //    private MyAuditEventComponent auditEvent;
    private static String UI_CODE = "SERV";
    private FilesToSendRepository filesToSendRepository;
    private final MyAuditEventComponent auditEvent;

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

    //    private static final Set<OrderState> notAvailableStates = Collections.unmodifiableSet(
//            EnumSet.complementOf(EnumSet.of(OrderState.DELIVERED, OrderState.READY, OrderState.CANCELLED)));
    @Transactional(rollbackOn = Exception.class)
    public FIlesToSend saveFileToSend(User currentUser, Long id, BiConsumer<User, FIlesToSend> fileToSendFiller) {
        FIlesToSend fIlesToSend;
        if (id == null) {
            fIlesToSend = new FIlesToSend();
        } else {
            fIlesToSend = load(id);
        }
        fileToSendFiller.accept(currentUser, fIlesToSend);
        return filesToSendRepository.save(fIlesToSend);
    }

    @Transactional(rollbackOn = Exception.class)
    public FIlesToSend saveFileToSend(FIlesToSend fIlesToSend) {
        return filesToSendRepository.save(fIlesToSend);
    }

    //    @Override
//    public FIlesToSend save(User currentUser, FIlesToSend entity) {
//        try {
//            return FilterableCrudService.super.save(currentUser, entity);
//        } catch (DataIntegrityViolationException e) {
//            throw new UserFriendlyDataException(
//                    "There is already a Masivo Agendado with that name. Please select a unique name for the Masivo a enviar.");
//        }
//    }
    @Override
    public FIlesToSend save(User currentUser, FIlesToSend fIlesToSend) {
        if (fIlesToSend == null) {
            log.warn("[{}] FilesToSend is null", Application.getAPP_NAME());
            return null;
        }
        Long id = fIlesToSend.getId();
        FIlesToSend f = CrudService.super.save(currentUser, fIlesToSend);
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

//    public FIlesToSend save(FIlesToSend fIlesToSend, String user) {
//        FIlesToSend f = null;
//        if (fIlesToSend == null) {
//            log.warn("[{}] FilesToSend is null", Application.getAPP_NAME());
//            return f;
//        }
//        try {
//            Long id = fIlesToSend.getId();
//            f = filesToSendRepository.save(fIlesToSend);
////            ODashAuditEvent.OEVENT_TYPE t = ODashAuditEvent.OEVENT_TYPE.CREATE_RECADO;
//            if (id == null) {
//                log.info("[{}] SAVED: ORDER NAME [{}] STATUS [{} - {}] BEING PRO[{}] READY TO SEND[{}]", Application.getAPP_NAME(),
//                        fIlesToSend.getOrderName(),
//                        fIlesToSend.getStatus().name(),
//                        fIlesToSend.getStatusText(),
//                        fIlesToSend.isBeingProcessed(),
//                        fIlesToSend.isReadyToSend());
////                t = ODashAuditEvent.OEVENT_TYPE.CREATE_RECADO;
////                try {
////                    auditEvent.add(ODashAuditEvent.OEVENT_TYPE.CREATE_RECADO, files);
////                } catch (Exception e) {
////                    log.error("", e);
////                }
//            } else {
//                log.info("[{}] UPDATED: ORDER NAME [{}] STATUS [{} - {}] BEING PRO[{}] READY TO SEND[{}]", Application.getAPP_NAME(),
//                        fIlesToSend.getOrderName(),
//                        fIlesToSend.getStatus().name(),
//                        fIlesToSend.getStatusText(),
//                        fIlesToSend.isBeingProcessed(),
//                        fIlesToSend.isReadyToSend());
////                t = ODashAuditEvent.OEVENT_TYPE.UPDATE_RECADO;
////                try {
////                    auditEvent.add(ODashAuditEvent.OEVENT_TYPE.UPDATE_RECADO, files);
////                } catch (Exception e) {
////                    log.error("", e);
////                }
//            }
////            try {
////                auditEvent.add(t, f, user);
////            } catch (Exception e) {
////                log.error("", e);
////            }
//        } catch (Exception d) {
//            log.error("[{}] Error on Save:", Application.getAPP_NAME());
//            log.error("", d);
//        }
//        return f;
//    }

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

    @Transactional
    public List<FileToSendSummary> findAnyMatchingStartingToday() {
        return filesToSendRepository.findFIlesToSendByDateToSendGreaterThanEqual(new Date());
    }

    public long countAnyMatchingAfterDateToSend(Optional<String> optionalFilter, Optional<Date> optionalFilterDate) {
        if (optionalFilter.isPresent() && optionalFilterDate.isPresent()) {
            return filesToSendRepository.countAllByOrderNameContainingIgnoreCaseAndDateToSendAfter(optionalFilter.get(),
                    optionalFilterDate.get());
        } else if (optionalFilter.isPresent()) {
            return filesToSendRepository.countAllByOrderNameContainingIgnoreCase(optionalFilter.get());
        } else if (optionalFilterDate.isPresent()) {
            return filesToSendRepository.countAllByDateToSendAfter(optionalFilterDate.get());
        } else {
            return filesToSendRepository.count();
        }
    }


//    private DeliveryStats getDeliveryStats() {
//        DeliveryStats stats = new DeliveryStats();
//        LocalDate today = LocalDate.now();
//        stats.setDueToday((int) orderRepository.countByDueDate(today));
//        stats.setDueTomorrow((int) orderRepository.countByDueDate(today.plusDays(1)));
//        stats.setDeliveredToday((int) orderRepository.countByDueDateAndStateIn(today,
//                Collections.singleton(OrderState.DELIVERED)));
//
//        stats.setNotAvailableToday((int) orderRepository.countByDueDateAndStateIn(today, notAvailableStates));
//        stats.setNewOrders((int) orderRepository.countByState(OrderState.NEW));
//
//        return stats;
//    }

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
        try {
            auditEvent.add(ODashAuditEvent.OEVENT_TYPE.DELETE_AGENDA, entity);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void throwIfDeletingFileAlreadySended(User currentUser, FIlesToSend entity) {
        if (entity.getStatus()==Status.COMPLETED){
            throw new UserFriendlyDataException(NO_SE_PUEDEN_BORRAR_PROGRAMACIONES_YA_ENVIADAS);
        }
    }
//    @Override
//    public Page<FIlesToSend> findAnyMatching(Optional<String> filter, Pageable pageable) {
//        return null;
//    }
//
//    @Override
//    public long countAnyMatching(Optional<String> filter) {
//        return 0;
//    }
}
