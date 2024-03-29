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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgendaService implements FilterableCrudService<Agenda> {

    public static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "El Usuario está bloqueado para cambios.";
    private static final String DELETING_SELF_NOT_PERMITTED = "No puedes borrar tu cuenta.";
    private static final Logger log = LoggerFactory.getLogger(AgendaService.class.getName());
    /**/
//    private final MyAuditEventComponent auditEvent;
    private static final String UI_CODE = "SERV_AGEN";
    private final MyAuditEventComponent auditEvent;
    private final AgendaRepository repo;
    private final UserRepository ouser_repo;

    @Autowired
    public AgendaService(AgendaRepository repo, UserRepository ouser_repo, MyAuditEventComponent auditEvent) {
        this.repo = repo;
        this.ouser_repo = ouser_repo;
        this.auditEvent = auditEvent;
    }

    private String getStringLog() {
        //String id = VaadinSession.getCurrent().getSession().getId();
        String id = "THREAD";
        return '[' + id + "] [" + UI_CODE + "]";
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(CurrentUser currentUser) {
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

    /**
     * Utilizado para obtener todas las agendas que estan en estado READY_TO_USE cuando se
     * va a crear una Programacion de masivo.
     *
     * @param currentUser
     * @param users
     * @return
     */
    public List<Agenda> getAllValidAgendasInFamily(CurrentUser currentUser, List<User> users) {
        if (currentUser.getUser().getUserTypeOrd() != User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            return repo.getAllValidAgendasInFamily(users, Agenda.Status.READY_TO_USE);
        } else {
            return repo.findAllByStatusOrderByDateCreatedDesc(Agenda.Status.READY_TO_USE, PageRequest.ofSize(1000)).getContent();
        }
    }

    public List<Agenda> getAllAgendasInFamily(CurrentUser currentUser, List<User> users) {
        if (currentUser.getUser().getUserTypeOrd() != User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            return repo.getAllAgendasInFamily(users);
        } else {
            return repo.findAll(Sort.by(Sort.Direction.DESC, "dateCreated"));
        }
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
            return getRepository().findByCreator_EmailLikeIgnoreCaseOrNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(repositoryFilter, repositoryFilter, repositoryFilter, pageable);
        } else {
            return find(pageable);
        }
    }

    /**
     * Busca las agendas que pertenecen al usuario actual y a sus hijos o todas las agendas si es un comercial.
     *
     * @param currentUser
     * @param filter
     * @param pageable
     * @return
     */
    @Override
    public Page<Agenda> findAnyMatching(CurrentUser currentUser, Optional<String> filter, Pageable pageable) {
        Page<Agenda> myAgendasAndMyAgendasSon;
        if (filter.isPresent()) {
            myAgendasAndMyAgendasSon = findAgendasByUserType(currentUser, filter, pageable);
        } else {
            myAgendasAndMyAgendasSon = findAgendasByUserType(currentUser, pageable);
        }
        return myAgendasAndMyAgendasSon;
    }

    @Override
    public long countAnyMatching(CurrentUser currentUser, Optional<String> filter) {
        Long totalAgendas;
        if (currentUser.getUser().getUserTypeOrd() != User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            totalAgendas = countAgenda(currentUser, filter);
        } else {
            totalAgendas = countAgendaForCommercial(filter);
        }
        return totalAgendas;
    }

    private Long countAgendaForCommercial(Optional<String> filter) {
        Long totalAgendas;
        if (filter.isPresent()) {
            totalAgendas =
                    getRepository().countAllByNameIsStartingWithOrDescriptionIsStartingWith(filter.get(), filter.get());
        } else {
            totalAgendas =
                    getRepository().count();
        }
        return totalAgendas;
    }

    private Long countAgenda(CurrentUser currentUser, Optional<String> filter) {
        Long totalAgendas;
        if (filter.isPresent()) {
            totalAgendas = getRepository().countAgendaByCreatorInAndNameIsStartingWithOrDescriptionIsStartingWith(getMeAndSon(currentUser.getUser()), filter.get(), filter.get());
        } else {
            totalAgendas = getRepository().countAgendaByCreatorIn(getMeAndSon(currentUser.getUser()));
        }
        return totalAgendas;
    }

    private Page<Agenda> findAgendasByUserType(CurrentUser currentUser, Pageable pageable) {
        Page<Agenda> myAgendasAndMyAgendasSon;
        if (currentUser.getUser().getUserTypeOrd() != User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            myAgendasAndMyAgendasSon = getRepository().findAllByCreatorIn(getMeAndSon(currentUser.getUser()), pageable);
        } else {
            myAgendasAndMyAgendasSon = getRepository().findAll(pageable);
        }
        return myAgendasAndMyAgendasSon;
    }

    private Page<Agenda> findAgendasByUserType(CurrentUser currentUser, Optional<String> filter, Pageable pageable) {
        Page<Agenda> myAgendasAndMyAgendasSon;
        if (currentUser.getUser().getUserTypeOrd() != User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            myAgendasAndMyAgendasSon = getRepository().findAllByCreatorInAndNameIsStartingWithOrDescriptionIsStartingWith(getMeAndSon(currentUser.getUser()), filter.get(), filter.get(), pageable);
        } else {
            myAgendasAndMyAgendasSon = getRepository().findAllByNameIsStartingWithOrDescriptionIsStartingWith(filter.get(), filter.get(), pageable);
        }
        return myAgendasAndMyAgendasSon;
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        return 0;
    }

    @Override
    public AgendaRepository getRepository() {
        return repo;
    }

    public Page<Agenda> find(Pageable pageable) {
        return getRepository().findBy(pageable);
    }

    @Override
    public Agenda save(User currentUser, @NotNull Agenda entity) {
        /* Si id es nulo es un save y no una actualizacion */
        boolean isNewEntity = Objects.isNull(entity.getId());
        if (isNewEntity) {
            if (existAlreadyAgendaName(entity.getName())) {
                throw new UIFieldDataException("Ya existe");
            }
        }
        try {
            entity = FilterableCrudService.super.save(currentUser, entity);
            if (isNewEntity) {
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
        } catch (DataIntegrityViolationException e) {
            throw new UserFriendlyDataException("Ya existe una agenda con ese nombre. Por favor seleccione otro nombre.");
        } catch (Exception d) {
            log.error("", d);
            throw new UserFriendlyDataException("Hubo un error y no se pudo salvar la agenda");
        }
        return entity;
    }

    @Override
    @Transactional
    public void delete(User currentUser, Agenda agenda) {
        FilterableCrudService.super.delete(currentUser, agenda);
        try {
            auditEvent.add(ODashAuditEvent.OEVENT_TYPE.DELETE_AGENDA, agenda);
        } catch (Exception e) {
            log.error("", e);
            throw new UserFriendlyDataException("Hubo un error y no se pudo borar la agenda");
        }
    }

    @Override
    public Agenda createNew(User currentUser) {
        return new Agenda();
    }

    private Boolean existAlreadyAgendaName(@NotNull String name) {
        /* buscar si existe el nombre */
        return !repo.findByName(name).isEmpty();
    }

    private List<User> getMeAndSon(User currentUser) {
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
