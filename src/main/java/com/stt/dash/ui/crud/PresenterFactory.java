/**
 *
 */
package com.stt.dash.ui.crud;

import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.service.FilesToSendService;
import com.stt.dash.ui.views.bulksms.FileToSendFrontView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.Order;
import com.stt.dash.backend.service.OrderService;
import com.stt.dash.ui.views.storefront.StorefrontView;

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
}
