package com.stt.dash;

import com.stt.dash.backend.service.TempSmsService;
import com.vaadin.flow.component.dependency.HtmlImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.stt.dash.app.security.SecurityConfiguration;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.UserRepository;
import com.stt.dash.backend.service.UserService;
import com.stt.dash.ui.MainView;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Spring boot web application initializer.
 */
@SpringBootApplication(scanBasePackageClasses = { SecurityConfiguration.class, MainView.class, Application.class,
		UserService.class }, exclude = ErrorMvcAutoConfiguration.class)
@EnableJpaRepositories(basePackageClasses = { UserRepository.class })
@EntityScan(basePackageClasses = { User.class })
@EnableScheduling
public class Application extends SpringBootServletInitializer {
	@Autowired
	TempSmsService temp_serv;
	private static String APP_NAME = "ODASH";
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	@Scheduled(cron = "0 */1 * * * ?")
	public void runResumeSms(){
		temp_serv.doResume();
	}

	public static String getAPP_NAME(){
		return APP_NAME;
	}
}
