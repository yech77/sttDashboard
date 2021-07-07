package com.stt.dash.backend.service;

import java.util.*;

import com.stt.dash.app.security.CurrentUser;
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

	public static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "User has been locked and cannot be modified or deleted";
	private static final String DELETING_SELF_NOT_PERMITTED = "You cannot delete your own account";
	private final UserRepository userRepository;
	private final MyAuditEventComponent audit;
	public UserService(UserRepository userRepository, MyAuditEventComponent audit) {
		this.userRepository = userRepository;
		this.audit=audit;
	}
	private long isotherCounter =-1;

	public Page<User> findAnyMatching(Optional<String> filter, Pageable pageable) {
		if (filter.isPresent()) {
			String repositoryFilter = "%" + filter.get() + "%";
			return getRepository()
					.findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
							repositoryFilter, repositoryFilter, repositoryFilter, repositoryFilter, pageable);
		} else {
			return find(pageable);
		}
	}

	@Override
	public Page<User> findAnyMatching(CurrentUser currentUser, Optional<String> filter, Pageable pageable) {
		if (filter.isPresent()) {
			String repositoryFilter = "%" + filter.get() + "%";
			return getRepository()
					.findByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
							repositoryFilter, repositoryFilter, repositoryFilter, repositoryFilter, pageable);
		} else {
			return find(currentUser, pageable);
		}
	}

	@Override
	public long countAnyMatching(Optional<String> filter) {
		if (filter.isPresent()) {
			String repositoryFilter = "%" + filter.get() + "%";
			return userRepository.countByEmailLikeIgnoreCaseOrFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCaseOrRoleLikeIgnoreCase(
					repositoryFilter, repositoryFilter, repositoryFilter, repositoryFilter);
		} else {
			if(isotherCounter>-1){
				long d = isotherCounter;
				isotherCounter=-1;
				return d;
			}
			return count();
		}
	}

	@Override
	public UserRepository getRepository() {
		return userRepository;
	}

	public Page<User> find(Pageable pageable) {
		return getRepository().findBy(pageable);
	}

	public Page<User> find(CurrentUser currentUser, Pageable pageable) {
		if (currentUser.getUser().getUserType() == User.OUSER_TYPE.HAS){
			Page<User> p =  getRepository().findAllByUserParentIsNotNull(pageable);
			isotherCounter = p.getTotalElements();
			return p;
		}
		List<User> lu = getUserFamily(currentUser.getUser());
		/* es para indicar que el counter debe contar este*/
		isotherCounter=lu.size();
		Page<User> u = new PageImpl<>(lu);
		return u;
	}

	@Override
	public User save(User currentUser, User entity) {
		boolean isNew = entity.getId()==null?true:false;
		throwIfUserLocked(entity);
		User u = FilterableCrudService.super.save(currentUser, entity);
		if (isNew) {
			audit.add(ODashAuditEvent.OEVENT_TYPE.CREATE_USER, entity);
		}else{
			/* Buscar el usuario original y comparar??? */
			audit.add(ODashAuditEvent.OEVENT_TYPE.UPDATE_USER, entity);
		}
		return u;
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

	@Override
	public User createNew(User currentUser) {
		return new User();
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
		System.out.println("Usuarios en la familia de " + currentUser.getEmail() );

		return allUsers;
	}
}
