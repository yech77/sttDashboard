package com.stt.dash.ui.views.bulksms;

import com.stt.dash.ui.views.orderedit.OrderEditor;
import com.vaadin.flow.component.ComponentEvent;

public class BulkSmsReviewEvent extends ComponentEvent<FileToSendEditor> {
    public BulkSmsReviewEvent(FileToSendEditor source) {
        super(source, false);
    }
}
