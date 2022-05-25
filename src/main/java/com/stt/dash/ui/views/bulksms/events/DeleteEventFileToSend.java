package com.stt.dash.ui.views.bulksms.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class DeleteEventFileToSend extends ComponentEvent<Component> {

    public DeleteEventFileToSend(Component source) {
        super(source, false);
    }
}