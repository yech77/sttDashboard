package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.ODashAuditEvent;
import com.stt.dash.backend.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ODashAuditEventRepository extends JpaRepository<ODashAuditEvent, Long> {

    public Page<ODashAuditEvent> findBy(Pageable pageable);

    /**
     * Por usuario y todos los eventos
     *
     * @param princial
     * @param firstDate
     * @param secondDate
     * @return
     */
    public Page<ODashAuditEvent> findAllByPrincipalAndEventDateBetweenOrderByEventDateDesc(String princial,
                                                                                           Date firstDate,
                                                                                           Date secondDate, Pageable pageable);

    /**
     * Todos los usuarios y todos los eventos
     *
     * @param firstDate
     * @param secondDate
     * @return
     */
    public Page<ODashAuditEvent> findAllByEventDateBetweenOrderByEventDateDesc(
            Date firstDate,
            Date secondDate, Pageable pageable);

    /**
     * Todos los usuarios hijos y un evento.
     *
     * @param principals
     * @param eventType
     * @param firstDate
     * @param secondDate
     * @return
     */
    public Page<ODashAuditEvent> findAllByPrincipalInAndEventTypeAndEventDateBetweenOrderByEventDateDesc(
            List<String> principals,
            ODashAuditEvent.OEVENT_TYPE eventType,
            Date firstDate,
            Date secondDate, Pageable pageable);

    /**
     * Por usuario y por tipo de evento.
     *
     * @param princial
     * @param eventType
     * @param firstDate
     * @param secondDate
     * @return
     */
    public Page<ODashAuditEvent> findAllByPrincipalAndEventTypeAndEventDateBetweenOrderByEventDateDesc(String princial,
                                                                                                       ODashAuditEvent.OEVENT_TYPE eventType,
                                                                                                       Date firstDate, Date secondDate, Pageable pageable);
}

