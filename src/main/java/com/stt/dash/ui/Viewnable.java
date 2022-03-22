package com.stt.dash.ui;

import com.vaadin.flow.data.provider.ListDataProvider;
import liquibase.pro.packaged.T;

import java.util.Collection;

public interface Viewnable<T> {
    void setGridDataProvider(ListDataProvider<T> dataProvider);

    void updateDownloadButton(Collection<T> messages);
}
