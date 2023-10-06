package com.stt.dash.backend.service;

import java.util.*;
import java.util.stream.Collectors;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Client;
import com.stt.dash.backend.data.entity.MyAuditEventComponent;
import com.stt.dash.backend.data.entity.ODashAuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.UserRepository;

@Service
public class UserService implements FilterableCrudService<User> {

    public static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "Usuario bloqueado para modificaciones.";
    private static final String DELETING_SELF_NOT_PERMITTED = "No puedes borrar tu propia cuenta.";
    private final UserRepository userRepository;
    private final MyAuditEventComponent audit;

    public UserService(UserRepository userRepository, MyAuditEventComponent audit) {
        this.userRepository = userRepository;
        this.audit = audit;
    }

    @Override
    public Page<User> findAnyMatching(Optional<String> filter, Pageable pageable) {
        if (filter.isPresent()) {
            String repositoryFilter = "%" + filter.get() + "%";
            return getRepository().findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(repositoryFilter, repositoryFilter, repositoryFilter, repositoryFilter, pageable);
        } else {
            return find(pageable);
        }
    }

    @Override
    public Page<User> findAnyMatching(CurrentUser currentUser, Optional<String> filter, Pageable pageable) {
        if (filter.isPresent()) {
            return find(currentUser, filter, pageable);
        }
        return find(currentUser, pageable);
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        if (filter.isPresent()) {
            String repositoryFilter = "%" + filter.get() + "%";
            return userRepository.countByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(repositoryFilter, repositoryFilter, repositoryFilter, repositoryFilter);
        } else {
            return count();
        }
    }

    @Override
    public long countAnyMatching(CurrentUser currentUser, Optional<String> filter) {
        /* Comercial ve todos los usuarios */
        if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            if (filter.isPresent()) {
                String repositoryFilter = filter.get();
                return getRepository().countAllByUserParentIsNotNullAndEmailIsNotAndFirstNameIsStartingWithOrLastNameIsStartingWith(currentUser.getUser().getEmail(), repositoryFilter, repositoryFilter);
            }
            Long p = getRepository().countAllByUserParentIsNotNullAndEmailIsNot(currentUser.getUser().getEmail());
            return p;
        } else if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS) {
            /* ADMIN_EMPRESAS ve todos los usuarios de su empresa */
            Long p = getRepository().countAllByClientsInAndUserTypeOrdNotAndIdIsNot(currentUser.getUser().getClients(), User.OUSER_TYPE_ORDINAL.COMERCIAL, currentUser.getUser().getId());
            return p;
        }
        /* Usuario no comercial y no admin_empresas solo ve su familia*/
        List<User> lu = getUserFamily(currentUser.getUser());
        long u = lu.size();
        return u;

    }

    public Page<User> findByUserTypeOrdAndClients(User.OUSER_TYPE_ORDINAL userTypeOrd, Client client, Pageable pageable) {
        return getRepository().findByUserTypeOrdAndClients(userTypeOrd, client, pageable);
    }

    public User findByEmailIgnoreCase(String email) {
        return getRepository().findByEmailIgnoreCase(email);
    }

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    public Page<User> find(Pageable pageable) {
        return getRepository().findBy(pageable);
    }

    public Page<User> find(CurrentUser currentUser, Pageable pageable) {
        /* Comercial ve todos los usuarios */
        if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            Page<User> p = getRepository().findAllByUserParentIsNotNullAndEmailIsNot(currentUser.getUser().getEmail(), pageable);
            return p;
        } else if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS) {
            /* ADMIN_EMPRESAS ve todos los usuarios de su empresa */
            Page<User> p = getRepository().findByClientsInAndUserTypeOrdNotAndIdIsNot(currentUser.getUser().getClients(), User.OUSER_TYPE_ORDINAL.COMERCIAL, currentUser.getUser().getId(), pageable);
            return p;
        }
        /* Usuario no comercial y no admin_empresas solo ve su familia*/
        List<User> lu = getUserFamily(currentUser.getUser());
        Page<User> u = new PageImpl<>(lu);
        return u;
    }

    public Page<User> find(CurrentUser currentUser, Optional<String> filter, Pageable pageable) {
        /* Comercial ve todos los usuarios */
        if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            return getRepository().findAllByUserParentIsNotNullAndEmailIsNotAndFirstNameIsStartingWithOrLastNameIsStartingWith(currentUser.getUser().getEmail(),
                    filter.get(), filter.get(), pageable);
        } else if (currentUser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.ADMIN_EMPRESAS) {
            /* ADMIN_EMPRESAS ve todos los usuarios de su empresa */
            return getRepository().findByClientsInAndUserTypeOrdNotAndIdIsNotAndFirstNameIsStartingWithOrLastNameIsStartingWith(currentUser.getUser().getClients(),
                    User.OUSER_TYPE_ORDINAL.COMERCIAL,
                    currentUser.getUser().getId(),
                    filter.get(),
                    filter.get(),
                    pageable);
        }
        /* Usuario no comercial y no admin_empresas solo ve su familia*/
        List<User> lu = getUserFamily(currentUser.getUser())
                .stream()
                .filter(u -> u.getFirstName().startsWith(filter.get().toLowerCase()) || u.getLastName().startsWith(filter.get().toLowerCase()))
                .collect(Collectors.toList());
        return new PageImpl<>(lu);
    }

    @Override
    public User save(User currentUser, User entity) {
        boolean isNew = entity.getId() == null;
        throwIfUserLocked(entity);
        throwIfComercialTryToModifyComerciald(currentUser, entity);
        User u = FilterableCrudService.super.save(currentUser, entity);
        if (isNew) {
            audit.add(ODashAuditEvent.OEVENT_TYPE.CREATE_USER, entity);
        } else {
            /* Buscar el usuario original y comparar??? */
            audit.add(ODashAuditEvent.OEVENT_TYPE.UPDATE_USER, entity);
        }
        return u;
    }

    public User changePassword(User user) {
        audit.add(ODashAuditEvent.OEVENT_TYPE.UPDATE_USER, user);
        return getRepository().saveAndFlush(user);
    }

    /*public User save(User currentUser, User entity, String changes) {
        throwIfUserLocked(entity);
        User u = getRepository().saveAndFlush(entity);
        audit.add(ODashAuditEvent.OEVENT_TYPE.UPDATE_USER, entity, changes);
        return u;
    }*/

    @Override
    @Transactional
    public void delete(User currentUser, User userToDelete) {
        throwIfDeletingSelf(currentUser, userToDelete);
        throwIfUserLocked(userToDelete);
        FilterableCrudService.super.delete(currentUser, userToDelete);
        audit.add(ODashAuditEvent.OEVENT_TYPE.DELETE_USER, userToDelete);
    }

    private void throwIfDeletingSelf(User currentUser, User user) {
        if (currentUser.equals(user)) {
            throw new UserFriendlyDataException(DELETING_SELF_NOT_PERMITTED);
        }
    }

    private void throwIfUserLocked(User entity) {
        if (entity != null && entity.isLocked()) {
            throw new UserFriendlyDataException(MODIFY_LOCKED_USER_NOT_PERMITTED);
        }
    }

    private void throwIfComercialTryToModifyComerciald(User currentUser, User userToSave) {
        if (userToSave.getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL && currentUser.getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            throw new UserFriendlyDataException("Debe entrar como usuario Administrador para modificar un usuario Comercial");
        }
    }

    @Override
    public User createNew(User currentUser) {
        return new User();
    }

    @Transactional
    public User deactivateUser(String userEmail, String blockReason) {
        User user = getRepository().findByEmailIgnoreCase(userEmail);
        /* no desactiva el usuario nuevamente si ya esta desactivado. */
        if (!user.isActive()) {
            return user;
        }
        user.setActive(false);
        audit.add(ODashAuditEvent.OEVENT_TYPE.BLOCKED, userEmail + ": " + blockReason);
        return getRepository().save(user);
    }

    private List<User> getUserFamily(User currentUser) {
        List<User> allUsers = new ArrayList<>();
        List<User> currentFam = new ArrayList<>();
        List<User> addingChildren = new ArrayList<>();

//		currentFam.add(currentUser.getUser());
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
