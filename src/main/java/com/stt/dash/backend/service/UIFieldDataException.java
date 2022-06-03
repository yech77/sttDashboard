package com.stt.dash.backend.service;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * A data integrity violation exception containing a message intended to be
 * shown to the end user.
 */
public class UIFieldDataException extends DataIntegrityViolationException {

    public UIFieldDataException(String message) {
        super(message);
    }

}
