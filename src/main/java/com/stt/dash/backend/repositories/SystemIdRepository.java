package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.SystemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface SystemIdRepository extends JpaRepository<SystemId, Long> {

    public List<SystemId> findBySystemId(String systemId);

    /**
     * Busca todos los SystemId de todos los clientes que pertenezcan al usuario
     * @param email Usuario
     * @return
     */
    @Query(value = "select * from system_id s " +
            "where s.client_id in ( " +
            "select client_id from user_has_clients us, user_info ui " +
            "where us.ouser_id = ui.id and ui.email = :email)", nativeQuery = true)
    public Set<SystemId> findAllSystemId(String email);
}
