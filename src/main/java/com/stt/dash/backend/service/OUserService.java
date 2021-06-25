package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.OUser;
import com.stt.dash.backend.repositories.OUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OUserService implements OFilterableCrudService<OUser> {

	public static final String MODIFY_LOCKED_USER_NOT_PERMITTED = "El usuario está bloqueado. No se puede eliminar ni modificar.";
	private static final String DELETING_SELF_NOT_PERMITTED = "No puedes borrar tu propia cuenta";
	private final OUserRepository userRepository;

	@Autowired
	public OUserService(OUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Page<OUser> findAnyMatching(Optional<String> filter, Pageable pageable) {
		if (filter.isPresent()) {
			String repositoryFilter = "%" + filter.get() + "%";
			return getRepository().findByUserEmailLikeIgnoreCaseOrUserNameLikeIgnoreCaseOrUserLastnameLikeIgnoreCase(
							repositoryFilter, repositoryFilter, repositoryFilter, pageable);
		} else {
			return find(pageable);
		}
	}

	@Override
	public long countAnyMatching(Optional<String> filter) {
		if (filter.isPresent()) {
			String repositoryFilter = "%" + filter.get() + "%";
			return userRepository.countByUserEmailLikeIgnoreCaseOrUserNameLikeIgnoreCaseOrUserLastnameLikeIgnoreCase(
					repositoryFilter, repositoryFilter, repositoryFilter);
		} else {
			return count();
		}
	}

	@Override
	public OUserRepository getRepository() {
		return userRepository;
	}

	public Page<OUser> find(Pageable pageable) {
		return getRepository().findBy(pageable);
	}

	@Override
	public OUser save(OUser currentUser, OUser entity) {
		throwIfUserLocked(entity);
		return getRepository().saveAndFlush(entity);
	}

	@Override
	@Transactional
	public void delete(OUser currentUser, OUser userToDelete) {
		throwIfDeletingSelf(currentUser, userToDelete);
		throwIfUserLocked(userToDelete);
		OFilterableCrudService.super.delete(currentUser, userToDelete);
	}

	private void throwIfDeletingSelf(OUser currentUser, OUser user) {
		if (currentUser.equals(user)) {
			throw new UserFriendlyDataException(DELETING_SELF_NOT_PERMITTED);
		}
	}

	private void throwIfUserLocked(OUser entity) {
		if (entity != null && entity.isLocked()) {
			throw new UserFriendlyDataException(MODIFY_LOCKED_USER_NOT_PERMITTED);
		}
	}

	@Override
	public OUser createNew(OUser currentUser) {
		return new OUser();
	}

}
