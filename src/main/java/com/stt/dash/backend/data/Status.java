package com.stt.dash.backend.data;

import com.vaadin.flow.shared.util.SharedUtil;

import java.util.Locale;

public enum Status {
    VALIDATING,
    GENERATING_MESSAGES,
    PREPARING_SMS,
    WAITING_TO_SEND,
    SENDING,
    COMPLETED,
    INVALID;

    /**
     * Gets a version of the enum identifier in a human friendly format.
     *
     * @return a human friendly version of the identifier
     */
    public String getDisplayName() {
        return SharedUtil.capitalize(name().toLowerCase(Locale.ENGLISH));
    }
}
