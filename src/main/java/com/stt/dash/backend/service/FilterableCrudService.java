package com.stt.dash.backend.service;

import java.util.Optional;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.AbstractEntitySequence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterableCrudService<T extends AbstractEntitySequence> extends CrudService<T> {

	Page<T> findAnyMatching(Optional<String> filter, Pageable pageable);

	long countAnyMatching(Optional<String> filter);

	default Page<T> findAnyMatching(CurrentUser currentUser, Optional<String> filter, Pageable pageable){
		return findAnyMatching(filter, pageable);
	}

	default long countAnyMatching(CurrentUser currentUser, Optional<String> filter){
		return countAnyMatching(filter);
	}

}
