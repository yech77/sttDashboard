package com.stt.dash.ui.crud;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.data.binder.Binder;

public class STTBinderCrudEditor<E> extends BinderCrudEditor<E> implements STTCrudEditor<E> {
    public STTBinderCrudEditor(Binder<E> binder) {
        super(binder);
    }

    public STTBinderCrudEditor(Binder<E> binder, Component view) {
        super(binder, view);
    }
}
