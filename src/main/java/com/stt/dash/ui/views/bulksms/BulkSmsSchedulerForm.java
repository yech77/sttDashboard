package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.OSystemIdSession;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.SystemId;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class BulkSmsSchedulerForm extends FormLayout {
    Binder<FIlesToSend> binder = new BeanValidationBinder<>(FIlesToSend.class);
    /**/
    private TextField orderName = new TextField();
    private TextField orderDescription = new TextField();
    private TextArea messageBox = new TextArea();
    private Paragraph messageBuilded = new Paragraph();
    /**/
    private Checkbox sendNow = new Checkbox("Despachar ahora");
    /**/
    private DateTimePicker dateTimePicker = new DateTimePicker();
    private ComboBox<Agenda> agendaCombo = new ComboBox<>();
    private ComboBox<SystemId> systemIdCombo = new ComboBox<>();
    private Span warningSpan = new Span("");
    private Paragraph charCountSpan = new Paragraph("");

    public BulkSmsSchedulerForm(List<Agenda> agendaList, Collection<SystemId> systemIdCollection, CurrentUser currentUser) {
        setResponsiveSteps(
                new ResponsiveStep("25em", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("32em", 2, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("40em", 4, ResponsiveStep.LabelsPosition.TOP));
        /**/
        orderName.setClearButtonVisible(true);
        orderDescription.setPlaceholder("(Opcional)...");
        orderDescription.setClearButtonVisible(true);
        /**/
        charCountSpan.setText("(1)  0/160 caracteres");
        charCountSpan.getStyle().set("color", "--lumo-tertiary-text-color");
        charCountSpan.getStyle().set("font-size", "var(--lumo-font-size-s)");
        /**/
        agendaCombo.setItems(agendaList);
        systemIdCombo.setItems(systemIdCollection);
        /**/
        setColspan(addFormItem(orderName, "Nombre del Recado"), 2);
        setColspan(addFormItem(orderDescription, "Descripcion del Recado"), 2);
        setColspan(addFormItem(agendaCombo, "Agenda"), 2);
        setColspan(warningSpan, 3);
        setColspan(addFormItem(systemIdCombo, "Pasaporte"), 2);
        FormItem des = addFormItem(messageBox, "Mensaje a enviar");
        des.add(charCountSpan);
        des.add(warningSpan);
        des.add(messageBuilded);
        setColspan(des, 2);
        FormItem despacho = addFormItem(dateTimePicker, "Fecha de Despacho");
        despacho.add(sendNow);
        setColspan(despacho, 2);
    }

    private void doBinder(){

    }

    public Binder<FIlesToSend> getBinder() {
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
