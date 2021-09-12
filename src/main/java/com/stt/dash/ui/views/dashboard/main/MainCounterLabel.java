package com.stt.dash.ui.views.dashboard.main;

import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.views.storefront.beans.OrdersCountData;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Tag("main-counter-label")
@JsModule("./src/views/main/main-counter-label.js")
public class MainCounterLabel extends PolymerTemplate<TemplateModel> {
	NumberFormat nf = NumberFormat.getNumberInstance(BakeryConst.APP_LOCALE);
	DecimalFormat formatter = (DecimalFormat) nf;
	@Id("title")
	private H4 title;

	@Id("subtitle")
	private Div subtitle;

	@Id("count")
	private Span count;

	public void setOrdersCountData(OrdersCountData data) {
		formatter.applyPattern("###,###,###");
		title.setText(data.getTitle());
		subtitle.setText(data.getSubtitle());
		count.setText(formatter.format(data.getCount()));
	}
}
