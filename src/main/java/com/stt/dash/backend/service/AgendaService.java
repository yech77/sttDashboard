package com.stt.dash.backend.service;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.MyAuditEventComponent;
import com.stt.dash.backend.data.entity.ODashAuditEvent;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.AgendaRepository;
import com.stt.dash.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AgendaService implements FilterableCrudService<Agenda> {

    public static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "El Usuario está bloqueado para cambios.";
    private static final String DELETING_SELF_NOT_PERMITTED = "No puedes borrar tu cuenta.";

    /**/
//    private final MyAuditEventComponent auditEvent;
    private static String UI_CODE = "SERV_AGEN";
    private static final Logger log = LoggerFactory.getLogger(AgendaService.class.getName());
    private AgendaRepository repo;
    private UserRepository ouser_repo;
    private final MyAuditEventComponent auditEvent;

    public AgendaService(AgendaRepository repo,
                         UserRepository ouser_repo,
                         MyAuditEventComponent auditEvent) {
        this.repo = repo;
        this.ouser_repo = ouser_repo;
        this.auditEvent = auditEvent;
    }

    private String getStringLog() {
        //String id = VaadinSession.getCurrent().getSession().getId();
        String id = "THREAD";
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(id).append("] [").append(UI_CODE).append("]");
        return sb.toString();
    }

    public long count() {
        return repo.count();
    }

    public void delete(Agenda agenda) {
        try {
            repo.delete(agenda);
            auditEvent.add(ODashAuditEvent.OEVENT_TYPE.DELETE_AGENDA, agenda);
            log.info("{} Deleted: [{}]", getStringLog(), agenda.getName());
        } catch (Exception d) {
            log.error("{} Error on Delete [{}]:", getStringLog(), agenda.getName());
            log.error("", d);
        }
    }

    /**
     * Utilizado por el hilo cuando va Vallidando.
     *
     * @param agenda
     * @deprecated
     */
    public void updateState(Agenda agenda) {
        if (agenda == null) {
            log.warn("{} Agenda is null", getStringLog());
            return;
        }
        try {
            repo.save(agenda);
        } catch (Exception d) {
            log.error("{} Error on Save:", getStringLog());
            log.error("", d);
        }
    }

    /**
     * Utilizado por el hilo cuando va Vallidando.
     *
     * @param agenda
     */
    public Agenda updateState(CurrentUser currentUser, Agenda agenda) {
        return FilterableCrudService.super.save(null, agenda);
    }

    public Optional<Agenda> findById(Long agendaId) {
        return repo.findById(agendaId);
    }

    /**
     * Utilizado para salvar nueva Agenda o modificar algun dato de la agenda.
     *
     * @param agenda
     */
    public void save(Agenda agenda) {
        if (agenda == null) {
            log.warn("{} Agenda is null", getStringLog());
            return;
        }
        try {
            Long id = agenda.getId();
            repo.save(agenda);
            if (id == null) {
                log.info("{} Saved: Agenda[{}]", getStringLog(), agenda.getName());
                try {
                    auditEvent.add(ODashAuditEvent.OEVENT_TYPE.CREATE_AGENDA, agenda);
                } catch (Exception e) {
                    log.error("", e);
                }
            } else {
                log.info("{} Updated: Agenda[{}]", getStringLog(), agenda.getName());
                try {
                    auditEvent.add(ODashAuditEvent.OEVENT_TYPE.UPDATE_AGENDA, agenda);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        } catch (Exception d) {
            log.error("{} Error on Save:", getStringLog());
            log.error("", d);
        }
    }

    public List<Agenda> getAllAgendasInFamily(List<User> users) {
        return repo.getAllAgendasInFamily(users);
    }

    public List<Agenda> getAllValidAgendasInFamily(List<User> users) {
        return repo.getAllValidAgendasInFamily(users, Agenda.Status.READY_TO_USE);
    }

    public List<Agenda> findByName(String name) {
        return repo.findByName(name);
    }

//    public List<Agenda> getAllAgendas(OUserSession ouser_session){
//        List<Agenda> users = session_utils.getSelfAndChildren();
//        return getAllAgendasInFamily(users);
//    }
    /**/

    public Page<Agenda> findAnyMatching(Optional<String> filter, Pageable pageable) {
        if (filter.isPresent()) {
            String repositoryFilter = "%" + filter.get() + "%";
            return getRepository()
                    .findByCreator_EmailLikeIgnoreCaseOrNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(
                            repositoryFilter, repositoryFilter, repositoryFilter, pageable);
        } else {
            return find(pageable);
        }
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        if (filter.isPresent()) {
            String repositoryFilter = "%" + filter.get() + "%";
            return repo.countByCreator_EmailLikeIgnoreCaseOrNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(
                    repositoryFilter, repositoryFilter, repositoryFilter);
        } else {
            return count();
        }
    }

    @Override
    public AgendaRepository getRepository() {
        return repo;
    }

    public Page<Agenda> find(Pageable pageable) {
        return getRepository().findBy(pageable);
    }

    @Override
    public Agenda save(User currentUser, Agenda entity) {
        try {
            try {
                Long id = entity.getId();
                entity = FilterableCrudService.super.save(currentUser, entity);
                if (id == null) {
                    log.info("{} Saved: Agenda[{}]", getStringLog(), entity.getName());
                    try {
                        auditEvent.add(ODashAuditEvent.OEVENT_TYPE.CREATE_AGENDA, entity);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                } else {
                    log.info("{} Updated: Agenda[{}]", getStringLog(), entity.getName());
                    try {
                        auditEvent.add(ODashAuditEvent.OEVENT_TYPE.UPDATE_AGENDA, entity);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            } catch (Exception d) {
                log.error("{} Error on Save:", getStringLog());
                log.error("", d);
            }
            return entity;
        } catch (DataIntegrityViolationException e) {
            throw new UserFriendlyDataException(
                    "Ya existe una agenda con ese nombe. Por favor seleccione otro nombre.");
        }
    }

    @Override
    @Transactional
    public void delete(User currentUser, Agenda agenda) {
        FilterableCrudService.super.delete(currentUser, agenda);
        try {
            auditEvent.add(ODashAuditEvent.OEVENT_TYPE.DELETE_AGENDA, agenda);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void throwIfDeletingSelf(User currentUser, Agenda user) {
        if (currentUser.equals(user)) {
            throw new UserFriendlyDataException(DELETING_SELF_NOT_PERMITTED);
        }
    }

    private void throwIfUserLocked(User entity) {
        if (entity != null && entity.isLocked()) {
            throw new UserFriendlyDataException(MODIFY_LOCKED_USER_NOT_PERMITTED);
        }
    }

    @Override
    public Agenda createNew(User currentUser) {
        return new Agenda();
    }

}
