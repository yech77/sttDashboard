package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.AbstractEntitySequence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OFilterableCrudService<T extends AbstractEntitySequence> extends OCrudService<T> {

	Page<T> findAnyMatching(Optional<String> filter, Pageable pageable);

	long countAnyMatching(Optional<String> filter);

}
