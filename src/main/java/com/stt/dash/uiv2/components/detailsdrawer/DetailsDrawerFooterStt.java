package com.stt.dash.uiv2.components.detailsdrawer;

import com.stt.dash.uiv2.components.FlexBoxLayout;
import com.stt.dash.uiv2.layout.size.Horizontal;
import com.stt.dash.uiv2.layout.size.Right;
import com.stt.dash.uiv2.layout.size.Vertical;
import com.stt.dash.uiv2.util.LumoStyles;
import com.stt.dash.uiv2.util.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;

public class DetailsDrawerFooterStt extends FlexBoxLayout {

    private Button save;
    private Button cancel;

    public DetailsDrawerFooterStt() {
        setBackgroundColor(LumoStyles.Color.Contrast._5);
        setPadding(Horizontal.RESPONSIVE_L, Vertical.S);
        setSpacing(Right.S);
        setWidthFull();

        save = UIUtils.createPrimaryButton("Guardar");
        cancel = UIUtils.createTertiaryButton("Cancelar");
        add(cancel, save);
    }

    public Registration addSaveListener(
            ComponentEventListener<ClickEvent<Button>> listener) {
        return save.addClickListener(listener);
    }

    public Registration addCancelListener(
            ComponentEventListener<ClickEvent<Button>> listener) {
        return cancel.addClickListener(listener);
    }

}
