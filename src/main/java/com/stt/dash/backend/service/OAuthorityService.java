package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.OAuthority;
import com.stt.dash.backend.repositories.OAuthorityRepository;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OAuthorityService {

    private static String UI_CODE = "SERV_OAUTH";
    private static final Logger log = LoggerFactory.getLogger(OAuthorityService.class.getName());
    private OAuthorityRepository auth_repo;

    public OAuthorityService(OAuthorityRepository auth_repo) {
        this.auth_repo = auth_repo;
    }

    private String getStringLog(){
        String id = VaadinSession.getCurrent().getSession().getId();
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(id).append("] [").append(UI_CODE).append("]");
        return sb.toString();
    }

    public List<OAuthority> findAll(){
        return auth_repo.findAll();
    }

    public List<OAuthority> findAll(String filterText){
        if(filterText.length()<1){
            return auth_repo.findAll();
        }
        return auth_repo.findByAuthName(filterText);
    }

    public List<OAuthority> findByAuthName(String authName){
        return auth_repo.findByAuthName(authName);
    }

    public void delete(OAuthority auth) {
        try {
            auth_repo.delete(auth);
            log.info("{} Deleted: [{}]", getStringLog(), auth.getAuthName());
        } catch (Exception d) {
            log.error("{} Error on Delete [{}]:", getStringLog(), auth.getAuthName());
            log.error("", d);
        }
    }

    public void save(OAuthority auth) {
        if (auth == null) {
            log.warn("{} OAuthority is null", getStringLog());
            return;
        }
        try {
            Long id = auth.getId();
            auth_repo.save(auth);
            if(id == null) {
                log.info("{} Saved: OAuthority[{}]", getStringLog(), auth.getAuthName());
            } else {
                log.info("{} Updated: OAuthority[{}]", getStringLog(), auth.getAuthName());
            }
        } catch (Exception d) {
            log.error("{} Error on Save:", getStringLog());
            log.error("", d);
        }
    }
}
