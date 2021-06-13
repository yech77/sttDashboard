package com.stt.dash.ui.views.bulksms;

import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.FileToSendSummary;
import com.stt.dash.ui.utils.ODateUitls;
import com.vaadin.flow.data.renderer.TemplateRenderer;

import java.time.LocalDate;

import static com.stt.dash.ui.utils.FormattingUtils.*;

/**
 * Help class to get ready to use TemplateRenderer for displaying order card list on the Storefront and Dashboard grids.
 * Using TemplateRenderer instead of ComponentRenderer optimizes the CPU and memory consumption.
 * <p>
 * In addition, component includes an optional header above the order card. It is used
 * to visually separate orders into groups. Technically all order cards are
 * equivalent, but those that do have the header visible create a visual group
 * separation.
 */
public class FileToSendCard {
    public static TemplateRenderer<FIlesToSend> getTemplate() {
        return TemplateRenderer.of(
                "<order-card"
                        + "  header='[[item.header]]'"
                        + "  order-card='[[item.orderCard]]'"
                        + "  on-card-click='cardClick'>"
                        + "</order-card>");
    }

    private static FileToSendStateConverter stateConverter = new FileToSendStateConverter();

    private boolean recent, inWeek;

    private final FileToSendSummary order;

    public FileToSendCard(FileToSendSummary order) {
        this.order = order;
        LocalDate now = LocalDate.now();
        LocalDate date = ODateUitls.valueOf(order.getDateToSend()).toLocalDate();
        recent = date.equals(now) || date.equals(now.minusDays(1));
        inWeek = !recent && now.getYear() == date.getYear() && now.get(WEEK_OF_YEAR_FIELD) == date.get(WEEK_OF_YEAR_FIELD);
    }

    public static FileToSendCard create(FileToSendSummary order) {
        return new FileToSendCard(order);
    }

//    public String getPlace() {
//        return recent || inWeek ? order.getPickupLocation().getName() : null;
//    }

    public String getTime() {
        return recent ? HOUR_FORMATTER.format(ODateUitls.valueOf(order.getDateToSend())) : null;
    }

    public String getShortDay() {
        return inWeek ? SHORT_DAY_FORMATTER.format(ODateUitls.valueOf(order.getDateToSend())) : null;
    }

    public String getSecondaryTime() {
        return inWeek ? HOUR_FORMATTER.format(ODateUitls.valueOf(order.getDateToSend())) : null;
    }

    public String getMonth() {
        return recent || inWeek ? null : MONTH_AND_DAY_FORMATTER.format(ODateUitls.valueOf(order.getDateToSend()));
    }

    public String getFullDay() {
        return recent || inWeek ? null : WEEKDAY_FULLNAME_FORMATTER.format(ODateUitls.valueOf(order.getDateToSend()));
    }

    public String getState() {
        return stateConverter.encode(order.getStatus());
    }

//    public String getFullName() {
//        return order.getCustomer().getFullName();
//    }

//    public List<OrderItem> getItems() {
//        return order.getItems();
//    }

}
