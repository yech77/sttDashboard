package com.stt.dash.backend.data;

import com.vaadin.flow.shared.util.SharedUtil;

import java.util.Locale;

public enum Status {
    VALIDATING("Validando"),
    GENERATING_MESSAGES("Generando"),
    PREPARING_SMS("Preparando"),
    WAITING_TO_SEND("Esperando"),
    SENDING("Enviando"),
    COMPLETED("Enviados"),
    INVALID("Invalido");

    private String text;

    Status(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    /**
     * Gets a version of the enum identifier in a human friendly format.
     *
     * @return a human friendly version of the identifier
     */
    public String getDisplayName() {
        return SharedUtil.capitalize(name().toLowerCase(Locale.ENGLISH));
    }
}
