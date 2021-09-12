package com.stt.dash.app.session;
import java.util.List;

@FunctionalInterface
public interface ListGenericBean<E> {
    List<E> getList();
}
