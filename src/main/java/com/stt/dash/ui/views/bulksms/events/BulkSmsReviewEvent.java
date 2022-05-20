package com.stt.dash.ui.views.bulksms.events;

import com.stt.dash.ui.views.bulksms.FileToSendEditorView;
import com.vaadin.flow.component.ComponentEvent;

public class BulkSmsReviewEvent extends ComponentEvent<FileToSendEditorView> {
    public BulkSmsReviewEvent(FileToSendEditorView source) {
        super(source, false);
    }
}
