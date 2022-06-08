package com.stt.dash.ui.crud;

import com.vaadin.flow.component.crud.CrudEditor;

public interface STTCrudEditor<E> extends CrudEditor<E> {
    default void doInUI(OnUIForm onui) {
    }
}
