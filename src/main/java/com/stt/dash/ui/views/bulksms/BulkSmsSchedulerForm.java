package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.OSystemIdSession;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.ui.utils.ODateUitls;
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
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BulkSmsSchedulerForm extends FormLayout {
    Binder<FIlesToSend> binder = new BeanValidationBinder<>(FIlesToSend.class);
    /**/
    public TextField orderName = new TextField();
    public TextField orderDescription = new TextField();
    public TextArea messageBox = new TextArea();
    private Paragraph messageBuilded = new Paragraph();
    /**/
    private Checkbox sendNow = new Checkbox("Despachar ahora");
    /**/
    private DateTimePicker dateTimePicker = new DateTimePicker();
    public ComboBox<Agenda> agendaCombo = new ComboBox<>();
    public ComboBox<String> systemIdCombo = new ComboBox<>();
    private Span warningSpan = new Span("");
    private Paragraph charCountSpan = new Paragraph("");
    /**/
    private int varCount;
    String[] firstLineValue;
    /**/
    private boolean hasMessageAllParameter = false;
    /**/
    private boolean hasEnougharmeters = false;
    /**/
    private int sendTime = 1;
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
        agendaCombo.setItemLabelGenerator(Agenda::getName);
        systemIdCombo.setItems(systemIdCollection.stream().map(SystemId::getSystemId).collect(Collectors.toList()));
//        systemIdCombo.setItemLabelGenerator();
        /**/
        orderName.setWidthFull();
        orderDescription.setWidthFull();
        agendaCombo.setWidthFull();
        warningSpan.setWidthFull();
        systemIdCombo.setWidthFull();
        messageBox.setWidthFull();
        messageBox.setValueChangeMode(ValueChangeMode.EAGER);
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
        addListeners();
        /**/
        dateTimePicker.setMin(LocalDateTime.now());
        dateTimePicker.setValue(LocalDateTime.now().plusMinutes(10));
        doBinder();
    }

    public Binder<FIlesToSend> getBinder() {
        return binder;
    }

    private void addListeners() {
        messageBox.addValueChangeListener(listener -> {
            int count = messageBox.getValue().length();
            int numMessages = (count - 1) / 160 + 1;
            charCountSpan.setText("(" + numMessages + ")  " + count + "/" + ((numMessages) * 160) + " caracteres");
        });

        messageBox.addInputListener(event -> {
            int count = messageBox.getValue().length();
            int numMessages = (count - 1) / 160 + 1;
            charCountSpan.setText("(" + numMessages + ")  " + count + "/" + ((numMessages) * 160) + " caracteres");
            /* Validacion de que este toda la informacion de variables. */
            int vars = 1;
            while (messageBox.getValue().contains("$" + vars)) {
                vars++;
            }
            /* al comenzar desde $1 se debe restar uno para que tenga la cantidad correcta de variables*/
            vars--;
            if (varCount != vars) {
                warningSpan.setText("Mensajes en esta Agenda necesitan "
                        + varCount
                        + " parámetros; Tienes "
                        + vars
                        + ".");
                hasMessageAllParameter = false;
            } else {
                warningSpan.setText("");
                hasMessageAllParameter = true;
            }
            String newMsg = messageBox.getValue() == null ? "" : messageBox.getValue();
            if (vars > 0 && newMsg.contains("$")) {
                for (int i = 1; i <= varCount; i++) {
                    System.out.println("FIRST VALUE: '" + firstLineValue[i] + "'");
                    newMsg = newMsg.replace("$" + i, firstLineValue[i]);
                }
                messageBuilded.setText("Mensaje: " + newMsg);
                messageBuilded.getStyle().set("color", "var(--lumo-success-text-color)");
                messageBuilded.getStyle().set("font-size", "var(--lumo-font-size-s)");
            }
        });
        agendaCombo.addValueChangeListener(event -> {
            hasEnougharmeters = false;
            if (agendaCombo.getValue() != null) {
                firstLineValue = getVariable(agendaCombo.getValue().getFirstLine());
                /* El total sin la columna numero del celular.  */
                varCount = firstLineValue.length - 1;
                /* Si tiene solo un parametro ese valor se coloca en el mensaje */
                if (varCount == 1) {
                    messageBox.setValue(firstLineValue[1]);
                } else {
                    hasEnougharmeters = true;
                }
            } else {
                firstLineValue = new String[1];
                varCount = 0;
            }
            hasMessageAllParameter = !hasEnougharmeters;
            messageBox.setEnabled(hasEnougharmeters);
        });
        sendNow.addValueChangeListener(changeEvent->{
            dateTimePicker.setValue(LocalDateTime.now());
        });

    }

    private void doBinder() {
        binder.bind(orderName, "orderName");
        binder.bind(orderDescription, "orderDescription");
        binder.bind(systemIdCombo, "systemId");
        binder.forField(dateTimePicker)
                .asRequired("Seleccione una credencial")
                .withConverter(new Converter<LocalDateTime, Date>() {
                    @Override
                    public Result<Date> convertToModel(LocalDateTime localDateTime, ValueContext valueContext) {
                        return Result.ok(ODateUitls.localDateTimeToDate(localDateTime));
                    }

                    @Override
                    public LocalDateTime convertToPresentation(Date date, ValueContext valueContext) {
                       if(date==null){
                           return LocalDateTime.now();
                       }
                        return ODateUitls.valueOf(date);
                    }
                })
                .bind(FIlesToSend::getDateToSend, FIlesToSend::setDateToSend);
    }

    /**
     * Obtiene por separado todos los valores de a primera linea. Num de cel
     * inclusive.
     *
     * @param firstLine
     * @return
     */
    private String[] getVariable(String firstLine) {
        String[] lineByValues = null;
        try {
            InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(firstLine.getBytes(StandardCharsets.UTF_8)));
            /* READER */
            Iterable<CSVRecord> records = CSVFormat.newFormat(',')
                    .withQuote('"')
                    .withIgnoreEmptyLines(true)
                    .parse(new BufferedReader(isr));
            /*Obtener la unica linea*/
            for (CSVRecord record : records) {
                lineByValues = new String[record.size()];
                int pos = 0;
                for (String string : record) {
                    System.out.println("POS y STRING: " + pos + ":" + string);
                    lineByValues[pos++] = string;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BulkSmsSchedulerForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lineByValues;
    }

    public boolean isValidData() {
        return binder.isValid() && hasMessageAllParameter;
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
