package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.ODashAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ODashAuditEventRepository extends JpaRepository<ODashAuditEvent, Long> {

    /**
     * Por usuario y todos los eventos
     * @param princial
     * @param firstDate
     * @param secondDate
     * @return
     */
    public Optional<List<ODashAuditEvent>> findAllByPrincipalAndEventDateBetweenOrderByEventDateDesc(String princial,
                                                                                                     Date firstDate,
                                                                                                     Date secondDate);

    /**
     * Todos los usuarios y todos los eventos
     * @param firstDate
     * @param secondDate
     * @return
     */
    public Optional<List<ODashAuditEvent>> findAllByEventDateBetweenOrderByEventDateDesc(
            Date firstDate,
            Date secondDate);


//    /**
//     * Todos los usuarios y un evento.
//     * @param eventType
//     * @param firstDate
//     * @param secondDate
//     * @return
//     */
//    public Optional<List<ODashAuditEvent>> findAllByEventTypeAndEventDateBetweenOrderByEventDateDesc(ODashAuditEvent.OEVENT_TYPE eventType,
//            Date firstDate,
//            Date secondDate);
    /**
     * Todos los usuarios hijos y un evento.
     * @param principals
     * @param eventType
     * @param firstDate
     * @param secondDate
     * @return
     */
    public Optional<List<ODashAuditEvent>> findAllByPrincipalInAndEventTypeAndEventDateBetweenOrderByEventDateDesc(
            List<String> principals,
            ODashAuditEvent.OEVENT_TYPE eventType,
            Date firstDate,
            Date secondDate);
    /**
     * Por usuario y por tipo de evento.
     * @param princial
     * @param eventType
     * @param firstDate
     * @param secondDate
     * @return
     */
    public Optional<List<ODashAuditEvent>> findAllByPrincipalAndEventTypeAndEventDateBetweenOrderByEventDateDesc(String princial,
                                                                                                                 ODashAuditEvent.OEVENT_TYPE eventType,
                                                                                                                 Date firstDate,Date secondDate);
}

