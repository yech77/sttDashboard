package com.stt.dash.ui.views.bulksms;

import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.User;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

public class BulkSmsForm extends FormLayout {
    Binder<Agenda> binder = new BeanValidationBinder<>(Agenda.class);
    TextField name = new TextField();

    public BulkSmsForm() {
        setResponsiveSteps(
                new ResponsiveStep("25em", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("32em", 2, ResponsiveStep.LabelsPosition.TOP));
        binder.bind(name, "name");
        add(name);
    }

    public Binder<Agenda> getBinder(){
        return binder;
    }
}
