package com.stt.dash.ui.crud;

import com.stt.dash.backend.data.entity.AbstractEntitySequence;

/**
 * Interface en la cual sus ejecuciones ocurren en los forms que la implementan de ella.
 */
public interface OnUIForm<E> {
    default void onUI() {

    }

    default void onFieldUI() {

    }

    /**
     * Manera de ejecutar una tarea en el Form donde se salvo el entity.
     *
     * @param idBeforeSave
     * @param entity
     */
    default void onSaveUI(long idBeforeSave, E entity) {
    }

    ;
}
