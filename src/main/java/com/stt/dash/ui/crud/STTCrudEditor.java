package com.stt.dash.ui.crud;

import com.vaadin.flow.component.crud.CrudEditor;
import liquibase.pro.packaged.E;

public interface STTCrudEditor<E> extends CrudEditor<E> {
    default void doInUI(OnUI onui) {
    }
}
