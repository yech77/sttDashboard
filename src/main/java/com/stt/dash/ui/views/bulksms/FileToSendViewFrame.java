package com.stt.dash.ui.views.bulksms;

import com.stt.dash.ui.MainView;
import com.stt.dash.uiv2.components.FlexBoxLayout;
import com.stt.dash.uiv2.components.detailsdrawer.DetailsDrawer;
import com.stt.dash.uiv2.components.detailsdrawer.DetailsDrawerFooterStt;
import com.stt.dash.uiv2.layout.size.Horizontal;
import com.stt.dash.uiv2.layout.size.Left;
import com.stt.dash.uiv2.layout.size.Right;
import com.stt.dash.uiv2.layout.size.Top;
import com.stt.dash.uiv2.layout.size.Vertical;
import com.stt.dash.uiv2.util.BoxShadowBorders;
import com.stt.dash.uiv2.util.LumoStyles;
import com.stt.dash.uiv2.util.css.BoxSizing;
import com.stt.dash.views.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "programacion", layout = MainView.class)
@PageTitle("Programacion de Archivos")
public class FileToSendViewFrame extends ViewFrame {
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerFooterStt footerDrawer;

    public FileToSendViewFrame() {
        setViewContent(createDrawerContent());

    }

    private Component createDrawerContent() {
        FileToSendForm form = new FileToSendForm();
        FlexBoxLayout content = new FlexBoxLayout(form);
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setMaxWidth("50%");
        content.setBackgroundColor(LumoStyles.Color.BASE_COLOR);
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
        content.setSpacing(Right.XL, Left.XL);
        content.setMargin(Horizontal.AUTO, Vertical.RESPONSIVE_M);
        content.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        content.addClassName(BoxShadowBorders.BOTTOM);
        return content;
    }

}
