package com.stt.dash.ui.views.bulksms.events;

import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.ui.views.bulksms.FileToSendEditor;
import com.stt.dash.ui.views.orderedit.OrderItemEditor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class DeleteEventFileToSend extends ComponentEvent<Component> {

    public DeleteEventFileToSend(Component source) {
        super(source, false);
    }
}