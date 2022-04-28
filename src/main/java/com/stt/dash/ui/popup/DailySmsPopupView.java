package com.stt.dash.ui.popup;

import com.stt.dash.backend.data.AbstractSmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
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
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;


@Tag("sms-show-grid-view-v2")
@JsModule("./src/views/smsgridview/sms-show-grid-view-v2.ts")
public abstract class DailySmsPopupView extends LitTemplate implements Viewnable<SmsByYearMonthDayHour> {

    @Id("grid-title")
    H3 title;

    @Id("grid-subtitle")
    H5 subtitle;

    @Id("grid-btn-download")
    Button downloadButton;

    @Id("grid-btn-close")
    Button closeButton;

    @Id("smsGrid")
    Grid<SmsByYearMonthDayHour> grid;

    protected DailySmsPopupPresenter presenter;
    protected Grid.Column<SmsByYearMonthDayHour> groupByColum;
    protected Grid.Column<SmsByYearMonthDayHour> someCodeColum;
    protected Grid.Column<SmsByYearMonthDayHour> totalColumn;
    protected Grid.Column<SmsByYearMonthDayHour> messageTypeColum;
    protected Grid.Column<SmsByYearMonthDayHour> dateColumn;

    protected Consumer<String> consumer;


    @Override
    public void setGridDataProvider(ListDataProvider<SmsByYearMonthDayHour> dataProvider) {
        grid.setDataProvider(dataProvider);
    }

    public abstract void createGroupByColumn(Grid<SmsByYearMonthDayHour> grid);

    public abstract void createSomeCodeColumn(Grid<SmsByYearMonthDayHour> grid);

    public abstract void createColumns(Grid<SmsByYearMonthDayHour> grid);


    public void createMessageType() {
        someCodeColum = grid.addColumn(AbstractSmsByYearMonth::getMessageType)
                .setComparator(com -> com.getMessageType())
                .setHeader("Tipo de Mensaje")
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
