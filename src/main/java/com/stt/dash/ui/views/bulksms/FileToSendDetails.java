package com.stt.dash.ui.views.bulksms;

import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.Order;
import com.stt.dash.ui.events.CancelEvent;
import com.stt.dash.ui.events.SaveEvent;
import com.stt.dash.ui.utils.FormattingUtils;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.utils.converters.*;
import com.stt.dash.ui.views.storefront.converters.StorefrontLocalDateConverter;
import com.stt.dash.ui.views.storefront.events.CommentEvent;
import com.stt.dash.ui.views.storefront.events.DeleteEvent;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.Encode;
import com.vaadin.flow.templatemodel.Include;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag("file-to-send-details")
@JsModule("./src/views/bulksms/file-to-send-details.ts")
public class FileToSendDetails extends LitTemplate {
    Logger log = LoggerFactory.getLogger(FileToSendDetails.class);

    private FIlesToSend order;

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

//    @Id("history")
//    private Element history;
//
//    @Id("comment")
//    private Element comment;
//
//    @Id("sendComment")
//    private Button sendComment;
//
//    @Id("commentField")
//    private TextField commentField;

    private boolean isDirty;

    public FileToSendDetails() {
//        sendComment.addClickListener(e -> {
//            String message = commentField.getValue();
//            message = message == null ? "" : message.trim();
//            if (!message.isEmpty()) {
//                commentField.clear();
//                fireEvent(new CommentEvent(this, order.getId(), message));
//            }
//        });
        save.addClickListener(e -> fireEvent(new SaveEvent(this, false)));
        cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
//        delete.addClickListener(e -> fireEvent(new DeleteEvent(this)));
    }

    public void display(FIlesToSend order, boolean review) {
        log.info("DISPLLAY: {}", review);
//        getModel().setReview(review);
//        getElement().setProperty("showing", true);
        this.order = order;
        showData();
        save.setVisible(review);
        edit.setVisible(!review);
        cancel.setVisible(!review);
        back.setVisible(review);
//        getModel().setItem(order);
        if (!review) {
            System.out.println(" REVIEW FALSE");
//            commentField.clear();
        }
        this.isDirty = review;
    }

    private void showData(){
        bulkday.setText(FormattingUtils.MONTH_AND_DAY_FORMATTER.format(ODateUitls.valueOf(order.getDateToSend())));
        bulktime.setText(FormattingUtils.HOUR_FORMATTER.format(ODateUitls.valueOf(order.getDateToSend())));
        orderName.setText(order.getOrderName());
        orderDescription.setText(order.getOrderDescription());
        systemid.setText(order.getSystemId());
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

   /* public interface Model extends TemplateModel {
        @Include({ "id", "dueDate.day", "dueDate.weekday", "dueDate.date", "dueTime", "state", "pickupLocation.name",
                "customer.fullName", "customer.phoneNumber", "customer.details", "items.product.name", "items.comment",
                "items.quantity", "items.product.price", "history.message", "history.createdBy.firstName",
                "history.timestamp", "history.newState", "totalPrice" })
        @Encode(value = LongToStringConverter.class, path = "id")
        @Encode(value = StorefrontLocalDateConverter.class, path = "dueDate")
        @Encode(value = LocalTimeConverter.class, path = "dueTime")
        @Encode(value = OrderStateConverter.class, path = "state")
        @Encode(value = CurrencyFormatter.class, path = "items.product.price")
        @Encode(value = LocalDateTimeConverter.class, path = "history.timestamp")
        @Encode(value = OrderStateConverter.class, path = "history.newState")
        @Encode(value = CurrencyFormatter.class, path = "totalPrice")
        void setItem(Order order);

        void setReview(boolean review);
    }*/

    public Registration addSaveListenter(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
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
