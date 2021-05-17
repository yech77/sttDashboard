package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.OUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface FilesToSendRepository extends JpaRepository<FIlesToSend, Long> {

    @Query("select f from FIlesToSend f "
            + "WHERE lower(f.fileName) like lower(concat('%', :filterText, '%'))"
            + " AND f.systemId is :systemId")
    List<FIlesToSend> filterSearchNameSystemId(String filterText, String systemId);

    List<FIlesToSend> findBySystemId(String systemId);

    @Query("select f from FIlesToSend f "
            + "WHERE f.systemId IN (:list_sid) "
            + "ORDER BY f.dateToSend ASC ")
    List<FIlesToSend> getAllOrders(@Param("list_sid") List<String> list_sid);


    List<FIlesToSend> findByUserCreatorIn(List<OUser> users);

    @Query("select f from FIlesToSend f "
            + "WHERE f.dateToSend <= :now "
            + "AND f.readyToSend = true "
            + "AND f.beingProcessed = false "
            + "AND f.systemId IN (:list_sid) "
            + "ORDER BY f.dateToSend ASC ")
    List<FIlesToSend> getUnsentOrders(@Param("now") Date now,
                                      @Param("list_sid") List<String> list_sid);

    @Query("select f from FIlesToSend f "
            + "WHERE f.dateToSend <= :now "
            + "AND f.readyToSend = true "
            + "AND f.beingProcessed = false "
            + "ORDER BY f.dateToSend ASC ")
    List<FIlesToSend> getUnsentOrders(@Param("now") Date now);

    public List<FIlesToSend> findByOrderName(String name);
}
