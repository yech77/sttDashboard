package com.stt.dash.ui.views.bulksms;

import com.stt.dash.backend.data.OSystemIdSession;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.SystemId;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class BulkSmsSchedulerForm extends FormLayout {
    Binder<FormBean> binder = new BeanValidationBinder<>(FormBean.class);
    List<Agenda> agendaList;
    Collection<SystemId> systemIdCollection;
    /**/
    private DateTimePicker dateTimePicker = new DateTimePicker();
    private ComboBox<Agenda> agendaCombo = new ComboBox<>();
    private ComboBox<OSystemIdSession> systemIdCombo = new ComboBox<>();

    public BulkSmsSchedulerForm(List<Agenda> agendaList, Collection<SystemId> systemIdCollection) {
        setResponsiveSteps(
                new ResponsiveStep("25em", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("32em", 2, ResponsiveStep.LabelsPosition.TOP));
        this.agendaList = agendaList;
        this.systemIdCollection = systemIdCollection;

    }

    public Binder<FormBean> getBinder(){
        return binder;
    }

    /**
     * Clase para este form
     */
    private static class FormBean {
        private String nameBox = "";
        private String descriptionBox = "";
        private String messageBox = "";
        private LocalDateTime dateTimePicker = LocalDateTime.now();
        private Agenda agendaCombo = null;
        private OSystemIdSession systemIdCombo = null;
        private boolean sendNow = false;

        public String getNameBox() {
            return nameBox;
        }

        public void setNameBox(String nameBox) {
            this.nameBox = nameBox;
        }

        public String getDescriptionBox() {
            return descriptionBox;
        }

        public void setDescriptionBox(String descriptionBox) {
            this.descriptionBox = descriptionBox;
        }

        public String getMessageBox() {
            return messageBox;
        }

        public void setMessageBox(String messageBox) {
            this.messageBox = messageBox;
        }

        public LocalDateTime getDateTimePicker() {
            return dateTimePicker;
        }

        public void setDateTimePicker(LocalDateTime dateTimePicker) {
            this.dateTimePicker = dateTimePicker;
        }

        public Agenda getAgendaCombo() {
            return agendaCombo;
        }

        public void setAgendaCombo(Agenda agendaCombo) {
            this.agendaCombo = agendaCombo;
        }

        public OSystemIdSession getSystemIdCombo() {
            return systemIdCombo;
        }

        public void setSystemIdCombo(OSystemIdSession systemIdCombo) {
            this.systemIdCombo = systemIdCombo;
        }

        public boolean isSendNow() {
            return sendNow;
        }

        public void setSendNow(boolean sendNow) {
            this.sendNow = sendNow;
        }

    }

}
