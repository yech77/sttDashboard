package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.backend.data.entity.*;
import com.stt.dash.backend.service.AgendaService;
import com.stt.dash.ui.crud.CrudEntityDataProvider;
import com.stt.dash.ui.events.CancelEvent;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.views.bulksms.events.BulkSmsReviewEvent;
import com.stt.dash.ui.views.storefront.events.ReviewEvent;
import com.stt.dash.ui.views.storefront.events.ValueChangeEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
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
    @Id("warningSpan")
    private Span warningSpan;
    @Id("messageBuilded")
    private Paragraph messageBuilded;

    @Id("cancel")
    private Button cancel;

    @Id("review")
    private Button review;

    private FileToSendEditor fileToSendEditor;

    private User currentUser;

    private BeanValidationBinder<FIlesToSend> binder = new BeanValidationBinder<>(FIlesToSend.class);
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
        binder.forField(dueDate)
                .asRequired("Seleccione fechay hora")
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
        binder.bind(systemIdMulti, "systemId");
        binder.bind(orderName, "orderName");
        binder.bind(orderDescription, "orderDescription");
        addListeners();
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
            String newMsg = message.getValue() == null ? "" : message.getValue();
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
        agendaComboBox.addValueChangeListener(event -> {
            hasEnougharmeters = false;
            if (agendaComboBox.getValue() != null) {
                firstLineValue = getVariable(agendaComboBox.getValue().getFirstLine());
                /* El total sin la columna numero del celular.  */
                varCount = firstLineValue.length - 1;
                /* Si tiene solo un parametro ese valor se coloca en el mensaje */
                if (varCount == 1) {
                    message.setValue(firstLineValue[1]);
                } else {
                    hasEnougharmeters = true;
                }
            } else {
                firstLineValue = new String[1];
                varCount = 0;
            }
            hasMessageAllParameter = !hasEnougharmeters;
            message.setEnabled(hasEnougharmeters);
        });
        sendNow.addValueChangeListener(changeEvent->{
            dueDate.setValue(LocalDateTime.now());
        });
//        ComponentUtil.addListener(itemsEditor, ValueChangeEvent.class, e -> review.setEnabled(hasChanges()));
        binder.addValueChangeListener(e -> {
            if (e.getOldValue() != null) {
                review.setEnabled(hasChanges() & binder.isValid());
            }
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
        binder.readBean(null);
//        itemsEditor.setValue(null);
    }
    public void write(FIlesToSend fIlesToSend) throws ValidationException {
        binder.writeBean(fIlesToSend);
    }

    public Registration addReviewListener(ComponentEventListener<ReviewEvent> listener) {
        return addListener(ReviewEvent.class, listener);
    }

    public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }
}
