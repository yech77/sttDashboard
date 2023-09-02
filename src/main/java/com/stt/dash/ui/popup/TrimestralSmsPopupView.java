package com.stt.dash.ui.popup;

import com.stt.dash.app.OMonths;
import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.ui.Viewnable;
import com.stt.dash.ui.utils.FormattingUtils;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.data.provider.ListDataProvider;
import liquibase.pro.packaged.T;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;


@Tag("sms-show-grid-view-v2")
@JsModule("./src/views/smsgridview/sms-show-grid-view-v2.js")
public abstract class TrimestralSmsPopupView extends LitTemplate implements Viewnable<SmsByYearMonth> {

    @Id("grid-title")
    H3 title;

    @Id("grid-subtitle")
    H5 subtitle;

    @Id("grid-btn-download")
    Button downloadButton;

    @Id("grid-btn-close")
    Button closeButton;

    @Id("smsGrid")
    Grid<SmsByYearMonth> grid;

    protected TrimestralSmsPopupPresenter presenter;
    protected Grid.Column<SmsByYearMonth> groupByColum;
    protected Grid.Column<SmsByYearMonth> someCodeColum;
    protected Grid.Column<SmsByYearMonth> totalColumn;
    protected Grid.Column<SmsByYearMonth> messageTypeColum;
    protected Grid.Column<SmsByYearMonth> dateColumn;

    protected Consumer<String> consumer;


    @Override
    public void setGridDataProvider(ListDataProvider<SmsByYearMonth> dataProvider) {
        grid.setDataProvider(dataProvider);
    }

    public abstract void createGroupByColumn(Grid<SmsByYearMonth> grid);

    public abstract void createSomeCodeColumn(Grid<SmsByYearMonth> grid);

    public abstract void createColumns(Grid<SmsByYearMonth> grid);


    public void createMessageType() {
        someCodeColum = grid.addColumn(AbstractSmsByYearMonth::getMessageType)
                .setComparator(com -> com.getMessageType())
                .setHeader("Tipo de mensaje")
                .setAutoWidth(true);
    }

    public void createTotalColumn() {
        totalColumn = grid.addColumn(
                        total -> {
                            return FormattingUtils.getUiSmsFormatter().format(total.getTotal());
                        })
                .setHeader("TOTAL")
                .setTextAlign(ColumnTextAlign.END)
                .setAutoWidth(true);
    }

    public void setConsumer(Consumer<String> c) {
        consumer = c;
        closeButton.setEnabled(true);
        closeButton.addClickListener(buttonClickEvent -> consumer.accept("Cerrando"));
    }

    public void setTitles(String mainTitle, String subT) {
        title.setText(StringUtils.isEmpty(mainTitle) ? "" : mainTitle);
        subtitle.setText(StringUtils.isEmpty(subT) ? "" : subT);
    }
}
