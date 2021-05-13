package com.stt.dash.app.session;

import com.stt.dash.app.HasLogger;
import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.SystemId;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.SystemIdRepository;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class STTConfiguration {
    private static Logger log = LoggerFactory.getLogger(STTConfiguration.class);
    /**
     * Devuelve todos los SystemIds de todos los clientes del
     * Usuario actual.
     * @param currentUser
     * @param repo
     * @return
     */
    @Bean
    @VaadinSessionScope
    public ComercialUserSystemId getComercialUserSystemId(CurrentUser currentUser,
                                                          SystemIdRepository repo){
        Set<SystemId> allSystemId;
        if (currentUser.getUser().getUserType() != User.OUSER_TYPE.BY) {
            log.info("{} ***** {}", currentUser.getUser().getClient(),
                    currentUser.getUser().getClient().getSystemids());
            allSystemId = repo.findAllSystemId(currentUser.getUser().getEmail());
        }else{
            allSystemId = new HashSet<>(currentUser.getUser().getClient().getSystemids());
        }
        log.info("{} Created SystemId ({}) Session Bean For type [{}] ",
                currentUser.getUser().getEmail(),
                allSystemId.size(),
                currentUser.getUser().getUserType().name());
        return ()->allSystemId;
    }
}
