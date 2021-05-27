package com.stt.dash.ui.components;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.templatemodel.TemplateModel;

@Tag("carrier-filter-bar")
@JsModule("./src/components/carrier-filter-bar.js")
public class CarrierFilterBar extends PolymerTemplate<CarrierFilterBar.Model> {

    public interface Model extends TemplateModel {
        boolean isCheckboxChecked();

        void setCheckboxChecked(boolean checkboxChecked);

        void setCheckboxText(String checkboxText);

        void setButtonText(String actionText);
    }

    @Id("field")
    private TextField textField;

    @Id("combo")
    private ComboBox carrierCombo;

    @Id("clear")
    private Button clearButton;

    @Id("action")
    private Button actionButton;

    public CarrierFilterBar() {
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        ComponentUtil.addListener(textField, CarrierFilterBar.SearchValueChanged.class,
                e -> fireEvent(new CarrierFilterBar.FilterChanged(this, false)));
        clearButton.addClickListener(e -> {
            textField.clear();
            getModel().setCheckboxChecked(false);
        });

        getElement().addPropertyChangeListener("checkboxChecked", e -> fireEvent(new CarrierFilterBar.FilterChanged(this, false)));
    }

    public String getFilter() {
        return textField.getValue();
    }

    public boolean isCheckboxChecked() {
        return getModel().isCheckboxChecked();
    }

    public void setPlaceHolder(String placeHolder) {
        textField.setPlaceholder(placeHolder);
    }

    public void setActionText(String actionText) {
        getModel().setButtonText(actionText);
    }

    public void setCheckboxText(String checkboxText) {
        getModel().setCheckboxText(checkboxText);
    }

    public void addFilterChangeListener(ComponentEventListener<CarrierFilterBar.FilterChanged> listener) {
        this.addListener(CarrierFilterBar.FilterChanged.class, listener);
    }

    public void addActionClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        actionButton.addClickListener(listener);
    }

    public Button getActionButton() {
        return actionButton;
    }

    @DomEvent(value = "value-changed", debounce = @DebounceSettings(timeout = 300, phases = DebouncePhase.TRAILING))
    public static class SearchValueChanged extends ComponentEvent<TextField> {
        public SearchValueChanged(TextField source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    public static class FilterChanged extends ComponentEvent<CarrierFilterBar> {
        public FilterChanged(CarrierFilterBar source, boolean fromClient) {
            super(source, fromClient);
        }
    }
}
