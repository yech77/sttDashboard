package com.stt.dash.ui.views.bulksms;

import com.stt.dash.backend.data.Role;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Tag("file-to-send-editor")
@JsModule("./src/views/bulksms/file-to-send-editor.ts")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Route(value = BakeryConst.PAGE_BULKSMS_SCHEDULER+"nuevo", layout = MainView.class)
@PageTitle(BakeryConst.TITLE_BULKSMS_SCHEDULER)
@Secured({Role.ADMIN, "UI_USER"})
public class FileToSendEditor extends LitTemplate {
    public FileToSendEditor(){

    }
}
