package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.entity.Agenda;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.ui.crud.CrudEntityDataProvider;
import com.stt.dash.ui.events.CancelEvent;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.views.bulksms.events.BulkSmsReviewEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Tag("file-to-send-editor")
@JsModule("./src/views/bulksms/file-to-send-editor.ts")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileToSendEditor extends LitTemplate {


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
    private Paragraph charCounter;

//    @Id("warningSpan")
//    private Span warningSpan;


    @Id("messageBuilded")
    private TextArea messageBuilded;

    @Id("cancel")
    private Button cancel;

    @Id("review")
    private Button review;
    private final String SMS_MESSAGE_WITHOUT_PARAMETER = "Escriba directamente su mensaje";
    private final String SMS_MESSAGE_WITH_PARAMETER = "Mensaje contiene %s  parámetros; Tienes usados %s.";
//    private FileToSendEditor fileToSendEditor;

    private User currentUser;

    private Binder<FIlesToSend> binder = new BeanValidationBinder<>(FIlesToSend.class);
    /**/
    private int varCount;
    String[] firstLineValue;
    /**/
    private boolean hasMessageAllParameter = false;
    /**/
    private boolean hasEnougharmeters = false;

    public FileToSendEditor(@Qualifier("getUserMeAndChildren") ListGenericBean<User> userChildrenList,
                            AgendaService agendaService, @Qualifier("getUserSystemIdString") ListGenericBean<String> systemIdList) {
        /*No existen Items*/
        /**/
        cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
        review.addClickListener(e -> fireEvent(new BulkSmsReviewEvent(this)));
        /* El pickup Locations es el systemid*/
        systemIdMulti.setItems(systemIdList.getList());
        /**/
        message.setValueChangeMode(ValueChangeMode.EAGER);
        /*No necesito el binder a status */
        /**/
        DataProvider<Agenda, String> agendaDataProvider = new CrudEntityDataProvider<>(agendaService);
        agendaComboBox.setDataProvider(agendaDataProvider);
        agendaComboBox.setItemLabelGenerator(Agenda::getName);
        /* contador de caracteres  */
        charCounter.setText("(1) 0/160 caracteres");
        /**/
        dueDate.addValueChangeListener(change -> {
            System.out.println("Cambie de lciente " + change.isFromClient());
        });
        binder.forField(dueDate)
                .asRequired("Seleccione fechay hora")
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
        addListeners();
        /* date-to-send */
        dueDate.setMin(LocalDateTime.now());
        dueDate.setValue(LocalDateTime.now().plusMinutes(10));
        /**/
        binder.addValueChangeListener(c->{
            System.out.println("OLDVALUE ->" + c.getOldValue()+" VALUE ->" + c.getValue());
            System.out.println("ADDVALUE BINDER->" + binder.hasChanges());
        });
    }

    private void addListeners() {
        message.addValueChangeListener(listener -> {
            int count = message.getValue().length();
            int numMessages = (count - 1) / 160 + 1;
            charCounter.setText("(" + numMessages + ")  " + count + "/" + ((numMessages) * 160) + " caracteres");
        });

        message.addInputListener(event -> {
            int count = message.getValue().length();
            int numMessages = (count - 1) / 160 + 1;
            charCounter.setText("(" + numMessages + ")  " + count + "/" + ((numMessages) * 160) + " caracteres");
            /* Validacion de que este toda la informacion de variables. */
            int vars = 1;
            while (message.getValue().contains("$" + vars)) {
                vars++;
            }
            /* al comenzar desde $1 se debe restar uno para que tenga la cantidad correcta de variables*/
            vars--;
            message.setHelperText(String.format(SMS_MESSAGE_WITH_PARAMETER, varCount, vars));
            if (varCount != vars) {
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
                for (int i = 1; i <= varCount; i++) {
                    System.out.println("FIRST VALUE: '" + firstLineValue[i] + "'");
                    newMsg = newMsg.replace("$" + i, firstLineValue[i]);
                }
                messageBuilded.setValue(newMsg);
                messageBuilded.getStyle().set("color", "var(--lumo-success-text-color)");
                messageBuilded.getStyle().set("font-size", "var(--lumo-font-size-s)");
            }
        });
        agendaComboBox.addValueChangeListener(event -> {
            hasEnougharmeters = false;
            /* clear cada vez que se cambia de agenda. */
            message.setValue("");
            if (agendaComboBox.getValue() != null) {
                firstLineValue = getVariable(agendaComboBox.getValue().getFirstLine());
                /* El total de parametros sin la columna numero del celular.  */
                varCount = firstLineValue.length - 1;
                /* Si tiene solo un parametro ese valor se coloca en el mensaje */
                if (varCount==0){
                    messageBuilded.setVisible(false);
                    message.setEnabled(true);
                    message.setHelperText(SMS_MESSAGE_WITHOUT_PARAMETER);
//                    warningSpan.setText(SMS_MESSAGE_WITHOUT_PARAMETER);
                    /* Invisible messageBuilder*/
                    /* Habilitado message */
                }
                if (varCount==1){
                    messageBuilded.setVisible(false);
                    message.setEnabled(false);
                    message.setValue(firstLineValue[1]);
                    message.setHelperText(SMS_MESSAGE_WITHOUT_PARAMETER);
                    /* Invisible messageBuilder*/
                    /* Deshabilitado message */
                    /* Escribir el unico parametro en message */
                }
                if (varCount>1){
                    messageBuilded.setVisible(true);
                    message.setEnabled(true);
                    message.setHelperText(String.format(SMS_MESSAGE_WITH_PARAMETER, varCount, "0"));
//                    warningSpan.setText(String.format(SMS_MESSAGE_WITH_PARAMETER, varCount));
                    /* Visible messageBuilder */
                    /* Habilitado message */
                }
//                if (varCount == 1) {
//                    message.setValue(firstLineValue[1]);
//                    warningSpan.setText("");
//                } else if (varCount == 0) {
//                    message.setValue(SMS_MESSAGE_WITHOUT_PARAMETER);
//                } else if (varCount !=1 ){
//                    hasEnougharmeters = true;
//                    warningSpan.setText(String.format(SMS_MESSAGE_WITH_PARAMETER, varCount));
//                }
//                if (varCount < 2) {
//                    messageBuilded.setVisible(false);
//                    messageBuilded.setValue("");
//                }
            } else {
                firstLineValue = new String[1];
                varCount = 0;
            }
            hasMessageAllParameter = !hasEnougharmeters;
//            message.setEnabled(hasEnougharmeters);
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
//        ComponentUtil.addListener(itemsEditor, ValueChangeEvent.class, e -> review.setEnabled(hasChanges()));
        binder.addValueChangeListener(e -> {
//            if (e.getOldValue() != null) {
            review.setEnabled(binder.isValid());
//            }
        });
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

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

//    public Stream<HasValue<?, ?>> validate() {
//        Stream<HasValue<?, ?>> errorFields = binder.validate().getFieldValidationErrors().stream()
//                .map(BindingValidationStatus::getField);
//
//        return Stream.concat(errorFields, itemsEditor.validate());
//    }

    public void read(FIlesToSend order, boolean isNew) {
        binder.readBean(order);

        this.orderNumber.setText(isNew ? "" : order.getId().toString());
        title.setVisible(isNew);
        metaContainer.setVisible(!isNew);

        if (order.getStatus() != null) {
//            getModel().setStatus(order.getState().name());
        }
        review.setEnabled(false);
    }

    public boolean hasChanges() {
        return binder.hasChanges() /*|| itemsEditor.hasChanges()*/;
    }

    public void clear() {
        System.out.println("llegue a CLEAR");
        binder.readBean(null);
//        itemsEditor.setValue(null);
    }

    public void write(FIlesToSend fIlesToSend) throws ValidationException {
        binder.writeBean(fIlesToSend);
    }

    public Registration addReviewListener(ComponentEventListener<BulkSmsReviewEvent> listener) {
        System.out.println("ADDREVIEWLISTENER");
        return addListener(BulkSmsReviewEvent.class, listener);
    }

    public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }

    public void close() {
//        setTotalPrice(0);
    }
}
