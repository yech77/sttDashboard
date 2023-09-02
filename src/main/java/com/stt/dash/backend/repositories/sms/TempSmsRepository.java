package com.stt.dash.backend.repositories.sms;

import com.stt.dash.backend.data.entity.sms.TempSms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TempSmsRepository extends JpaRepository<TempSms, Long> {

    @Query(value = "SELECT * FROM temp_sms t ORDER BY id DESC LIMIT 1", nativeQuery = true)
    public TempSms searchMaxId();

    /**
     * @return null si no encunetra datos
     */
    public TempSms findFirst1ByOrderByIdDesc();

    @Modifying
    @Query(value = "insert into sms_hour "
            + "select nextval('hibernate_sequence'), 0, "
            + "o.carrier_char_code as carrier_char_code,  "
            + "(select c1.client_cod from client c1 inner join system_id s1 on c1.id = s1.client_id "
            + " where o.system_id = s1.system_id) as client_cod, "
            + "date_part('day', o.date) as day, "
            + "date_part('hour', o.date) as hour, "
            + "o.message_type, "
            + "date_part('month', o.date) as month, "
            + "o.system_id, "
            + "count(*), "
            + "date_part('year', o.date) as year "
            + "from temp_sms o "
            + "where o.Id<= :id "
            + "group by o.carrier_char_code, day, hour, o.message_type, month, o.system_id, year", nativeQuery = true)
    public void insertResume(Long id);

//    @Modifying
//    @Query(value = "DELETE FROM temp_sms o "
//            + "WHERE o.Id<= :id ", nativeQuery = true)
//    public void deleteByIdIsLessThanEqual(Long id);

    public long deleteByIdIsLessThanEqual(Long id);
}
