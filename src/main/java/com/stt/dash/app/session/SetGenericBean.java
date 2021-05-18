package com.stt.dash.app.session;

import com.stt.dash.backend.data.entity.SystemId;

import java.util.Set;

@FunctionalInterface
public interface SetGenericBean<E> {
    Set<E> getSet();
}
