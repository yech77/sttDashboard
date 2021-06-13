package com.stt.dash.app.session;

import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.app.security.SecurityUtils;
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
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableJpaAuditing
public class STTConfiguration {
    private static Logger log = LoggerFactory.getLogger(STTConfiguration.class);

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }

    /**
     * Devuelve todos los SystemIds de todos los clientes del
     * CurrentUser.
     *
     * @param currentUser
     * @param repo
     * @return
     */
    @Bean
    @VaadinSessionScope
    public SetGenericBean getComercialUserSystemId(CurrentUser currentUser,
                                                   SystemIdRepository repo) {
        Set<SystemId> allSystemId;
        if (currentUser.getUser().getUserType() != User.OUSER_TYPE.BY) {
            log.info("{} ***** {}", currentUser.getUser().getClient(),
                    currentUser.getUser().getClient().getSystemids());
            allSystemId = repo.findAllSystemId(currentUser.getUser().getEmail());
        } else {
            allSystemId = new HashSet<>(currentUser.getUser().getSystemids());
        }
        log.info("{} Created SystemId ({}) Session Bean For type [{}] ",
                currentUser.getUser().getEmail(),
                allSystemId.size(),
                currentUser.getUser().getUserType().name());
        return () -> allSystemId;
    }

    /**
     * Devuelve todos los SystemIds, en String, de todos los clientes del
     * CurrentUser.
     *
     * @param currentUser
     * @param repo
     * @return List de String
     */
    @Bean
    @VaadinSessionScope
    public ListGenericBean getUserSystemIdString(CurrentUser currentUser,
                                                   SystemIdRepository repo) {
        SetGenericBean<SystemId> s = getComercialUserSystemId(currentUser, repo);
        List<String> allSystemId = s.getSet().stream().map(SystemId::getSystemId).collect(Collectors.toList());
        return () -> allSystemId;
    }

    /**
     * Devuelve todos los User hijos de CurrentUser y el mismo
     * Usuario actual.
     *
     * @param currentUser
     * @return
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ListGenericBean getUserMeAndChildren(CurrentUser currentUser) {
        User thisUser = currentUser.getUser();
        List<User> allUsers = new ArrayList<>();
        List<User> currentFam = new ArrayList<>();
        List<User> addingChildren = new ArrayList<>();

        currentFam.add(thisUser);
        addingChildren.addAll(thisUser.getUserChildren());
        while (addingChildren.size() > 0) {
            allUsers.addAll(currentFam);
            currentFam.clear();
            currentFam.addAll(addingChildren);
            addingChildren.clear();
            for (User user : currentFam) {
                addingChildren.addAll(user.getUserChildren());
            }
        }
        allUsers.addAll(currentFam);
        log.info("{} Created ListUser ({}) SCOPE_PROTOTYPE Bean",
                currentUser.getUser().getEmail(),
                allUsers.size());
        return () -> allUsers;
    }

    private class SpringSecurityAuditorAware implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            return Optional.of(SecurityUtils.getUsername());
        }
    }
}