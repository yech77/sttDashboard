package com.stt.dash.ui.views.bulksms;

import com.stt.dash.backend.data.Status;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.ui.events.CancelEvent;
import com.stt.dash.ui.events.SaveEvent;
import com.stt.dash.ui.utils.FormattingUtils;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.views.bulksms.events.DeleteEventFileToSend;
import com.stt.dash.ui.views.storefront.events.CommentEvent;
import com.stt.dash.ui.views.storefront.events.EditEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag("file-to-send-details")
@JsModule("./src/views/bulksms/file-to-send-details.ts")
public class FileToSendDetails extends LitTemplate {
    Logger log = LoggerFactory.getLogger(FileToSendDetails.class);

    private FIlesToSend fIlesToSend;
    @Id("delete")
    private Button delete;

    @Id("back")
    private Button back;

    @Id("cancel")
    private Button cancel;

    @Id("save")
    private Button save;

    @Id("edit")
    private Button edit;

    @Id("bulkday")
    private H3 bulkday;

    @Id("bulktime")
    private H3 bulktime;

    @Id("orderName")
    private H3 orderName;

    @Id("systemid")
    private H3 systemid;

    @Id("orderDescription")
    private Paragraph orderDescription;

    private boolean isDirty;

    public FileToSendDetails() {
        save.addClickListener(e -> fireEvent(new SaveEvent(this, false)));
        cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
        delete.addClickListener(e -> fireEvent(new DeleteEventFileToSend(this)));
    }

    public void display(FIlesToSend fIlesToSend, boolean review) {
        log.info("DISPLLAY: {}", review);
        this.fIlesToSend = fIlesToSend;
        showData();
//        getModel().setItem(order);
        /* TODO: Hacer que funcione desde la plantilla.
        LitElement no trabaja con Models ais que la visibilidad de los botones se hace por aca directo en el componente.  */
        if (review) {
            save.setVisible(true);
            edit.setVisible(false);
            cancel.setVisible(false);
            back.setVisible(true);
            delete.setVisible(false);
        } else {
            save.setVisible(false);
            edit.setVisible(false);
            cancel.setVisible(true);
            back.setVisible(false);
            delete.setVisible(fIlesToSend.getStatus() != Status.COMPLETED);
        }
        this.isDirty = review;
    }

    private void showData() {
        bulkday.setText(FormattingUtils.MONTH_AND_DAY_FORMATTER.format(ODateUitls.valueOf(fIlesToSend.getDateToSend())));
        bulktime.setText(FormattingUtils.HOUR_FORMATTER.format(ODateUitls.valueOf(fIlesToSend.getDateToSend())));
        orderName.setText(fIlesToSend.getOrderName());
        orderDescription.setText(fIlesToSend.getOrderDescription());
        systemid.setText(fIlesToSend.getSystemId());
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public Registration addSaveListenter(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addDeleteListenter(ComponentEventListener<DeleteEventFileToSend> listener) {
        return addListener(DeleteEventFileToSend.class, listener);
    }

    public Registration addEditListener(ComponentEventListener<EditEvent> listener) {
        return addListener(EditEvent.class, listener);
    }

    public Registration addBackListener(ComponentEventListener<ClickEvent<Button>> listener) {
        return back.addClickListener(listener);
    }

    public Registration addCommentListener(ComponentEventListener<CommentEvent> listener) {
        return addListener(CommentEvent.class, listener);
    }

    public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }
}
