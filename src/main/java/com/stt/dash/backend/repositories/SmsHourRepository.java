package com.stt.dash.backend.repositories;

import com.stt.dash.app.OMessageType;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.data.entity.SmsHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SmsHourRepository extends JpaRepository<SmsHour, Long> {

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "SUM(h.total), h.year, h.month, h.clientCod) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.clientCod "
            + "ORDER BY  h.year, h.month, h.clientCod ")
    List<SmsByYearMonth> groupSmsClientByYeMoWhYeMoSyIn(@Param("yearSms") int yearSms,
                                                        @Param("monthSms") int monthSms,
                                                        @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.clientCod) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.clientCod "
            + "ORDER BY  h.year, h.month, h.day, h.clientCod ")
    List<SmsByYearMonthDay> groupSmsClientByYeMoDaWhYeMoDaSyIn(@Param("yearSms") int yearSms,
                                                               @Param("monthSms") int monthSms,
                                                               @Param("daySms") int daySms,
                                                               @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "SUM(h.total), h.year, h.month) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month "
            + "ORDER BY  h.year, h.month ")
    List<SmsByYearMonth> groupSmsByYeMoWhYeMoSyIn(@Param("yearSms") int yearSms,
                                                  @Param("monthSms") int monthSms,
                                                  @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "SUM(h.total), h.year, h.month) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month "
            + "ORDER BY  h.year, h.month ")
    List<SmsByYearMonth> groupSmsByYeMoWhYeTySyIn(@Param("yearSms") int yearSms,
                                                  @Param("messageType") String messageType,
                                                  @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH WHERE: YEAR, LIST-SID
     *
     * @param yearSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "SUM(h.total), h.year, h.month) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month "
            + "ORDER BY h.year, h.month")
    List<SmsByYearMonth> groupSmsByYeMoWhYeSyIn(@Param("yearSms") int yearSms,
                                                @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY WHERE: YEAR, MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day "
            + "ORDER BY  h.year, h.month, h.day ")
    List<SmsByYearMonthDay> groupSmsByYeMoDaWhYeMoSyIn(@Param("yearSms") int yearSms,
                                                       @Param("monthSms") int monthSms,
                                                       @Param("listSid") List<String> listSid);


    /**
     * GROUP: YEAR, MONTH, DAY WHERE: YEAR, MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageType
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day "
            + "ORDER BY  h.year, h.month, h.day ")
    List<SmsByYearMonthDay> groupSmsByYeMoDaWhYeMoTySyIn(@Param("yearSms") int yearSms,
                                                         @Param("monthSms") int monthSms,
                                                         @Param("messageType") String messageType,
                                                         @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY, HOUR WHERE: YEAR, MONTH, DAY, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour "
            + "ORDER BY  h.year, h.month, h.day, h.hour ")
    List<SmsByYearMonthDayHour> groupSmsByYeMoDaHoWhYeMoDaSyIn(@Param("yearSms") int yearSms,
                                                               @Param("monthSms") int monthSms,
                                                               @Param("daySms") int daySms,
                                                               @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY, HOUR WHERE: YEAR, MONTH, DAY, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageType
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour "
            + "ORDER BY  h.year, h.month, h.day, h.hour ")
    List<SmsByYearMonthDayHour> groupSmsByYeMoDaHoWhYeMoDaTySyIn(@Param("yearSms") int yearSms,
                                                                 @Param("monthSms") int monthSms,
                                                                 @Param("daySms") int daySms,
                                                                 @Param("messageType") String messageType,
                                                                 @Param("listSid") List<String> listSid);

    /**
     * GROUP: POR YEAR, MONTH, MESSAGETYPE WHERE: YEAR, MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.messageType "
            + "ORDER BY  h.year, h.month, h.messageType")
    List<SmsByYearMonth> groupSmsMessageTypeByYeMoWhYeMoSyIn(@Param("yearSms") int yearSms,
                                                             @Param("monthSms") int monthSms,
                                                             @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, MESSAGETYPE WHERE: YEAR, LIST-MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.messageType "
            + "ORDER BY h.year, h.month, h.messageType")
    List<SmsByYearMonth> groupSmsMessageTypeByYeMoWhYeMoInSyIn(@Param("yearSms") int yearSms,
                                                               @Param("monthSms") List<Integer> monthSms,
                                                               @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.messageType "
            + "ORDER BY h.year, h.month, h.messageType")
    List<SmsByYearMonth> groupSmsMessageTypeByYeMoWhYeMoInSyIn(@Param("yearSms") int yearSms,
                                                               @Param("monthSms") Integer monthSms,
                                                               @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, CARRIER WHERE: YEAR, LIST-MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.carrierCharCode ")
    List<SmsByYearMonth> groupSmsCarrierByYeMoWhYeMoInSyIn(@Param("yearSms") int yearSms,
                                                           @Param("monthSms") List<Integer> monthSms,
                                                           @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.systemId, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month =:monthSms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.systemId, h.messageType "
            + "ORDER BY h.year, h.month, h.systemId, h.messageType ")
    List<SmsByYearMonth> groupSmsSyTyByYeMoWhYeMoSyInTyIn(@Param("yearSms") int yearSms,
                                                          @Param("monthSms") Integer monthSms,
                                                          @Param("messageTypeSms") List<String> messageTypeSms,
                                                          @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.systemId, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.systemId, h.messageType "
            + "ORDER BY h.year, h.month, h.systemId, h.messageType ")
    List<SmsByYearMonth> groupSmsSyTyByYeMoWhYeMoInSyInTyIn(@Param("yearSms") int yearSms,
                                                            @Param("monthSms") List<Integer> monthSms,
                                                            @Param("messageTypeSms") List<String> messageTypeSms,
                                                            @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, CARRIER WHERE: YEAR, LIST-MONTH, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.carrierCharCode, h.messageType ) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.carrierCharCode, h.messageType  "
            + "ORDER BY h.year, h.month, h.carrierCharCode, h.messageType  ")
    List<SmsByYearMonth> groupSmsCarrierTyByYeMoWhYeMoSyIn_TyIn(@Param("yearSms") int yearSms,
                                                                @Param("monthSms") List<Integer> monthSms,
                                                                @Param("messageTypeSms") List<String> messageTypeSms,
                                                                @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, CARRIER WHERE: YEAR, LIST-MONTH, SET-CARRIER, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.carrierCharCode, h.messageType ) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.carrierCharCode IN (:carrierList) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.carrierCharCode, h.messageType  "
            + "ORDER BY h.year, h.month, h.carrierCharCode, h.messageType  ")
    List<SmsByYearMonth> groupSmsCarrierAndMessageTypeByYeMoWhYeMoInSyIn_CarrierInTyIn(@Param("yearSms") int yearSms,
                                                                                       @Param("monthSms") List<Integer> monthSms,
                                                                                       @Param("carrierList") List<String> carrierList,
                                                                                       @Param("messageTypeSms") List<String> messageTypeSms,
                                                                                       @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.carrierCharCode, h.messageType ) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.carrierCharCode IN (:carrierList) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.carrierCharCode, h.messageType  "
            + "ORDER BY h.year, h.month, h.carrierCharCode, h.messageType  ")
    List<SmsByYearMonth> groupSmsCarrierAndMessageTypeByYeMoWhYeMoSyIn_CarrierInTyIn(@Param("yearSms") int yearSms,
                                                                                     @Param("monthSms") int monthSms,
                                                                                     @Param("carrierList") List<String> carrierList,
                                                                                     @Param("messageTypeSms") List<String> messageTypeSms,
                                                                                     @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "sum(h.total), h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType ) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.carrierCharCode IN (:carrierList) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType  "
            + "ORDER BY h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType  ")
    List<SmsByYearMonthDayHour> groupSmsCarrierAndMessageTypeByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(@Param("yearSms") int yearSms,
                                                                                                @Param("monthSms") int monthSms,
                                                                                                @Param("daySms") int daySms,
                                                                                                @Param("carrierList") List<String> carrierList,
                                                                                                @Param("messageTypeSms") List<String> messageTypeSms,
                                                                                                @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "sum(h.total), h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType ) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.hour = :hourSms AND "
            + "h.carrierCharCode IN (:carrierList) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType  "
            + "ORDER BY h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType  ")
    List<SmsByYearMonthDay> groupSmsCarrierAndMessageTypeByYeMoDaHoWhYeMoDaHoSyIn_CarrierInTyIn(@Param("yearSms") int yearSms,
                                                                                                @Param("monthSms") int monthSms,
                                                                                                @Param("daySms") int daySms,
                                                                                                @Param("hourSms") int hourSms,
                                                                                                @Param("carrierList") List<String> carrierList,
                                                                                                @Param("messageTypeSms") List<String> messageTypeSms,
                                                                                                @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, CARRIER, MESSAGE TYPE WHERE: YEAR, MONTH, LIST-SIDS,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.carrierCharCode, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.carrierCharCode, h.messageType "
            + "ORDER BY h.year, h.month, h.carrierCharCode,h.messageType ")
    List<SmsByYearMonth> groupSmsCarrierAndMessageTypeByYeMoWhYeMoSyIn_TyIn(@Param("yearSms") int yearSms,
                                                                            @Param("monthSms") int monthSms,
                                                                            @Param("messageTypeSms") List<String> messageTypeSms,
                                                                            @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, CARRIER WHERE: YEAR, LIST-MONTH, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.systemId "
            + "ORDER BY h.year, h.month, h.systemId ")
    List<SmsByYearMonth> groupSystemIdByYeMoWhMoInMessageTypeIn(@Param("yearSms") int yearSms,
                                                                @Param("monthSms") List<Integer> monthSms,
                                                                @Param("messageTypeSms") List<String> messageTypeSms,
                                                                @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, SYSTEMID, MESSAGE TYPE WHERE: YEAR, LIST-MONTH, LIST-MESSAGETYPE,
     * LIST-SID
     * Se usa en los graficos de Los Ultimos tres meses.
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.systemId, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.systemId, h.messageType "
            + "ORDER BY h.year, h.month, h.systemId, h.messageType ")
    List<SmsByYearMonth> groupYeMoSyTyWhYeMoInSyInTyIn(@Param("yearSms") int yearSms,
                                                       @Param("monthSms") List<Integer> monthSms,
                                                       @Param("messageTypeSms") List<String> messageTypeSms,
                                                       @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, CARRIER WHERE: YEAR, LIST-MONTH, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "sum(h.total), h.year, h.month, h.systemId, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month IN (:monthSms) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.systemId, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.systemId,h.carrierCharCode ")
    List<SmsByYearMonth> groupSystemIdByYeMoCaWhMoInMessageTypeIn(@Param("yearSms") int yearSms,
                                                                  @Param("monthSms") List<Integer> monthSms,
                                                                  @Param("messageTypeSms") List<String> messageTypeSms,
                                                                  @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "sum(h.total), h.year, h.month, h.day, h.hour, h.systemId, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day =:daySms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour, h.systemId, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.hour, h.systemId,h.carrierCharCode ")
    List<SmsByYearMonthDayHour> groupSystemIdByYeMoDaHoCaWhMoEqDaEqMessageTypeIn(@Param("yearSms") int yearSms,
                                                                                 @Param("monthSms") Integer monthSms,
                                                                                 @Param("daySms") Integer daySms,
                                                                                 @Param("messageTypeSms") List<String> messageTypeSms,
                                                                                 @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY, CARRIER WHERE: YEAR, MONTH, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.carrierCharCode")
    List<SmsByYearMonthDay> groupCarrierByYeMoDa(@Param("yearSms") int yearSms,
                                                 @Param("monthSms") int monthSms,
                                                 @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY, CARRIER WHERE: YEAR, MONTH, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.carrierCharCode, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.carrierCharCode, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.carrierCharCode, h.messageType ")
    List<SmsByYearMonthDay> groupCarrierByYeMoDaWhMessageTypeIn(@Param("yearSms") int yearSms,
                                                                @Param("monthSms") int monthSms,
                                                                @Param("messageTypeSms") List<String> messageTypeSms,
                                                                @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY, CARRIER WHERE: YEAR, MONTH, CARRIER-IN, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.carrierCharCode, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.carrierCharCode IN (:carrierList) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.carrierCharCode, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.carrierCharCode, h.messageType ")
    List<SmsByYearMonthDay> groupSmsCarrierMessageTypeByYeMoDaWhYeMoSyIn_CarrierTyIn(@Param("yearSms") int yearSms,
                                                                                     @Param("monthSms") int monthSms,
                                                                                     @Param("carrierList") Collection<String> carrier,
                                                                                     @Param("messageTypeSms") List<String> messageTypeSms,
                                                                                     @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY, CARRIER WHERE: YEAR, MONTH, LIST-MESSAGETYPE,
     * LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.systemId, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.systemId, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.systemId, h.messageType")
    List<SmsByYearMonthDay> groupSmsByYeMoDaSyWhYeMoSyIn_TyIn(@Param("yearSms") int yearSms,
                                                              @Param("monthSms") int monthSms,
                                                              @Param("messageTypeSms") List<String> messageTypeSms,
                                                              @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.systemId, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.systemId, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.systemId, h.messageType")
    List<SmsByYearMonthDay> groupSmsByYeMoDaSyWhYeMoDaSyIn_TyIn(@Param("yearSms") int yearSms,
                                                                @Param("monthSms") int monthSms,
                                                                @Param("daySms") int daySms,
                                                                @Param("messageTypeSms") List<String> messageTypeSms,
                                                                @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY CARRIER WHERE: YEAR, MONTH, DAY,
     * LIST-MESSAGETYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.carrierCharCode")
    List<SmsByYearMonthDay> groupSmsCarrierByYeMoDaWhYeMoDaSyIn_TyIn(@Param("yearSms") int yearSms,
                                                                     @Param("monthSms") int monthSms,
                                                                     @Param("daySms") int daySms,
                                                                     @Param("messageType") List<String> messageTypeSms,
                                                                     @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY CARRIER WHERE: YEAR, MONTH, DAY,
     * LIST-MESSAGETYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.systemId "
            + "ORDER BY h.year, h.month, h.day, h.systemId")
    List<SmsByYearMonthDay> groupSystemIdByYeMoDaWhYeMoDaEqMessageTypeIn(@Param("yearSms") int yearSms,
                                                                         @Param("monthSms") int monthSms,
                                                                         @Param("daySms") int daySms,
                                                                         @Param("messageTypeSms") List<String> messageTypeSms,
                                                                         @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY, HOUR, CARRIER WHERE: YEAR, MONTH, DAY,
     * LIST-MESSAGETYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType")
    List<SmsByYearMonthDayHour> groupSmsCarrierTyByYeMoDaHoWhYeMoDaSyIn_TyIn(@Param("yearSms") int yearSms,
                                                                             @Param("monthSms") int monthSms,
                                                                             @Param("daySms") int daySms,
                                                                             @Param("messageTypeSms") Collection<String> messageTypeSms,
                                                                             @Param("listSid") List<String> listSid);


    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.carrierCharCode, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageType) AND "
            + "h.carrierCharCode IN (:carrierCharCode) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.carrierCharCode, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.carrierCharCode, h.messageType")
    List<SmsByYearMonthDay> groupSmsCarrierTyByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(@Param("yearSms") int yearSms,
                                                                                @Param("monthSms") int monthSms,
                                                                                @Param("daySms") int daySms,
                                                                                @Param("messageType") List<String> messageType,
                                                                                @Param("carrierCharCode") List<String> carrierCharCode,
                                                                                @Param("listSid") List<String> listSid);


    /**
     * GROUP: YEAR, MONTH, DAY, HOUR, CARRIER WHERE: YEAR, MONTH, DAY,
     * LIST - CARRIER, LIST-MESSAGETYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.carrierCharCode IN (:carrierList) AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.hour, h.carrierCharCode, h.messageType")
    List<SmsByYearMonthDayHour> groupSmsCarrierAndMessageTypeByYeMoDaHoWhYeMoDaSyIn_CarrierTyIn(@Param("yearSms") int yearSms,
                                                                                                @Param("monthSms") int monthSms,
                                                                                                @Param("daySms") int daySms,
                                                                                                @Param("carrierList") Collection<String> carrierList,
                                                                                                @Param("messageTypeSms") Collection<String> messageTypeSms,
                                                                                                @Param("listSid") Collection<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY, HOUR, SYSTEMID WHERE: YEAR, MONTH, DAY,
     * LIST-MESSAGETYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param messageTypeSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageTypeSms) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.hour, h.systemId "
            + "ORDER BY h.year, h.month, h.day, h.hour, h.systemId")
    List<SmsByYearMonthDayHour> groupSystemIdByYeMoDaHoWhMessageTypeIn(@Param("yearSms") int yearSms,
                                                                       @Param("monthSms") int monthSms,
                                                                       @Param("daySms") int daySms,
                                                                       @Param("messageTypeSms") List<String> messageTypeSms,
                                                                       @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, CARRIER WHERE: YEAR, MONTH, MESSAGE TYPE, LIST-SID
     *
     * @param yearSms
     * @param monthSms
     * @param messageType a buscar
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonth("
            + "SUM(h.total), h.year, h.month, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.carrierCharCode")
    List<SmsByYearMonth> groupCarrierByYeMo(@Param("yearSms") int yearSms,
                                            @Param("monthSms") int monthSms,
                                            @Param("messageType") String messageType,
                                            @Param("listSid") List<String> listSid);

    /**
     * GROUP: YEAR, MONTH, DAY, CARRIER WHERE: YEAR, MONTH, MESSAGE TYPE,
     * LIST-SID.
     *
     * @param yearSms
     * @param monthSms
     * @param messageType a buscar
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.carrierCharCode")
    List<SmsByYearMonthDay> groupCarrierByYeMoDa(@Param("yearSms") int yearSms,
                                                 @Param("monthSms") int monthSms,
                                                 @Param("messageType") String messageType,
                                                 @Param("listSid") List<String> listSid);

    /**
     * AGRUPACION SYSTEMID DEL DIA: Por YEAR, MONTH, DAY, SYSTEMID, MESSAGE_TYPE. WHERE
     * MESSAGETYPE
     *
     * @param yearSms
     * @param monthSms
     * @param messageType a buscar
     * @param listSid
     * @return EXACTAMENTE IGUAL A : groupSystemIdByYearMonthDay
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.systemId, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.messageType IN (:messageType) AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.systemId, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.systemId, h.messageType")
    List<SmsByYearMonthDay> groupSmsMessageTypeByYeMoDaSyWhYeMoDaSyIn_TyIn(@Param("yearSms") int yearSms,
                                                                           @Param("monthSms") int monthSms,
                                                                           @Param("daySms") int daySms,
                                                                           @Param("messageType") List<String> messageType,
                                                                           @Param("listSid") List<String> listSid);

    /**
     * AGRUPACION SYSTEMID MENSUAL: Por YEAR, MONTH, DAY, SYSTEMID. WHERE
     * MESSAGETYPE
     *
     * @param yearSms
     * @param monthSms
     * @param messageType a buscar
     * @param listSid
     * @return EXACTAMENTE IGUAL A : groupSystemIdByYearMonthDay
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.systemId "
            + "ORDER BY h.year, h.month, h.day, h.systemId")
    List<SmsByYearMonthDay> groupSystemIdByYeMoDa(@Param("yearSms") int yearSms,
                                                  @Param("monthSms") int monthSms,
                                                  @Param("messageType") String messageType,
                                                  @Param("listSid") List<String> listSid);

    /**
     * AGRUPACION: YEAR + MONTH + DAY + HOUR + CARRIER CONDICION: SIDS +
     * MESSAGE_TYPE
     *
     * @param yearSms
     * @param monthSms
     * @param messageType a buscar
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.carrierCharCode) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY h.year, h.month, h.day, h.hour, h.carrierCharCode "
            + "ORDER BY h.year, h.month, h.day, h.hour, h.carrierCharCode")
    List<SmsByYearMonthDayHour> groupCarrierByYeMoDaHo(@Param("yearSms") int yearSms,
                                                       @Param("monthSms") int monthSms,
                                                       @Param("messageType") String messageType,
                                                       @Param("listSid") List<String> listSid);

    /**
     * GRUPO POR YEAR, MONTH, DAY, MESSAGETYPE
     *
     * @param yearSms
     * @param monthSms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.messageType")
    List<SmsByYearMonthDay> groupSmsMessageTypeByYeMoDaWhYeMoSyIn(@Param("yearSms") int yearSms,
                                                                  @Param("monthSms") int monthSms,
                                                                  @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.messageType "
            + "ORDER BY h.year, h.month, h.day, h.messageType")
    List<SmsByYearMonthDay> groupSmsMessageTypeByYeMoDaWhYeMoDaSyIn(@Param("yearSms") int yearSms,
                                                                    @Param("monthSms") int monthSms,
                                                                    @Param("daySms") int daySms,
                                                                    @Param("listSid") List<String> listSid);

    /**
     * WHERE : YEAR, MONTH, DAY AND SYSTEMID LIST
     * GRUPO : POR YEAR, MONTH, DAY, HOUR, MESSAGETYPE
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE  h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour, h.messageType "
            + "ORDER BY  h.year, h.month, h.day, h.hour, h.messageType")
    List<SmsByYearMonthDayHour> groupSmsMessageTypeByYeMoDaHoWhYeMoDaSyIn(@Param("yearSms") int yearSms,
                                                                          @Param("monthSms") int monthSms,
                                                                          @Param("daySms") int daySms,
                                                                          @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.systemId, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE  h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour, h.systemId, h.messageType "
            + "ORDER BY  h.year, h.month, h.day, h.hour, h.systemId, h.messageType")
    List<SmsByYearMonthDayHour> groupSmsSystemidMessageTypeByYeMoDaHoWhYeMoDaSyIn(@Param("yearSms") int yearSms,
                                                                                  @Param("monthSms") int monthSms,
                                                                                  @Param("daySms") int daySms,
                                                                                  @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE  h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.hour = :hourSms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour, h.messageType "
            + "ORDER BY  h.year, h.month, h.day, h.hour, h.messageType")
    List<SmsByYearMonthDayHour> groupSmsMessageTypeByYeMoDaHoWhYeMoDaHoSyIn(@Param("yearSms") int yearSms,
                                                                            @Param("monthSms") int monthSms,
                                                                            @Param("daySms") int daySms,
                                                                            @Param("hourSms") int hourSms,
                                                                            @Param("listSid") List<String> listSid);

    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDayHour("
            + "SUM(h.total), h.year, h.month, h.day, h.hour, h.systemId, h.messageType) "
            + "FROM  SmsHour h "
            + "WHERE  h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.day = :daySms AND "
            + "h.hour = :hourSms AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.hour, h.systemId, h.messageType "
            + "ORDER BY  h.year, h.month, h.day, h.hour, h.systemId, h.messageType")
    List<SmsByYearMonthDayHour> groupSmsSystemidMessageTypeByYeMoDaHoWhYeMoDaHoSyIn(@Param("yearSms") int yearSms,
                                                                                    @Param("monthSms") int monthSms,
                                                                                    @Param("daySms") int daySms,
                                                                                    @Param("hourSms") int hourSms,
                                                                                    @Param("listSid") List<String> listSid);

    /**
     * GRUPO POR YEAR, MONTH, DAY, SYSTEMID
     *
     * @param yearSms
     * @param monthSms
     * @param messageType
     * @param listSid
     * @return
     */
    @Query("SELECT  new com.stt.dash.backend.data.SmsByYearMonthDay("
            + "SUM(h.total), h.year, h.month, h.day, h.systemId) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month, h.day, h.systemId "
            + "ORDER BY h.year, h.month, h.day, h.systemId")
    List<SmsByYearMonthDay> groupSystemIdByYearMonthDay(@Param("yearSms") int yearSms,
                                                        @Param("monthSms") int monthSms,
                                                        @Param("messageType") String messageType,
                                                        @Param("listSid") List<String> listSid);

    /**
     * **** TOTAL ********
     */
    /**
     * TOTAL DE UN TIPO DE MENSAJE. POR MES
     *
     * @param yearSms
     * @param monthSms
     * @param listSid
     * @param messageType
     * @return
     */
    @Query("SELECT COUNT(h) "
            + "FROM  SmsHour h "
            + "WHERE h.year = :yearSms AND "
            + "h.month = :monthSms AND "
            + "h.messageType = :messageType AND "
            + "h.systemId IN (:listSid) "
            + "GROUP BY  h.year, h.month "
            + "ORDER BY  h.year, h.month ")
    Long totalMessageTypeByYearMonth(@Param("yearSms") int yearSms,
                                     @Param("monthSms") int monthSms,
                                     @Param("listSid") List<String> listSid,
                                     @Param("messageType") String messageType);
}
