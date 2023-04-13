package com.stt.dash.ui.views.bulksms;

import com.google.gson.Gson;
import com.googlecode.gentyref.TypeToken;
import com.stt.dash.app.OProperties;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.ui.events.CancelEvent;
import com.stt.dash.ui.utils.I18nUtils;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.views.HasNotifications;
import com.stt.dash.ui.views.bulksms.events.BulkSmsReviewEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag("file-to-send-editor")
@JsModule("./src/views/bulksms/file-to-send-editor.js")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileToSendEditorView extends LitTemplate implements HasNotifications {

    @Id("title")
    private H2 title;

    @Id("metaContainer")
    private Div metaContainer;

    @Id("orderNumber")
    private Span orderNumber;

    @Id("status")
    private ComboBox<Agenda> agendaComboBox;

    @Id("dueDate")
    private DateTimePicker dueDate;
    @Id("dueDate2")
    private DatePicker dueDate2;
    @Id("dueTime")
    private TimePicker dueTime;

    @Id("sendNow")
    private Checkbox sendNow;

    @Id("systemId")
    private ComboBox<String> systemIdMulti;

    @Id("orderName")
    private TextField orderName;

    @Id("orderDescription")
    private TextField orderDescription;

    @Id("message")
    private TextArea message;

    @Id("charCounter")
    private Paragraph paragraphCharCounter;

    @Id("acceptCheckbox")
    private Checkbox acceptCheckbox;

//    @Id("warningSpan")
//    private Span warningSpan;


    @Id("messageBuilded")
    private TextArea messageBuilded;

    @Id("cancel")
    private Button cancel;

    @Id("review")
    private Button programAgendaButton;

    /**/
    private FileToSendEditorViewPresenter presenter;

    private final String SMS_MESSAGE_WITHOUT_PARAMETER = "Escriba directamente su mensaje";
    private final String SMS_MESSAGE_WITH_PARAMETER = "Mensaje contiene %s  parámetros; Tienes usados %s.";
//    private FileToSendEditorView fileToSendEditor;

    private User user;

    private Binder<FIlesToSend> binder = new BeanValidationBinder<>(FIlesToSend.class);
    /**/
    private int numOfParameters;
    String[] firstLineValues;
    private Map<Integer, Integer> lines;
    /**/
    private boolean hasMessageAllParameter = false;
    /**/
    private boolean hasEnougharmeters = false;
    private final Gson gson = new Gson();
    private boolean hasMoreSms = false;
    Type gsonType = new TypeToken<HashMap<Integer, Integer>>() {
    }.getType();

    private static Locale esLocale = new Locale("es", "ES");

    private int totsms = 0;

    public FileToSendEditorView(@Qualifier("getMyChildrenAndItsChildrenAndMe") ListGenericBean<User> userChildrenList,
                                AgendaService agendaService,
                                @Qualifier("getUserSystemIdString") ListGenericBean<String> systemIdList,
                                WebClient webClient,
                                OProperties properties,
                                CurrentUser currentuser) {
        /**/
        if (currentuser.getUser().getUserTypeOrd() == User.OUSER_TYPE_ORDINAL.COMERCIAL) {
            presenter = new FileToSendEditorViewPresenter(this, userChildrenList, agendaService, systemIdList, webClient, properties);
        } else {
            /* Usuario solo puede ver sus SYstemIds */
            List<String> stringList = currentuser.getUser().getSystemids().stream().map(SystemId::getSystemId).collect(Collectors.toList());
            ListGenericBean<String> stringListGenericBean = () -> stringList;
            presenter = new FileToSendEditorViewPresenter(this, userChildrenList, agendaService, stringListGenericBean, webClient, properties);
        }
        /**/
        acceptCheckbox.setVisible(false);
        dueDate.setVisible(false);
        /**/
        cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
        programAgendaButton.addClickListener(e ->
        {
            Integer block = -1;
            try {
                block = presenter.callBalance(systemIdMulti.getValue(), dueDate.getValue());
            } catch (Exception ex) {
                showNotification("Ha ocurrido un Error al obtener Saldo. Favor intente nuevamente.", true);
                return;
            }
            if (block <= 0) {
                showNotification("No se puede Programar. Verifique saldo o fecha de Vencimiento de: " + systemIdMulti.getValue(), true);
                return;
            }
            String m = message.getValue().replaceAll("$[0-9]", "");
            int smsBoxCharCounter = StringUtils.isNotEmpty(m) ? m.length() : 0;
            int totSmsLineAgenda = 0;

            if (ObjectUtils.isNotEmpty(agendaComboBox.getValue())) {
                totSmsLineAgenda = agendaComboBox.getValue().getItemCount();
            }

            /* calcular el total asumiendo que la agenda no es variable */
            String s = smsBoxCharCounter + " caracteres";
            paragraphCharCounter.setText(s + "\n");
            Map<Integer, Integer> smsToSendList = calculateNumberOfSms(smsBoxCharCounter);

            /* Calcular total de Agenda no Variable */
            if (ObjectUtils.isEmpty(smsToSendList)) {
                totsms = ((smsBoxCharCounter - 1) / 160 + 1) * totSmsLineAgenda;
            }
            StringBuilder sb = new StringBuilder("");
            for (Map.Entry<Integer, Integer> entry : smsToSendList.entrySet()) {
                sb.append("(" + entry.getValue() + ") registros de (" + entry.getKey() + ") sms. ");
                /* Calcula total de Agenda Variable */
                totsms += entry.getValue() * entry.getKey();
            }

            if (totsms > block) {
                showNotification("Saldo insuficiente", true);
                return;
            }
            /* Agregar al la hora qe va al bind, lo seleccionado en los campos.*/
            LocalDateTime l = LocalDateTime.of(dueDate2.getValue().getYear(),
                    dueDate2.getValue().getMonthValue(),
                    dueDate2.getValue().getDayOfMonth(),
                    dueTime.getValue().getHour(), dueTime.getValue().getMinute());
            dueDate.setValue(l);
            fireEvent(new BulkSmsReviewEvent(this));
        });
        /* El pickup Locations es el systemid*/
        presenter.setSystemIdItems();
        presenter.setAgendaItems();
        /**/
        message.setValueChangeMode(ValueChangeMode.EAGER);
        /*No necesito el binder a status */
        /**/
        agendaComboBox.setItemLabelGenerator(agenda -> agenda.getName() + (
                StringUtils.isNotBlank(agenda.getDescription()) ? " - " + agenda.getDescription() : ""));
        sendNow.addValueChangeListener(listener -> {
            dueTime.setValue(LocalTime.now());
        });
        /* contador de caracteres  */
        paragraphCharCounter.setText("(1) 0/160 caracteres");
        /**/
        dueDate.addValueChangeListener(change -> {
            System.out.println("Cambie de lciente " + change.isFromClient());
        });
        binder.forField(dueDate)
                .asRequired("Seleccione fecha y hora")
                .withConverter(new Converter<LocalDateTime, Date>() {
                    @Override
                    public Result<Date> convertToModel(LocalDateTime localDateTime, ValueContext valueContext) {
                        return Result.ok(ODateUitls.localDateTimeToDate(localDateTime));
                    }

                    @Override
                    public LocalDateTime convertToPresentation(Date date, ValueContext valueContext) {
                        if (date == null) {
                            return LocalDateTime.now();
                        }
                        return ODateUitls.valueOf(date);
                    }
                })
                .bind(FIlesToSend::getDateToSend, FIlesToSend::setDateToSend);

        binder.bind(message, "messageWithParam");
        binder.bind(agendaComboBox, "agenda");
        binder.bind(systemIdMulti, "systemId");
        binder.bind(orderName, "orderName");
        binder.bind(orderDescription, "orderDescription");
        binder.bind(acceptCheckbox, "smsAccepted");
        addListeners();
        /* date-to-send */
        LocalDate ld = LocalDate.now();
        dueDate2.setLocale(esLocale);
        dueDate2.setI18n(I18nUtils.getDatepickerI18n());
        dueDate2.setValue(ld);
        dueDate2.setMin(ld);
        dueDate.setLocale(esLocale);
        dueDate.setDatePickerI18n(I18nUtils.getDatepickerI18n());
        dueDate.setMin(LocalDateTime.now());
        dueDate.setValue(LocalDateTime.now().plusMinutes(10));
        /* time */
        dueTime.setValue(LocalTime.of(LocalDateTime.now().plusHours(1).getHour(), 0));
        dueTime.setStep(Duration.ofMinutes(30));
        /**/
        binder.addValueChangeListener(e -> {
            System.out.println("OLDVALUE ->" + e.getOldValue() + " VALUE ->" + e.getValue());
            System.out.println("ADDVALUE BINDER->" + binder.hasChanges());

            if (e.getOldValue() != null) {
                programAgendaButton.setEnabled(hasChanges());
            }
        });
    }

    private void addListeners() {
        dueDate2.addValueChangeListener(lister -> {
            if (lister.getValue().isEqual(LocalDate.now())) {
                dueTime.setMinTime(LocalTime.of(LocalDateTime.now().plusHours(1).getHour(), 0));
            } else {
                dueTime.setMinTime(null);
            }
        });
        acceptCheckbox.addValueChangeListener(event -> {
            if (!event.isFromClient()) {
                return;
            }
            validateReview();
        });
        message.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                return;
            }

            String m = message.getValue().replaceAll("$[0-9]", "");
            int smsMsgboxCharCounter = StringUtils.isNotEmpty(m) ? m.length() : 0;
            int totSmsLineAgenda = 0;

            if (ObjectUtils.isNotEmpty(agendaComboBox.getValue())) {
                totSmsLineAgenda = agendaComboBox.getValue().getItemCount();
            }

            /* calcular el total asumiendo que la agenda no es variable */
            String s = smsMsgboxCharCounter + " caracteres";
            paragraphCharCounter.setText(s + "\n");
            int totsms = 0;
            Map<Integer, Integer> smsToSendList;

            if (numOfParameters == 1) {
                smsToSendList = calculateNumberOfSms(0);
            } else {
                smsToSendList = calculateNumberOfSms(smsMsgboxCharCounter);
            }

            /* Calcular total de Agenda no Variable */
            if (ObjectUtils.isEmpty(smsToSendList)) {
                totsms = ((smsMsgboxCharCounter - 1) / 160 + 1) * totSmsLineAgenda;
            }

            StringBuilder sb = new StringBuilder("");
            for (Map.Entry<Integer, Integer> entry : smsToSendList.entrySet()) {
                sb.append("(" + entry.getValue() + ") registros de (" + entry.getKey() + ") sms. ");

                /* Calcula total de Agenda Variable */
                totsms += entry.getValue() * entry.getKey();
            }
            paragraphCharCounter.setText(smsMsgboxCharCounter + "/160. " + sb.toString() + "Total Sms a enviar: " + totsms);

            /* Tiene mas sms que lineas en la agenda */
            if (totsms != totSmsLineAgenda) {
                hasMoreSms = true;
                acceptCheckbox.setVisible(true);
                acceptCheckbox.setLabel("Acepto enviar: " + totsms + " sms.");
            } else {
                hasMoreSms = false;
                acceptCheckbox.setVisible(false);
            }
            /* Validacion de que este toda la informacion de variables. */
            int vars = 1;
            while (message.getValue().contains("$" + vars)) {
                vars++;
            }
            /* al comenzar desde $1 se debe restar uno para que tenga la cantidad correcta de variables*/
            vars--;
            message.setHelperText(String.format(SMS_MESSAGE_WITH_PARAMETER, numOfParameters, vars));
            if (numOfParameters != vars) {
//                warningSpan.setText("Mensajes en esta Agenda necesitan "
//                        + varCount
//                        + " parámetros; Tienes "
//                        + vars
//                        + ".");
                hasMessageAllParameter = false;
            } else {
//                warningSpan.setText("");
                hasMessageAllParameter = true;
            }
            String newMsg = message.getValue() == null ? "" : message.getValue();
            if (vars > 0 && newMsg.contains("$")) {
                for (int i = 1; i <= numOfParameters; i++) {
                    System.out.println("FIRST VALUE: '" + firstLineValues[i] + "'");
                    newMsg = newMsg.replace("$" + i, firstLineValues[i]);
                }
                messageBuilded.setValue(newMsg);
                messageBuilded.getStyle().set("color", "var(--lumo-success-text-color)");
                messageBuilded.getStyle().set("font-size", "var(--lumo-font-size-s)");
            }
            validateReview();
        });
        message.addInputListener(event -> {
//            if (!event.isFromClient()) {
//                return;
//            }
            String m = message.getValue().replaceAll("$[0-9]", "");
            int smsMsgboxCharCounter = StringUtils.isNotEmpty(m) ? m.length() : 0;
            int totSmsLineAgenda = 0;

            if (ObjectUtils.isNotEmpty(agendaComboBox.getValue())) {
                totSmsLineAgenda = agendaComboBox.getValue().getItemCount();
            }

            /* calcular el total asumiendo que la agenda no es variable */
            String s = smsMsgboxCharCounter + " caracteres";
            paragraphCharCounter.setText(s + "\n");
            int totsms = 0;
            Map<Integer, Integer> smsToSendList = calculateNumberOfSms(smsMsgboxCharCounter);

            /* Calcular total de Agenda no Variable */
            if (ObjectUtils.isEmpty(smsToSendList)) {
                totsms = ((smsMsgboxCharCounter - 1) / 160 + 1) * totSmsLineAgenda;
            }
            StringBuilder sb = new StringBuilder("");
            for (Map.Entry<Integer, Integer> entry : smsToSendList.entrySet()) {
                sb.append("(" + entry.getValue() + ") registros de (" + entry.getKey() + ") sms. ");

                /* Calcula total de Agenda Variable */
                totsms += entry.getValue() * entry.getKey();
            }
            paragraphCharCounter.setText(smsMsgboxCharCounter + "/160. " + sb.toString() + "Total Sms a enviar: " + totsms);

            /* Tiene mas sms que lineas en la agenda */
            if (totsms != totSmsLineAgenda) {
                hasMoreSms = true;
                acceptCheckbox.setVisible(true);
                acceptCheckbox.setLabel("Acepto enviar: " + totsms + " sms.");
            } else {
                hasMoreSms = false;
                acceptCheckbox.setVisible(false);
            }
            /* Validacion de que este toda la informacion de variables. */
            int vars = 1;
            while (message.getValue().contains("$" + vars)) {
                vars++;
            }
            /* al comenzar desde $1 se debe restar uno para que tenga la cantidad correcta de variables*/
            vars--;
            message.setHelperText(String.format(SMS_MESSAGE_WITH_PARAMETER, numOfParameters, vars));
            if (numOfParameters != vars) {
//                warningSpan.setText("Mensajes en esta Agenda necesitan "
//                        + varCount
//                        + " parámetros; Tienes "
//                        + vars
//                        + ".");
                hasMessageAllParameter = false;
            } else {
//                warningSpan.setText("");
                hasMessageAllParameter = true;
            }
            String newMsg = message.getValue() == null ? "" : message.getValue();
            if (vars > 0 && newMsg.contains("$")) {
                for (int i = 1; i <= numOfParameters; i++) {
                    System.out.println("FIRST VALUE: '" + firstLineValues[i] + "'");
                    newMsg = newMsg.replace("$" + i, firstLineValues[i]);
                }
                messageBuilded.setValue(newMsg);
                messageBuilded.getStyle().set("color", "var(--lumo-success-text-color)");
                messageBuilded.getStyle().set("font-size", "var(--lumo-font-size-s)");
            }
            validateReview();
        });
        agendaComboBox.addValueChangeListener(event -> {
            if (!event.isFromClient()) {
                return;
            }
            hasEnougharmeters = false;
            /* Cada vez que se cambia de agenda. */
            message.setValue("");
            acceptCheckbox.setValue(false);
            acceptCheckbox.setVisible(false);
            messageBuilded.setValue("");
            if (agendaComboBox.getValue() != null) {
                lines = gson.fromJson(agendaComboBox.getValue().getSizeOfLines(), gsonType);
                firstLineValues = getVariable(agendaComboBox.getValue().getFirstLine());
                /* El total de parametros sin la columna numero del celular.  */
                numOfParameters = firstLineValues.length - 1;
                /* Si tiene solo un parametro ese valor se coloca en el mensaje */
                if (numOfParameters == 0) {
                    messageBuilded.setVisible(false);
                    message.setEnabled(true);
                    message.setHelperText(SMS_MESSAGE_WITHOUT_PARAMETER);
                } else if (numOfParameters == 1) {
                    messageBuilded.setVisible(false);
                    message.setEnabled(false);
                    message.setValue(firstLineValues[1]);
                    message.setHelperText(SMS_MESSAGE_WITHOUT_PARAMETER);
                } else {
                    messageBuilded.setVisible(true);
                    message.setEnabled(true);
                    message.setHelperText(String.format(SMS_MESSAGE_WITH_PARAMETER, numOfParameters, "0"));
                }
            } else {
                firstLineValues = new String[1];
                numOfParameters = 0;
            }
            hasMessageAllParameter = !hasEnougharmeters;
            /**/
            binder.validate();
        });
        systemIdMulti.addValueChangeListener(changeListener -> binder.validate());
        sendNow.addValueChangeListener(changeEvent -> {
            if (changeEvent.isFromClient()) {
                return;
            }
            dueDate.setValue(LocalDateTime.now());
        });
        binder.addValueChangeListener(e -> {
            validateReview();
        });
    }

    private void validateReview() {
        if (hasMoreSms) {
            programAgendaButton.setEnabled(binder.isValid() && acceptCheckbox.getValue());
        } else {
            programAgendaButton.setEnabled(binder.isValid());
        }
    }

    /**
     * Map que contiene:
     * k-> Cantidad de mensajes a enviar
     * V-> Cantidad de lineas del archivo que van a enviar esa cantidad de mensajes.
     *
     * @param actualSmsSize
     * @return Cantidad de mensajes que van a ser enviados por n cantidad de lineas
     */
    private Map<Integer, Integer> calculateNumberOfSms(int actualSmsSize) {

        if (ObjectUtils.isEmpty(agendaComboBox.getValue()) ||
                ObjectUtils.isEmpty(agendaComboBox.getValue().getSizeOfLines())) {
            return new HashMap<>();
        }

        Map<Integer, Integer> numOfSmsToSendMap = new HashMap<>();
        lines.forEach((messageSize, nLines) -> {
            Integer totLines = 0;
            int numOfMessageToSend = (actualSmsSize + messageSize - 1) / 160 + 1;
            Integer nLinesWithThatNumOfMsgToSend = numOfSmsToSendMap.get(numOfMessageToSend);

            if (nLinesWithThatNumOfMsgToSend == null) {
                totLines = nLines;
            } else {
                totLines = nLines + nLinesWithThatNumOfMsgToSend;
            }
            numOfSmsToSendMap.put(numOfMessageToSend, totLines);
        });
        return numOfSmsToSendMap;
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
                    lineByValues[pos++] = string;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BulkSmsSchedulerForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lineByValues;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean hasChanges() {
        return binder.hasChanges() /*|| itemsEditor.hasChanges()*/;
    }

    public void clear() {
        System.out.println("llegue a CLEAR");
        binder.readBean(null);
//        itemsEditor.setValue(null);
    }

    public void write(FIlesToSend filesToSend) throws ValidationException {
        filesToSend.setSmsCount(totsms);
        binder.writeBean(filesToSend);
    }

    public void read(FIlesToSend filesToSend, boolean isNew) {
        binder.readBean(filesToSend);

        this.orderNumber.setText(isNew ? "" : filesToSend.getId().toString());
        title.setVisible(isNew);
        metaContainer.setVisible(!isNew);

        if (filesToSend.getStatus() != null) {
//            getModel().setStatus(order.getState().name());
        }
        programAgendaButton.setEnabled(false);
    }

    public Stream<HasValue<?, ?>> validate() {
        Stream<HasValue<?, ?>> errorFields = binder.validate().getFieldValidationErrors().stream()
                .map(BindingValidationStatus::getField);
        return errorFields;
    }

    public Registration addReviewListener(ComponentEventListener<BulkSmsReviewEvent> listener) {
        return addListener(BulkSmsReviewEvent.class, listener);
    }

    public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }

    public void close() {
//        setTotalPrice(0);
    }

    public void setComboAgendaItems(List<Agenda> agendaList) {
        agendaComboBox.setItems(agendaList);
    }

    public void setComboSystemidItems(List<String> systemIdList) {
        systemIdMulti.setItems(systemIdList);
    }
}
