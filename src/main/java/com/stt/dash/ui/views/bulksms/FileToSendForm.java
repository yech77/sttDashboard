package com.stt.dash.ui.views.bulksms;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;

public class FileToSendForm extends FormLayout {
    private DatePicker dueDate2;
    private TimePicker dueTime;
    private Checkbox sendNow;
    private ComboBox status;
    private ComboBox systemid;
    private TextField orderName;
    private TextField orderDescription;
    private TextArea message;
    private TextField messageBuilded;
    private Checkbox acceptCheckbox;
    private Span warningSpan;

}
