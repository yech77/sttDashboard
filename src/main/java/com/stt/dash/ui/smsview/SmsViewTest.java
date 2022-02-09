package com.stt.dash.ui.smsview;

import com.stt.dash.app.session.SetGenericBean;
import com.stt.dash.backend.data.OUserSession;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.service.AbstractSmsService;
import com.stt.dash.backend.service.CarrierService;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.BakeryConst;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

@Tag("sms-view-test")
@Route(value = BakeryConst.PAGE_SMS_VIEW + "test", layout = MainView.class)
@PageTitle(BakeryConst.TITLE_SMS_VIEW + "test")
public class SmsViewTest extends HorizontalLayout {

    private int totalAmountOfPages;
    private int itemsPerPage = 25;
    private int currentPageNumber = 1;
    AbstractSmsService sms_serv;
    private IntegerField textboxCurrentPage = new IntegerField("");
    private Label labelTotalAmountOfPages = new Label();
    Grid<AbstractSMS> grid;

    public SmsViewTest(@Autowired AbstractSmsService sms_serv,
                       @Autowired OUserSession ouser_session,
                       @Autowired CarrierService carrier_serv,
                       @Autowired SetGenericBean<SystemId> systemIdSetGenericBean) {
        this.sms_serv = sms_serv;
        grid = new Grid<>(AbstractSMS.class);
        Page<AbstractSMS> pageSms = sms_serv.findAll(0, itemsPerPage);
        totalAmountOfPages = pageSms.getTotalPages();
        labelTotalAmountOfPages.setText("/" + totalAmountOfPages);
        /**/
        ComboBox<Integer> comboItemsPerPage = new ComboBox<>("Sms por página");
        comboItemsPerPage.setItems(Arrays.asList(25, 50, 100, 200, 400, 800));
        comboItemsPerPage.setValue(itemsPerPage);
        comboItemsPerPage.addValueChangeListener(change -> {
            if (change.isFromClient()) {
                itemsPerPage = change.getValue();
                updateDataView(updateDataPage(itemsPerPage, 0));
            }
        });

        grid.removeAllColumns();
        grid.addColumn(AbstractSMS::getDestination).setHeader("Country").setSortable(true);
        grid.addColumn(AbstractSMS::getDate).setHeader("State").setSortable(true);
        grid.addColumn(AbstractSMS::getCarrierCharCode).setHeader("Name").setSortable(true);
        grid.addColumn(AbstractSMS::getMessageType).setHeader("Name").setSortable(true);
        grid.setPageSize(itemsPerPage);

        List<AbstractSMS> initialItems = pageSms.getContent();
        grid.setItems(initialItems);

        textboxCurrentPage.setValue(1);
        textboxCurrentPage.setMin(1);
        textboxCurrentPage.setMax(totalAmountOfPages);
        textboxCurrentPage.setHasControls(true);
        textboxCurrentPage.addValueChangeListener(change -> {
            if (change.isFromClient()) {
                updateDataPage(itemsPerPage, change.getValue().intValue() - 1);
            }
        });
//
//        Button nextButton = new Button("Next page", e -> {
//            if (currentPageNumber >= totalAmountOfPages) {
//                return;
//            }
//            updateDataPage(itemsPerPage, ++currentPageNumber);
//        });
//        Button previousButton = new Button("Previous page", e -> {
//            if (currentPageNumber <= 1) {
//                return;
//            }
//            updateDataPage(itemsPerPage, --currentPageNumber);
//        });
        add(grid, comboItemsPerPage, textboxCurrentPage, labelTotalAmountOfPages);
    }

    private Page<AbstractSMS> updateDataPage(int itemsPerPage, int page) {
        Page<AbstractSMS> pageSms = sms_serv.findAll(page, itemsPerPage);
        grid.setPageSize(itemsPerPage);
        grid.setItems(pageSms.getContent());
        return pageSms;
    }

    private void updateDataView(Page<AbstractSMS> pageSms) {
        /**/
        textboxCurrentPage.setValue(1);
        textboxCurrentPage.setMin(1);
        textboxCurrentPage.setMax(pageSms.getTotalPages());
        /**/
        labelTotalAmountOfPages.setText("/" + pageSms.getTotalPages());
    }
}