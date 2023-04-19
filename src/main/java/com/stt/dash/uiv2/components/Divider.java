package com.stt.dash.uiv2.components;

import com.stt.dash.uiv2.util.LumoStyles;
import com.stt.dash.uiv2.util.UIUtils;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;

public class Divider extends FlexBoxLayout implements HasSize, HasStyle {

    private String CLASS_NAME = "divider";

    private Div divider;

    public Divider(String height) {
        this(Alignment.CENTER, height);
    }

    public Divider(Alignment alignItems, String height) {
        setAlignItems(alignItems);
        setClassName(CLASS_NAME);
        setHeight(height);

        divider = new Div();
        UIUtils.setBackgroundColor(LumoStyles.Color.Contrast._10, divider);
        divider.setHeight("1px");
        divider.setWidthFull();
        add(divider);
    }

}
