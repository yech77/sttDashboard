package com.stt.dash.ui.views.bulksms;

import com.stt.dash.app.session.ListGenericBean;
import com.stt.dash.app.session.SetGenericBean;
import com.stt.dash.backend.data.OrderState;
import com.stt.dash.backend.data.Role;
import com.stt.dash.backend.data.Status;
import com.stt.dash.backend.data.entity.*;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.events.CancelEvent;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.converters.LocalTimeConverter;
import com.stt.dash.ui.views.orderedit.OrderItemsEditor;
import com.stt.dash.ui.views.storefront.events.ReviewEvent;
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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.time.LocalTime;

@Tag("file-to-send-editor")
@JsModule("./src/views/bulksms/file-to-send-editor.ts")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Route(value = BakeryConst.PAGE_BULKSMS_SCHEDULER+"nuevo", layout = MainView.class)
@PageTitle(BakeryConst.TITLE_BULKSMS_SCHEDULER)
@Secured({Role.ADMIN, "UI_USER"})
public class FileToSendEditor extends LitTemplate {


    @Id("title")
    private H2 title;

    @Id("metaContainer")
    private Div metaContainer;

    @Id("orderNumber")
    private Span orderNumber;

    @Id("status")
    private ComboBox<Status> status;

    @Id("dueDate")
    private DateTimePicker dueDate;

    @Id("sendNow")
    private Checkbox sendNow;

    @Id("systemId")
    private MultiselectComboBox<SystemId> systemIdMulti;

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

    public FileToSendEditor(@Qualifier("getComercialUserSystemId") SetGenericBean<SystemId> systemIdList){
        /* El pickup Locations es el systemid*/
        systemIdMulti.setItems(systemIdList.getSet());
        systemIdMulti.setItemLabelGenerator(SystemId::getSystemId);
        /**/
        cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
        review.addClickListener(e -> fireEvent(new BulkSmsReviewEvent(this)));
    }
}
