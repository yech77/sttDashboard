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
                "<filetosend-card"
                        + "  header='[[item.header]]'"
                        + "  order-card='[[item.orderCard]]'"
                        + "  on-card-click='cardClick'>"
                        + "</filetosend-card>");
    }

    public static FileToSendCard create(FileToSendSummary order) {
        return new FileToSendCard(order);
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


//    public String getPlace() {
//        return recent || inWeek ? order.getPickupLocation().getName() : null;
//    }

    public String getTime() {
        return recent ? HOUR_FORMATTER.format(ODateUitls.valueOf(order.getDateToSend())) : null;
    }

    public String getShortDay() {
        return inWeek ? SHORT_DAY_FORMATTER.format(ODateUitls.valueOf(order.getDateToSend())) : null;
    }

    public String getSystemId() {
        return order.getSystemId();
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
        System.out.println("STATUS: " + order.getStatus().name());
        return order.getStatus().getText();
    }

    public String getFileName() {
        System.out.println("STATUS: " + order.getFileName());
        return order.getFileName();
    }

    public String getOrderName() {
        return order.getOrderName();
    }

    public String getOrderDescription() {
        return order.getOrderDescription();
    }

    public String getCreatedBy() {
        return "Creador";
    }

}
