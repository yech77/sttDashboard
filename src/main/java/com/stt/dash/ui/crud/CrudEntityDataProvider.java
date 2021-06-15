package com.stt.dash.ui.crud;

import java.util.List;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.AbstractEntitySequence;
import com.stt.dash.backend.service.FilterableCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.QuerySortOrderBuilder;

public class CrudEntityDataProvider<T extends AbstractEntitySequence> extends FilterablePageableDataProvider<T, String> {

	private final FilterableCrudService<T> crudService;
	private List<QuerySortOrder> defaultSortOrders;
	private  CurrentUser currentUser;

	public CrudEntityDataProvider(FilterableCrudService<T> crudService) {
		this.crudService = crudService;
		setSortOrders();
	}
	public CrudEntityDataProvider(FilterableCrudService<T> crudService, CurrentUser currentUser) {
		this(crudService);
		this.currentUser = currentUser;
	}

	private void setSortOrders() {
		QuerySortOrderBuilder builder = new QuerySortOrderBuilder();
		builder.thenAsc("id");
		defaultSortOrders = builder.build();
	}

	@Override
	protected Page<T> fetchFromBackEnd(Query<T, String> query, Pageable pageable) {
		System.out.println("FECHAFROMBACKEND");
		return crudService.findAnyMatching(currentUser, query.getFilter(), pageable);
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return defaultSortOrders;
	}

	@Override
	protected int sizeInBackEnd(Query<T, String> query) {
		return (int) crudService.countAnyMatching(query.getFilter());
	}

}

