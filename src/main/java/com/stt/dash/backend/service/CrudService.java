package com.stt.dash.backend.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stt.dash.backend.data.entity.AbstractEntitySequence;
import com.stt.dash.backend.data.entity.User;

public interface CrudService<T extends AbstractEntitySequence> {

	JpaRepository<T, Long> getRepository();

	default T save(User currentUser, T entity) {
		T t;
		try {
			t = getRepository().saveAndFlush(entity);
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		return t;
	}
//	default T save(T entity) {
//		T t;
//		try {
//			t = getRepository().saveAndFlush(entity);
//		}catch (Exception e){
//			e.printStackTrace();
//			throw e;
//		}
//		return t;
//	}

	default void delete(User currentUser, T entity) {
		if (entity == null) {
			throw new EntityNotFoundException();
		}
		getRepository().delete(entity);
	}

	default void delete(User currentUser, long id) {
		delete(currentUser, load(id));
	}

	default long count() {
		return getRepository().count();
	}

	default long count(long count) {
		return count;
	}

	default T load(long id) {
		System.out.println("CARGANDO!!!!!!!!");
		T entity = getRepository().findById(id).orElse(null);
		if (entity == null) {
			throw new EntityNotFoundException();
		}
		return entity;
	}

	T createNew(User currentUser);
}
