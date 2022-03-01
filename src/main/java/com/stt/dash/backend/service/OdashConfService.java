package com.stt.dash.backend.service;

import com.google.gson.Gson;
import com.googlecode.gentyref.TypeToken;
import com.stt.dash.backend.data.entity.ODashConf;
import com.stt.dash.backend.repositories.OdashConfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OdashConfService {
    public enum ODASH_CONF_TYPE {
        SYNC(1, "SYNC");

        private Integer idConf;
        private String dscConf;

        ODASH_CONF_TYPE(Integer idConf, String dscConf) {
            this.idConf = idConf;
            this.dscConf = dscConf;
        }

        public Integer getIdConf() {
            return idConf;
        }

        public void setIdConf(Integer idConf) {
            this.idConf = idConf;
        }

        public String getDscConf() {
            return dscConf;
        }

        public void setDscConf(String dscConf) {
            this.dscConf = dscConf;
        }
    }

    private final Gson gson = new Gson();
    Type gsonType = new TypeToken<HashMap<String, String>>() {
    }.getType();

    private final OdashConfRepository repo_conf;

    public OdashConfService(@Autowired OdashConfRepository repo_conf) {
        this.repo_conf = repo_conf;
    }

    /**
     * @param odashConf
     * @return Map con variables o Map vacio.
     */
    public Map<String, String> findSyncConfData(ODASH_CONF_TYPE odashConf) {
        Optional<ODashConf> optionalODashConf = findSyncConf(odashConf);
        if (!optionalODashConf.isPresent()) {
            return new HashMap<>(1);
        }
        Map<String, String> map = gson.fromJson(optionalODashConf.get().getSyncData(), gsonType);
        return map;
    }

    public Optional<ODashConf> findSyncConf(ODASH_CONF_TYPE odashConf) {
        return repo_conf.findBySyncId(odashConf.getIdConf());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public ODashConf save(ODASH_CONF_TYPE oDashConf, Map<String, String> values, Long id) {
        Optional<ODashConf> odc = repo_conf.findBySyncId(oDashConf.getIdConf());
        String v = gson.toJson(values);
        /**/
        ODashConf oDashConf1 = new ODashConf();
        oDashConf1.setSyncId(oDashConf.getIdConf());
//        oDashConf1.setId(id);
        if (odc.isPresent()) {
            oDashConf1 = odc.get();
        }
        oDashConf1.setSyncData(v);
        return repo_conf.save(oDashConf1);
    }

    public ODashConf save(ODashConf oDashConf) {
        return repo_conf.save(oDashConf);
    }

}
