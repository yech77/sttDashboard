/**
 *
 */
package com.stt.dash.ui.crud;

import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.service.FilesToSendService;
import com.stt.dash.ui.views.bulksms.FileToSendFrontView;
import com.vaadin.flow.component.grid.Grid;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Order;
import com.stt.dash.backend.service.OrderService;
import com.stt.dash.ui.views.storefront.StorefrontView;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.util.Map;
import java.util.TreeMap;

@Configuration
public class PresenterFactory {

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public EntityPresenter<Order, StorefrontView> orderEntityPresenter(OrderService crudService, CurrentUser currentUser) {
		return new EntityPresenter<>(crudService, currentUser);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public EntityPresenter<FIlesToSend, FileToSendFrontView> fileToSendEntityPresenter(FilesToSendService crudService, CurrentUser currentUser) {
		return new EntityPresenter<>(crudService, currentUser);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public <T> Grid<T> produceGrid(DependencyDescriptor injectionPoint) {
		System.out.println("*************** ****************** ******************* PRODUCEGRID");
		ResolvableType resolvableType = injectionPoint.getResolvableType();
		ResolvableType genericType = resolvableType.getGeneric(0);
		Class<?> itemType = genericType.resolve();
		System.out.println(itemType);
		return configureGrid((Class<T>) itemType);
	}

	private <T> Grid<T> configureGrid(Class<T> itemType) {
		System.out.println("*************** ****************** ******************* CONFIGUREDGRID");
		Grid<T> grid = new Grid<>(itemType);
		Map<Integer, GridColumnsHelper> gridColumns = new TreeMap();
		ReflectionUtils.doWithFields(itemType,
				field -> {
					GridColumn gridColumnAnnotation = field.getAnnotation(GridColumn.class);
					gridColumns.put(gridColumnAnnotation.order(), new GridColumnsHelper(field.getName(), gridColumnAnnotation.columnName()));
					System.out.println("---------------------------- " + field.getName() +" "+ gridColumnAnnotation.columnName());
				}, field->{return field.getAnnotation(GridColumn.class)!=null;});
		GridColumnsHelper[] columnArr = new GridColumnsHelper[gridColumns.size()];
		columnArr = gridColumns.values().toArray(columnArr);
		grid.removeAllColumns();
		for (GridColumnsHelper gridColumnsHelper : columnArr) {
			grid.addColumn(gridColumnsHelper.getFieldName())
					.setHeader(gridColumnsHelper.getColumnName())
					.setAutoWidth(true);
		}
		return grid;
	}

	private class GridColumnsHelper {
		String fieldName;
		String columnName;

		public GridColumnsHelper(String fieldName, String columnName) {
			this.fieldName = fieldName;
			this.columnName = columnName;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getColumnName() {
			return columnName;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

	}
}
