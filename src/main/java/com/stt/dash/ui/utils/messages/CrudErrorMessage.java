package com.stt.dash.ui.utils.messages;

public final class CrudErrorMessage {
    public static final String ENTITY_NOT_FOUND = "La entidad seleccionada no existe.";

    public static final String CONCURRENT_UPDATE = "Alhuien mas a actualizado la data. Por favor refresque he intente nuevamente.";

    public static final String OPERATION_PREVENTED_BY_REFERENCES = "Ya existe una referencia con ese nombre";

    public static final String REQUIRED_FIELDS_MISSING = "Please fill out all required fields before proceeding.";

    private CrudErrorMessage() {
    }
}
