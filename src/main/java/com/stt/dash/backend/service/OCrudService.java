package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.AbstractEntitySequence;
import com.stt.dash.backend.data.entity.OUser;
import com.stt.dash.backend.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;

public interface OCrudService<T extends AbstractEntitySequence> {

	JpaRepository<T, Long> getRepository();

	default T save(OUser currentUser, T entity) {
		return getRepository().saveAndFlush(entity);
	}

	default void delete(OUser currentUser, T entity) {
		if (entity == null) {
			throw new EntityNotFoundException();
		}
		getRepository().delete(entity);
	}

	default void delete(OUser currentUser, long id) {
		delete(currentUser, load(id));
	}

	default long count() {
		return getRepository().count();
	}

	default T load(long id) {
		T entity = getRepository().findById(id).orElse(null);
		if (entity == null) {
			throw new EntityNotFoundException();
		}
		return entity;
	}

	T createNew(OUser currentUser);
}
