package com.stt.dash.ui.views.dashboard.main;

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

@Tag("main-counter-label")
@JsModule("./src/views/main/main-counter-label.js")
public class MainCounterLabel extends PolymerTemplate<TemplateModel> {
	DecimalFormat df = new DecimalFormat("###,###,###");
	@Id("title")
	private H4 title;

	@Id("subtitle")
	private Div subtitle;

	@Id("count")
	private Span count;

	public void setOrdersCountData(OrdersCountData data) {
		title.setText(data.getTitle());
		subtitle.setText(data.getSubtitle());
		count.setText(df.format(data.getCount()));
	}
}
