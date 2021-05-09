package com.stt.dash.backend.repositories;

import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.SmsHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SmsHourRepository extends JpaRepository<SmsHour, Long> {

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "SUM(h.total), h.year, h.month, h.clientCod) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.clientCod "
            + "ORDER BY  h.year, h.month, h.clientCod ")
    List<SmsByYearMonth> groupClientByYeMoWhereYeMo(@Param("yearSms") int yearSms,
                                                    @Param("monthSms") int monthSms,
                                                    @Param("list_sid") List<String> list_sid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.clientCod) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.day, h.clientCod "
            + "ORDER BY  h.year, h.month, h.day, h.clientCod ")
    List<SmsByYearMonthDay> groupClientByYeMoDaWhereYeMoDa(@Param("yearSms") int yearSms,
                                                           @Param("monthSms") int monthSms,
                                                           @Param("daySms") int daySms,
                                                           @Param("list_sid") List<String> list_sid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "SUM(h.total), h.year, h.month) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month "
            + "ORDER BY  h.year, h.month ")
    List<SmsByYearMonth> groupByYeMoWhereYeMo(@Param("yearSms") int yearSms,
                                              @Param("monthSms") int monthSms,
                                              @Param("list_sid") List<String> list_sid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "SUM(h.total), h.year, h.month) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month "
            + "ORDER BY  h.year, h.month ")
    List<SmsByYearMonth> groupByYeMoWhereYeType(@Param("yearSms") int yearSms,
                                                @Param("messageType") String messageType,
                                                @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH WHERE: YEAR, LIST-SID
     *
     * @param yearSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "SUM(h.total), h.year, h.month) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month "
            + "ORDER BY h.year, h.month")
    List<SmsByYearMonth> groupByYeMoWhereYe(@Param("yearSms") int yearSms,
                                            @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY WHERE: YEAR, MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.day "
            + "ORDER BY  h.year, h.month, h.day ")
    List<SmsByYearMonthDay> groupByYeMoDaWhereYeMo(@Param("yearSms") int yearSms,
                                                   @Param("monthSms") int monthSms,
                                                   @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY WHERE: YEAR, MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageType
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.day "
            + "ORDER BY  h.year, h.month, h.day ")
    List<SmsByYearMonthDay> groupByYeMoDaWhereYeMoType(@Param("yearSms") int yearSms,
                                                       @Param("monthSms") int monthSms,
                                                       @Param("messageType") String messageType,
                                                       @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY, HOUR WHERE: YEAR, MONTH, DAY, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour "
            + "ORDER BY  h.year, h.month, h.day, h.hour ")
    List<SmsByYearMonthDayHour> groupByYeMoDaHoWhereYeMoDa(@Param("yearSms") int yearSms,
                                                           @Param("monthSms") int monthSms,
                                                           @Param("daySms") int daySms,
                                                           @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY, HOUR WHERE: YEAR, MONTH, DAY, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageType
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour "
            + "ORDER BY  h.year, h.month, h.day, h.hour ")
    List<SmsByYearMonthDayHour> groupByYeMoDaHoWhereYeMoDaType(@Param("yearSms") int yearSms,
                                                               @Param("monthSms") int monthSms,
                                                               @Param("daySms") int daySms,
                                                               @Param("messageType") String messageType,
                                                               @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: POR YEAR, MONTH, MESSAGETYPE WHERE: YEAR, MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.messageType "
            + "ORDER BY  h.year, h.month, h.messageType")
    List<SmsByYearMonth> groupMessageTypeByYearMonth(@Param("yearSms") int yearSms,
                                                     @Param("monthSms") int monthSms,
                                                     @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, MESSAGETYPE WHERE: YEAR, LIST-MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.messageType "
            + "ORDER BY h.year, h.month, h.messageType")
    List<SmsByYearMonth> groupMessageTypeByYeMoWhMoIn(@Param("yearSms") int yearSms,
                                                      @Param("monthSms") List<Integer> monthSms,
                                                      @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, CARRIER WHERE: YEAR, LIST-MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.carrierCharCode ")
    List<SmsByYearMonth> groupCarrierByYeMoWhMoIn(@Param("yearSms") int yearSms,
                                                  @Param("monthSms") List<Integer> monthSms,
                                                  @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, CARRIER WHERE: YEAR, LIST-MONTH, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.carrierCharCode ")
    List<SmsByYearMonth> groupCarrierByYeMoWhMoInMessageTypeIn(@Param("yearSms") int yearSms,
                                                               @Param("monthSms") List<Integer> monthSms,
                                                               @Param("messageTypeSms") List<String> messageTypeSms,
                                                               @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, CARRIER WHERE: YEAR, LIST-MONTH, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.systemId "
            + "ORDER BY h.year, h.month, h.systemId ")
    List<SmsByYearMonth> groupSystemIdByYeMoWhMoInMessageTypeIn(@Param("yearSms") int yearSms,
                                                                @Param("monthSms") List<Integer> monthSms,
                                                                @Param("messageTypeSms") List<String> messageTypeSms,
                                                                @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY, CARRIER WHERE: YEAR, MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.day, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.carrierCharCode")
    List<SmsByYearMonthDay> groupCarrierByYeMoDa(@Param("yearSms") int yearSms,
                                                 @Param("monthSms") int monthSms,
                                                 @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY, CARRIER WHERE: YEAR, MONTH, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.day, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.carrierCharCode")
    List<SmsByYearMonthDay> groupCarrierByYeMoDaWhMessageTypeIn(@Param("yearSms") int yearSms,
                                                                @Param("monthSms") int monthSms,
                                                                @Param("messageTypeSms") List<String> messageTypeSms,
                                                                @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY, CARRIER WHERE: YEAR, MONTH, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.day, h.systemId "
            + "ORDER BY h.year, h.month, h.day, h.systemId")
    List<SmsByYearMonthDay> groupSystemIdByYeMoDaWhMessageTypeIn(@Param("yearSms") int yearSms,
                                                                 @Param("monthSms") int monthSms,
                                                                 @Param("messageTypeSms") List<String> messageTypeSms,
                                                                 @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY CARRIER WHERE: YEAR, MONTH, DAY,
     * LIST-MESSAGETYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageTypeSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.day, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.carrierCharCode")
    List<SmsByYearMonthDay> groupCarrierByYeMoDaWhYeMoDaEqMessageTypeIn(@Param("yearSms") int yearSms,
                                                                        @Param("monthSms") int monthSms,
                                                                        @Param("daySms") int daySms,
                                                                        @Param("messageTypeSms") List<String> messageTypeSms,
                                                                        @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY CARRIER WHERE: YEAR, MONTH, DAY,
     * LIST-MESSAGETYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageTypeSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.day, h.systemId "
            + "ORDER BY h.year, h.month, h.day, h.systemId")
    List<SmsByYearMonthDay> groupSystemIdByYeMoDaWhYeMoDaEqMessageTypeIn(@Param("yearSms") int yearSms,
                                                                         @Param("monthSms") int monthSms,
                                                                         @Param("daySms") int daySms,
                                                                         @Param("messageTypeSms") List<String> messageTypeSms,
                                                                         @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY, HOUR, CARRIER WHERE: YEAR, MONTH, DAY,
     * LIST-MESSAGETYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageTypeSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.day, h.hour, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.hour, h.carrierCharCode")
    List<SmsByYearMonthDayHour> groupCarrierByYeMoDaHoWhMessageTypeIn(@Param("yearSms") int yearSms,
                                                                      @Param("monthSms") int monthSms,
                                                                      @Param("daySms") int daySms,
                                                                      @Param("messageTypeSms") List<String> messageTypeSms,
                                                                      @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY, HOUR, SYSTEMID WHERE: YEAR, MONTH, DAY,
     * LIST-MESSAGETYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageTypeSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.day, h.hour, h.systemId "
            + "ORDER BY h.year, h.month, h.day, h.hour, h.systemId")
    List<SmsByYearMonthDayHour> groupSystemIdByYeMoDaHoWhMessageTypeIn(@Param("yearSms") int yearSms,
                                                                       @Param("monthSms") int monthSms,
                                                                       @Param("daySms") int daySms,
                                                                       @Param("messageTypeSms") List<String> messageTypeSms,
                                                                       @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, CARRIER WHERE: YEAR, MONTH, MESSAGE TYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageType a buscar
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "SUM(h.total), h.year, h.month, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.carrierCharCode")
    List<SmsByYearMonth> groupCarrierByYeMo(@Param("yearSms") int yearSms,
                                            @Param("monthSms") int monthSms,
                                            @Param("messageType") String messageType,
                                            @Param("list_sid") List<String> list_sid);

    /**
     * GROUP: YEAR, MONTH, DAY, CARRIER WHERE: YEAR, MONTH, MESSAGE TYPE,
     * LIST-SID.
     *
     * @param yearSms
     * @param monthSms
     * @param messageType a buscar
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.day, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.carrierCharCode")
    List<SmsByYearMonthDay> groupCarrierByYeMoDa(@Param("yearSms") int yearSms,
                                                 @Param("monthSms") int monthSms,
                                                 @Param("messageType") String messageType,
                                                 @Param("list_sid") List<String> list_sid);

    /**
     * AGRUPACION SYSTEMID MENSUAL: Por YEAR, MONTH, DAY, SYSTEMID. WHERE
     * MESSAGETYPE
     *
     * @param yearSms
     * @param monthSms
     * @param messageType a buscar
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.day, h.systemId "
            + "ORDER BY h.year, h.month, h.day, h.systemId")
    List<SmsByYearMonthDay> groupSystemIdByYeMoDa(@Param("yearSms") int yearSms,
                                                  @Param("monthSms") int monthSms,
                                                  @Param("messageType") String messageType,
                                                  @Param("list_sid") List<String> list_sid);

    /**
     * AGRUPACION: YEAR + MONTH + DAY + HOUR + CARRIER CONDICION: SIDS +
     * MESSAGE_TYPE
     *
     * @param yearSms
     * @param monthSms
     * @param messageType a buscar
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY h.year, h.month, h.day, h.hour, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.hour, h.carrierCharCode")
    List<SmsByYearMonthDayHour> groupCarrierByYeMoDaHo(@Param("yearSms") int yearSms,
                                                       @Param("monthSms") int monthSms,
                                                       @Param("messageType") String messageType,
                                                       @Param("list_sid") List<String> list_sid);

    /**
     * GRUPO POR YEAR, MONTH, DAY, MESSAGETYPE
     *
     * @param yearSms
     * @param monthSms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.day, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.messageType")
    List<SmsByYearMonthDay> groupMessageTypeByYearMonthDay(@Param("yearSms") int yearSms,
                                                           @Param("monthSms") int monthSms,
                                                           @Param("list_sid") List<String> list_sid);

    /**
     * GRUPO POR YEAR, MONTH, DAY, HOUR, MESSAGETYPE
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE  h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour, h.messageType "
            + "ORDER BY  h.year, h.month, h.day, h.hour, h.messageType")
    List<SmsByYearMonthDayHour> groupMessageTypeByYearMonthDayHour(@Param("yearSms") int yearSms,
                                                                   @Param("monthSms") int monthSms,
                                                                   @Param("daySms") int daySms,
                                                                   @Param("list_sid") List<String> list_sid);

    /**
     * GRUPO POR YEAR, MONTH, DAY, SYSTEMID
     *
     * @param yearSms
     * @param monthSms
     * @param messageType
     * @param list_sid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month, h.day, h.systemId "
            + "ORDER BY h.year, h.month, h.day, h.systemId")
    List<SmsByYearMonthDay> groupSystemIdByYearMonthDay(@Param("yearSms") int yearSms,
                                                        @Param("monthSms") int monthSms,
                                                        @Param("messageType") String messageType,
                                                        @Param("list_sid") List<String> list_sid);

    /**
     * **** TOTAL ********
     */
    /**
     * TOTAL DE UN TIPO DE MENSAJE. POR MES
     *
     * @param yearSms
     * @param monthSms
     * @param list_sid
     * @param messageType
     * @return
     */
    @Query("SELECT COUNT(h) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:list_sid) "
            + "GROUP BY  h.year, h.month "
            + "ORDER BY  h.year, h.month ")
    Long totalMessageTypeByYearMonth(@Param("yearSms") int yearSms,
                                     @Param("monthSms") int monthSms,
                                     @Param("list_sid") List<String> list_sid,
                                     @Param("messageType") String messageType);
}
