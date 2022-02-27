package com.stt.dash.ui.views.audit;

import com.stt.dash.backend.data.entity.ODashAuditEvent;
import com.stt.dash.backend.data.entity.OUser;
import com.stt.dash.backend.data.entity.User;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class ODashAuditEventForm extends FormLayout {

    private ComboBox<User> userCombo = new ComboBox<>();
    private ComboBox<ODashAuditEvent.OEVENT_TYPE> eventCombo = new ComboBox<>();
    private DatePicker firstDate = new DatePicker();
    private DatePicker secondDate = new DatePicker();
    private Button searchButton = new Button("BUSCAR");
    private Binder<OdashAuditEventFormBean> binder = new BeanValidationBinder<>(OdashAuditEventFormBean.class);
    /**/
    private Checkbox allUserCheck = new Checkbox("todos los usuarios");
    private Checkbox allEventCheck = new Checkbox("todos los eventos");
    private Locale esLocale = new Locale("es", "ES");

    public ODashAuditEventForm(List<User> userList) {
        initCombo(userList);
        /**/
        initEventCombo();
        /**/
        initDates();
        /**/
        initBean();
        /**/
        initForm();
        /**/
        initButtonSearch();
        /**/
        initAllUserCheck();
    }

    private void initBean() {
        /* User */
        binder.forField(userCombo)
                .withValidator(
                        user -> {
                            if ((user == null || userCombo.isEmpty()) && allUserCheck.getValue() == true) {
                                return true;
                            } else if (user != null && allUserCheck.getValue() == false) {
                                return true;
                            } else {
                                return false;
                            }
                        },
                        "Seleccione el Usuario")
                .bind(OdashAuditEventFormBean::getUserCombo, OdashAuditEventFormBean::setUserCombo);
        /* Event */
        binder.forField(eventCombo)
                .withValidator(
                        user -> {
                            if ((user == null || eventCombo.isEmpty()) && allEventCheck.getValue() == true) {
                                return true;
                            } else if (user != null && allEventCheck.getValue() == false) {
                                return true;
                            } else {
                                return false;
                            }
                        },
                        "Seleccione el Evento")
                .bind(OdashAuditEventFormBean::getEventCombo, OdashAuditEventFormBean::setEventCombo);

//        binder.forField(eventCombo)
//                .asRequired("Seleecione el evento")
//                .bind(OdashAuditEventFormBean::getEventCombo, OdashAuditEventFormBean::setEventCombo);
        /* firstDate */
        // Store return date binding so we can
// revalidate it later
        Binder.Binding<OdashAuditEventFormBean, LocalDate> returningBinding = binder.forField(firstDate)
                .asRequired()
                .withValidator(
                        returnDate -> !returnDate.isAfter(secondDate.getValue()),
                        "Fecha desde debe ser menor o igual")
                .bind(OdashAuditEventFormBean::getFirstDate, OdashAuditEventFormBean::setFirstDate);
        /* secondDate */
        binder.forField(secondDate)
                .asRequired()
                .bind(OdashAuditEventFormBean::getSecondDate, OdashAuditEventFormBean::setSecondDate);
        // Revalidate return date when departure date changes
        secondDate.addValueChangeListener(
                event -> returningBinding.validate());
        binder.addStatusChangeListener(evt -> searchButton.setEnabled(isValid()));
        binder.setBean(new OdashAuditEventFormBean());
        secondDate.setLocale(esLocale);
        firstDate.setLocale(esLocale);
    }

    public OdashAuditEventFormBean getBinderBean() {
        return binder.getBean();
    }

    private boolean isValid() {
        return binder.isValid();
    }

    private void initForm() {
        setResponsiveSteps(
                new ResponsiveStep("25em", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("32em", 2, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("40em", 4, ResponsiveStep.LabelsPosition.TOP));
        setColspan(addFormItem(firstDate, "Desde"), 1);
        setColspan(addFormItem(secondDate, "Hasta"), 1);
        /**/
        FormItem userItem = addFormItem(userCombo, "Usuarios");
        userItem.add(allUserCheck);
        /**/
        setColspan(userItem, 1);
        FormItem formItem = addFormItem(eventCombo, "Eventos");
        formItem.add(allEventCheck);
        setColspan(formItem, 1);
        HorizontalLayout h = new HorizontalLayout(searchButton);
        setColspan(addFormItem(h, ""), 4);
        searchButton.getElement().getStyle().set("margin-left", "auto");
    }

    private void initCombo(List<User> userList) {
        userCombo.setItems(userList);
        userCombo.setClearButtonVisible(true);
        userCombo.setItemLabelGenerator(User::getEmail);
        userCombo.setWidthFull();
        userCombo.addValueChangeListener(listener -> {
            searchButton.setEnabled(isValid());
        });
    }

    private void initEventCombo() {
        eventCombo.setItems(ODashAuditEvent.OEVENT_TYPE.values());
        eventCombo.setClearButtonVisible(true);
        eventCombo.setItemLabelGenerator(ODashAuditEvent.OEVENT_TYPE::name);
        eventCombo.setWidthFull();
    }

    private void initAllUserCheck() {
        allUserCheck.addValueChangeListener(click -> {
            if (allUserCheck.getValue()) {
                userCombo.setValue(null);
                allEventCheck.setValue(false);
            }
            binder.validate();
            userCombo.setEnabled(!allUserCheck.getValue());
        });
        allUserCheck.addClassName("text-desc-s");
        allEventCheck.addValueChangeListener(click -> {
            if (allUserCheck.getValue()) {
                eventCombo.setValue(null);
                allUserCheck.setValue(false);
            }
            binder.validate();
            eventCombo.setEnabled(!allEventCheck.getValue());
        });
        allEventCheck.addClassName("text-desc-s");
    }

    private void initButtonSearch() {
        Icon icon = VaadinIcon.SEARCH.create();
        icon.setSize("30px");
        searchButton.setIcon(icon);
        searchButton.addClickListener(click -> fireEvent(new SearchEvent(this, binder.getBean())));
//        searchButton.setWidthFull();

    }

    private void initDates() {
        firstDate.setValue(LocalDate.now());
        secondDate.setValue(LocalDate.now());
        firstDate.setWidthFull();
        secondDate.setWidthFull();
    }

    public static class OdashAuditEventFormBean {

        private User userCombo;
        private ODashAuditEvent.OEVENT_TYPE eventCombo;
        private LocalDate firstDate = LocalDate.now();
        private LocalDate secondDate = LocalDate.now();

        public User getUserCombo() {
            return userCombo;
        }

        public void setUserCombo(User userCombo) {
            this.userCombo = userCombo;
        }

        public ODashAuditEvent.OEVENT_TYPE getEventCombo() {
            return eventCombo;
        }

        public void setEventCombo(ODashAuditEvent.OEVENT_TYPE eventCombo) {
            this.eventCombo = eventCombo;
        }

        public LocalDate getFirstDate() {
            return firstDate;
        }

        public void setFirstDate(LocalDate firstDate) {
            this.firstDate = firstDate;
        }

        public LocalDate getSecondDate() {
            return secondDate;
        }

        public void setSecondDate(LocalDate secondDate) {
            this.secondDate = secondDate;
        }

    }

    //////////////////////
    // Events
    public static abstract class ODashAuditEventFormEvent extends ComponentEvent<ODashAuditEventForm> {

        private OdashAuditEventFormBean formBean;

        protected ODashAuditEventFormEvent(ODashAuditEventForm source, OdashAuditEventFormBean user) {
            super(source, false);
            this.formBean = user;
        }

        public OdashAuditEventFormBean getUser() {
            return formBean;
        }
    }

    public static class SearchEvent extends ODashAuditEventFormEvent {

        public SearchEvent(ODashAuditEventForm source, OdashAuditEventFormBean formBean) {
            super(source, formBean);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
