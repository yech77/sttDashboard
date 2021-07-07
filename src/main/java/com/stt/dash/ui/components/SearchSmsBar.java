package com.stt.dash.ui.components;

import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;

@Tag("search-sms-bar")
@JsModule("./src/components/search-sms-bar.ts")
public class SearchSmsBar extends LitTemplate {
    @Id("dueDate")
    EnhancedDateRangePicker dateRangePicker;

    public SearchSmsBar(){
    }
}
