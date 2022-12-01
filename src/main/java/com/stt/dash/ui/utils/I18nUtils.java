package com.stt.dash.ui.utils;

import com.vaadin.componentfactory.multiselect.MultiComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.Arrays;
import java.util.function.Supplier;

public class I18nUtils {
    public static Supplier<DatePicker.DatePickerI18n> datePickerI18nSupplier = () -> {
        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
        i18n.setCancel("Cancelar");
        i18n.setToday("Hoy");
        i18n.setClear("Limpiar");
        i18n.setMonthNames(Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"));
        i18n.setWeek("Semana");
        i18n.setWeekdays(Arrays.asList("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sábado", "Domingo"));
        i18n.setWeekdaysShort(Arrays.asList("Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sáb"));
        return i18n;
    };

    public static MultiComboBox.MultiComboBoxI18n getMulticomboI18n() {
        MultiComboBox.MultiComboBoxI18n i18n = new MultiComboBox.MultiComboBoxI18n();
        i18n.setClear("Ninguno");
        i18n.setSelect("Todos");
        return i18n;
    }

    public static DatePicker.DatePickerI18n getDatepickerI18n() {
        return datePickerI18nSupplier.get();
    }
}
