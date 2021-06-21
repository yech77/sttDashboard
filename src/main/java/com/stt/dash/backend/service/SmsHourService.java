package com.stt.dash.backend.service;

import com.stt.dash.app.OMessageType;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.data.entity.SmsHour;
import com.stt.dash.backend.data.entity.User;
import com.stt.dash.backend.repositories.SmsHourRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmsHourService {
    private final SmsHourRepository smshour_repo;

    public SmsHourService(SmsHourRepository smshour_repo) {
        this.smshour_repo = smshour_repo;
    }

    private static final Logger log = LoggerFactory.getLogger(SmsHourService.class.getName());
//    private SmsHourRepository smshour_repo;
//    private MyAuditEventComponent my;

//    public SmsHourService(SmsHourRepository smshour_repo, MyAuditEventComponent my) {
//        this.my=my;
//        this.smshour_repo = smshour_repo;
//    }

    public List<SmsByYearMonth> getClientGroupByYearMonth(int yearSms, int monthSms, List<String> list_sid) {
//        my.add("ClientGroupByYearMonth", yearSms, monthSms, list_sid);
        return smshour_repo.groupClientByYeMoWhereYeMo(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonthDay> getClientGroupByYearMonthDay(int yearSms, int monthSms, int daySms, List<String> list_sid) {
        return smshour_repo.groupClientByYeMoDaWhereYeMoDa(yearSms, monthSms, daySms, list_sid);
    }

    public List<SmsByYearMonth> getGroupByYearMonth(int yearSms, String messageType, List<String> list_sid) {
        return smshour_repo.groupByYeMoWhereYeType(yearSms, messageType, list_sid);
    }

    public List<SmsByYearMonth> getGroupByYearMonth(int yearSms, int monthSms, List<String> list_sid) {
//        my.add("getGroupByYearMonth", yearSms, monthSms, list_sid);
        return smshour_repo.groupByYeMoWhereYeMo(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupByYearMonthDay(int yearSms, int monthSms, String messageType, List<String> list_sid) {
        return smshour_repo.groupByYeMoDaWhereYeMoType(yearSms, monthSms, messageType, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupByYearMonthDay(int yearSms, int monthSms, List<String> list_sid) {
//        my.add("getGroupByYearMonthDay", yearSms, monthSms, list_sid);
        return smshour_repo.groupByYeMoDaWhereYeMo(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupByYearMonthDayHour(int yearSms, int monthSms, int daySms, String messageType, List<String> list_sid) {
        return smshour_repo.groupByYeMoDaHoWhereYeMoDaType(yearSms, monthSms, daySms, messageType, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupByYearMonthDayHour(int yearSms, int monthSms, int daySms, List<String> list_sid) {
        List<SmsByYearMonthDayHour> hourList = smshour_repo.groupByYeMoDaHoWhereYeMoDa(yearSms, monthSms, daySms, list_sid);
        Calendar c = Calendar.getInstance();

        /* Completar HOUR faltantes con 0, hasta lal hora actual */
        for (int hourRunner = 0; hourRunner <= c.get(Calendar.HOUR_OF_DAY); hourRunner++) {
            boolean thisHasIt = false;
            for (SmsByYearMonthDay smsByYearMonth : hourList) {
                /* Si tengo el Hour me salgo del ciclo. */
                if (smsByYearMonth.getGroupBy() == hourRunner) {
                    thisHasIt = true;
                    break;
                }
            }
            /* Agregar el HOUR faltante a la respuesta. */
            if (!thisHasIt) {
                log.info("HOUR COLUMN DATA - ADDING HOUR({}) WITH 0 ", hourRunner);
                SmsByYearMonthDayHour o = new SmsByYearMonthDayHour(0, yearSms, monthSms, daySms, hourRunner, "N/A");
                hourList.add(o);
            }
        }
        return hourList;
    }

    public List<SmsByYearMonth> getGroupByYearMonth(int yearSms, List<String> list_sid) {
//        return smshour_repo.groupByYeMoWhereYe(yearSms, list_sid);

        List<SmsByYearMonth> monthlyList = smshour_repo.groupByYeMoWhereYe(yearSms, list_sid);
//        Calendar c = Calendar.getInstance();
//        /* Completar MONTH faltantes con 0 */
//        for (int monthRunner = 1; monthRunner <= c.get(Calendar.MONTH + 1); monthRunner++) {
//            boolean thisHasIt = false;
//            for (SmsByYearMonth smsByYearMonth : monthlyList) {
//                /* Si tengo el MONTH me salgo del ciclo. */
//                if (smsByYearMonth.getGroupBy() == monthRunner) {
//                    thisHasIt = true;
//                    break;
//                }
//            }
//            /* Agregar el HOUR faltante a la respuesta. */
//            if (!thisHasIt) {
//                log.info("MONTH COLUMN DATA - ADDING MONTH({}) WITH 0 ", monthRunner);
//                SmsByYearMonth o = new SmsByYearMonthDayHour(0, 2021, c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), monthRunner, "N/A");
//                monthlyList.add(o);
//            }
//        }
        return monthlyList;
    }

    public List<SmsByYearMonth> getGroupCarrierByYeMoWhMoIn(int yearSms, List<Integer> monthSms, List<String> list_sid) {
        return smshour_repo.groupCarrierByYeMoWhMoIn(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonth> getGroupCarrierByYeMoWhMoInMessageTypeIn(int yearSms, List<Integer> monthSms, List<String> messageTypeSms, List<String> list_sid) {
        return smshour_repo.groupCarrierByYeMoWhMoInMessageTypeIn(yearSms, monthSms, messageTypeSms, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupCarrierByYeMoDaWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, List<String> messageTypeSms, List<String> list_sid) {
        return smshour_repo.groupCarrierByYeMoDaWhYeMoDaEqMessageTypeIn(yearSms, monthSms, daySms, messageTypeSms, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupCarrierByYeMoDaWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = new ArrayList<>(messageTypeSms.size());
        for (OMessageType messageTypeSm : messageTypeSms) {
            l.add(messageTypeSm.name());
        }
        return smshour_repo.groupCarrierByYeMoDaWhYeMoDaEqMessageTypeIn(yearSms, monthSms, daySms, l, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupSystemIdByYeMoDaWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = new ArrayList<>(messageTypeSms.size());
        for (OMessageType messageTypeSm : messageTypeSms) {
            l.add(messageTypeSm.name());
        }
        return smshour_repo.groupSystemIdByYeMoDaWhYeMoDaEqMessageTypeIn(yearSms, monthSms, daySms, l, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupCarrierByYeMoDaHoWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, List<String> messageTypeSms, List<String> list_sid) {
        return smshour_repo.groupCarrierByYeMoDaHoWhMessageTypeIn(yearSms, monthSms, daySms, messageTypeSms, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupCarrierByYeMoDaHoWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = new ArrayList<>(messageTypeSms.size());
        for (OMessageType messageTypeSm : messageTypeSms) {
            l.add(messageTypeSm.name());
        }
        return smshour_repo.groupCarrierByYeMoDaHoWhMessageTypeIn(yearSms, monthSms, daySms, l, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupSystemIdByYeMoDaHoWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = new ArrayList<>(messageTypeSms.size());
        for (OMessageType messageTypeSm : messageTypeSms) {
            l.add(messageTypeSm.name());
        }
        return smshour_repo.groupSystemIdByYeMoDaHoWhMessageTypeIn(yearSms, monthSms, daySms, l, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupSystemIdByYeMoDaHoCaWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = new ArrayList<>(messageTypeSms.size());
        for (OMessageType messageTypeSm : messageTypeSms) {
            l.add(messageTypeSm.name());
        }
        return smshour_repo.groupSystemIdByYeMoDaHoCaWhMoEqDaEqMessageTypeIn(yearSms, monthSms, daySms, l, list_sid);
    }

    /**
     * Total por: year, mes y operadora.
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param list_sid
     * @return
     */
    public List<SmsByYearMonth> getGroupCarrierByYeMoWhMoInMessageTypeIn(int yearSms, List<Integer> monthSms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> messagesType = new ArrayList<>(messageTypeSms.size());
        for (OMessageType messageTypeSm : messageTypeSms) {
            messagesType.add(messageTypeSm.name());
        }
        log.info("Preparing Searching: year[{}] months[{}] Message Type [{}] sids[{}]",
                yearSms, monthSms, messageTypeSms, list_sid);
        List<SmsByYearMonth> yearBefore = null;
        List<Integer> monthsOfPrevYear = findMonthsOfPreviousYear(monthSms);

        if (monthsOfPrevYear != null) {
            log.info("Month ({}) before actual year detected. Searching year[{}] month[{}] Message Type [{}] sid[{}]",
                    monthsOfPrevYear, yearSms - 1, monthsOfPrevYear, messageTypeSms, list_sid);
            yearBefore = smshour_repo.groupCarrierByYeMoWhMoInMessageTypeIn(yearSms - 1, monthsOfPrevYear, messagesType, list_sid);
        }
        if (yearBefore == null) {
            yearBefore = new ArrayList<>();
        }
        log.info("Searching: year[{}] months[{}] message type[{}] sids[{}]", yearSms, monthSms, messagesType, list_sid);
        yearBefore.addAll(smshour_repo.groupCarrierByYeMoWhMoInMessageTypeIn(yearSms, monthSms, messagesType, list_sid));
        return yearBefore;
    }

    /**
     * Total por: year, mes y operadora.
     *
     * @param yearSms
     * @param monthSms
     * @param messageTypeSms
     * @param list_sid
     * @return
     */
    public List<SmsByYearMonth> getGroupCarrierByYeMoWhMoInMessageTypeIn(int yearSms, List<Integer> monthSms, Set<Carrier> carrierSet, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> messagesTypeList = messageTypeSms.stream().map(OMessageType::name).collect(Collectors.toList());
        List<String> carrierList = carrierSet.stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
        log.info("Preparing Searching: year[{}] months[{}] Message Type [{}] sids[{}]",
                yearSms, monthSms, messageTypeSms, list_sid);
        List<SmsByYearMonth> yearBefore = null;
        List<Integer> monthsOfPrevYear = findMonthsOfPreviousYear(monthSms);

        if (monthsOfPrevYear != null) {
            log.info("Month ({}) before actual year detected. Searching year[{}] month[{}] Message Type [{}] sid[{}]",
                    monthsOfPrevYear, yearSms - 1, monthsOfPrevYear, messageTypeSms, list_sid);
            yearBefore = smshour_repo.groupCarrierByYeMoWhMoInMessageTypeIn(yearSms - 1, monthsOfPrevYear, carrierList, messagesTypeList, list_sid);
        }
        if (yearBefore == null) {
            yearBefore = new ArrayList<>();
        }
        log.info("Searching: year[{}] months[{}] carrier [{}] message type[{}] sids[{}]", yearSms, monthSms, carrierList, messagesTypeList, list_sid);
        yearBefore.addAll(smshour_repo.groupCarrierByYeMoWhMoInMessageTypeIn(yearSms, monthSms, carrierList, messagesTypeList, list_sid));
        return yearBefore;
    }

    ////////////////
    public List<SmsByYearMonth> getGroupSystemIdByYeMoWhMoInMessageTypeIn(int yearSms, List<Integer> monthSms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> messagesType = new ArrayList<>(messageTypeSms.size());
        for (OMessageType messageTypeSm : messageTypeSms) {
            messagesType.add(messageTypeSm.name());
        }
        //return smshour_repo.groupSystemIdByYeMoWhMoInMessageTypeIn(yearSms, monthSms, l, list_sid);

        log.info("Preparing Searching: year[{}] months[{}] Message Type [{}] sids[{}]",
                yearSms, monthSms, messageTypeSms, list_sid);
        List<SmsByYearMonth> yearBefore = null;
        List<Integer> monthsOfPrevYear = findMonthsOfPreviousYear(monthSms);

        if (monthsOfPrevYear != null) {
            log.info("Month ({}) before actual year detected. Searching year[{}] month[{}] Message Type [{}] sid[{}]",
                    monthsOfPrevYear, yearSms - 1, monthsOfPrevYear, messageTypeSms, list_sid);
            yearBefore = smshour_repo.groupSystemIdByYeMoWhMoInMessageTypeIn(yearSms - 1, monthsOfPrevYear, messagesType, list_sid);
        }
        if (yearBefore == null) {
            yearBefore = new ArrayList<>();
        }
        log.info("Searching: year[{}] months[{}] message type[{}] sids[{}]", yearSms, monthSms, messagesType, list_sid);
        yearBefore.addAll(smshour_repo.groupSystemIdByYeMoWhMoInMessageTypeIn(yearSms, monthSms, messagesType, list_sid));
        return yearBefore;
    }

    public List<SmsByYearMonth> getGroupSystemIdByYeMoCaWhMoInMessageTypeIn(int yearSms, List<Integer> monthSms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> messagesType = new ArrayList<>(messageTypeSms.size());
        for (OMessageType messageTypeSm : messageTypeSms) {
            messagesType.add(messageTypeSm.name());
        }
        //return smshour_repo.groupSystemIdByYeMoWhMoInMessageTypeIn(yearSms, monthSms, l, list_sid);

        log.info("Preparing Searching: year[{}] months[{}] Message Type [{}] sids[{}]",
                yearSms, monthSms, messageTypeSms, list_sid);
        List<SmsByYearMonth> yearBefore = null;
        List<Integer> monthsOfPrevYear = findMonthsOfPreviousYear(monthSms);

        if (monthsOfPrevYear != null) {
            log.info("Month ({}) before actual year detected. Searching year[{}] month[{}] Message Type [{}] sid[{}]",
                    monthsOfPrevYear, yearSms - 1, monthsOfPrevYear, messageTypeSms, list_sid);
            yearBefore = smshour_repo.groupSystemIdByYeMoCaWhMoInMessageTypeIn(yearSms - 1, monthsOfPrevYear, messagesType, list_sid);
        }
        if (yearBefore == null) {
            yearBefore = new ArrayList<>();
        }
        log.info("Searching: year[{}] months[{}] message type[{}] sids[{}]", yearSms, monthSms, messagesType, list_sid);
        yearBefore.addAll(smshour_repo.groupSystemIdByYeMoCaWhMoInMessageTypeIn(yearSms, monthSms, messagesType, list_sid));
        return yearBefore;
    }

    public List<SmsByYearMonth> getGroupCarrierByYearMonthMessageType(int yearSms, int monthSms, String messageType, List<String> list_sid) {
        return smshour_repo.groupCarrierByYeMo(yearSms, monthSms, messageType, list_sid);
    }
    public List<SmsByYearMonth> groupCarrierByYeMoMeWhMoEqMessageTypeIn(int yearSms, int monthSms, List<String> messageType, List<String> list_sid) {
        return smshour_repo.groupCarrierByYeMoMeWhMoEqMessageTypeIn(yearSms, monthSms, messageType, list_sid);
    }
    public List<SmsByYearMonthDay> getGroupCarrierByYearMonthDayMessageType(int yearSms, int monthSms, int daySms, String messageType, List<String> list_sid) {
        List<String> lis = new ArrayList<>(1);
        lis.add(messageType);
        return getGroupCarrierByYeMoDaWhYeMoDayEqMessageTypeIn(yearSms, monthSms, daySms, lis, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupCarrierByYeMoDaMessageType(int yearSms, int monthSms, String messageType, List<String> list_sid) {
        return smshour_repo.groupCarrierByYeMoDa(yearSms, monthSms, messageType, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupCarrierByYeMoDa(int yearSms, int monthSms, List<String> list_sid) {
        return smshour_repo.groupCarrierByYeMoDa(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupCarrierByYeMoDa(int yearSms, int monthSms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = messageTypeSms.stream().map(OMessageType::name).collect(Collectors.toList());
        return smshour_repo.groupCarrierByYeMoDaWhMessageTypeIn(yearSms, monthSms, l, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupCarrierByYeMoMe(int yearSms, int monthSms, Collection<String> carrier_list, Collection<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = messageTypeSms.stream().map(OMessageType::name).collect(Collectors.toList());
        return smshour_repo.groupCarrierByYeMoDaWhMessageTypeIn(yearSms, monthSms, carrier_list, l, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupSystemIdByYeMoDa(int yearSms, int monthSms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = new ArrayList<>(messageTypeSms.size());
        messageTypeSms.forEach(messageTypeSm -> {
            l.add(messageTypeSm.name());
        });
        return smshour_repo.groupSystemIdByYeMoDaWhMessageTypeIn(yearSms, monthSms, l, list_sid);
    }

    public List<SmsByYearMonth> getGroupSmsByYearMonthMessageType(int yearSms, int monthSms, List<String> list_sid) {
        return smshour_repo.groupMessageTypeByYearMonth(yearSms, monthSms, list_sid);
    }

    ////////////////////
    /**
     * Busca Year, Month In y sids INs
     *
     * @param yearSms
     * @param monthSms
     * @param list_sid
     * @return
     */
    public List<SmsByYearMonth> getGroupSmsByYearMonthMessageTypeWhMo(int yearSms, List<Integer> monthSms, List<String> list_sid) {
        log.info("Preparing Searching: year[{}] months[{}] sids[{}]", yearSms, monthSms, list_sid);
        List<SmsByYearMonth> yearBefore = null;
        List<Integer> monthsOfPrevYear = findMonthsOfPreviousYear(monthSms);
        if (monthsOfPrevYear != null) {
            log.info("Month ({}) before actual year detected. Searching year[{}] month[{}] sid[{}]",
                    monthsOfPrevYear, yearSms - 1, monthsOfPrevYear, list_sid);
            yearBefore = smshour_repo.groupMessageTypeByYeMoWhMoIn(yearSms - 1, monthsOfPrevYear, list_sid);
        }
        if (yearBefore == null) {
            yearBefore = new ArrayList<>();
        }
        log.info("Searching: year[{}] months[{}] sids[{}]", yearSms, monthSms, list_sid);
        yearBefore.addAll(smshour_repo.groupMessageTypeByYeMoWhMoIn(yearSms, monthSms, list_sid));
        return yearBefore;
    }

    ///////////////////////
    /**
     * Devuelve los meses que se deben buscar con el anio anterior y modifica
     * dejando solo los meses con busqueda del anio actual.
     *
     * @param monthSms puede ser modificado
     * @return
     */
    private List<Integer> findMonthsOfPreviousYear(List<Integer> monthSms) {
        if (monthSms.size() == 1) {
            return null;
        }
        List<Integer> l = null;
        /* TODO: Convertir a ciclo. */
        /* Si una posicion es mayor que la siguiente quiere decir que es un mes del anio anterior. */
        if (monthSms.get(0) > monthSms.get(1)) {
            l = new ArrayList<>(1);
            l.add(monthSms.get(0));
            monthSms.remove(0);
        } else if (monthSms.get(1) > monthSms.get(2)) {
            l = new ArrayList<>(2);
            l.add(monthSms.get(0));
            l.add(monthSms.get(1));
            monthSms.remove(0);
            monthSms.remove(1);
        }
        return l;
    }

    public List<SmsByYearMonthDay> getGroupSmsByYearMonthDayMessageType(int yearSms, int monthSms, List<String> list_sid) {
        return smshour_repo.groupMessageTypeByYearMonthDay(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupSmsByYearMonthDayHourMessageType(int yearSms, int monthSms, int daySms, List<String> list_sid) {
        List<SmsByYearMonthDayHour> hourList = smshour_repo.groupMessageTypeByYearMonthDayHour(yearSms, monthSms, daySms, list_sid);
        Calendar c = Calendar.getInstance();
        /* Completar HOUR faltantes con 0 */
        for (int hourRunner = 0; hourRunner <= c.get(Calendar.HOUR_OF_DAY); hourRunner++) {
            boolean thisHasIt = false;
            for (SmsByYearMonthDay smsByYearMonth : hourList) {
                /* Si tengo el Hour me salgo del ciclo. */
                if (smsByYearMonth.getGroupBy() == hourRunner) {
                    thisHasIt = true;
                    break;
                }
            }
            /* Agregar el HOUR faltante a la respuesta. */
            if (!thisHasIt) {
                log.info("HOUR COLUMN DATA - ADDING HOUR({}) WITH 0 ", hourRunner);
                SmsByYearMonthDayHour o = new SmsByYearMonthDayHour(0, 2021, c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), hourRunner, "N/A");
                hourList.add(o);
            }
        }
        return hourList;

    }

    public List<SmsByYearMonthDay> getGroupSystemIdByYeMoDaMessageType(int yearSms, int monthSms, String messageType, List<String> list_sid) {
        return smshour_repo.groupSystemIdByYeMoDa(yearSms, monthSms, messageType, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupSystemIdByYearMonthDayMessageType(int yearSms, int monthSms, String messageType, List<String> list_sid) {
        return smshour_repo.groupSystemIdByYearMonthDay(yearSms, monthSms, messageType, list_sid);
    }

    public long getTotalSmsByYearMonthMessageType(int yearSms, int monthSms,
                                                  List<String> list_sid, String message_type) {
        return smshour_repo.totalMessageTypeByYearMonth(yearSms, monthSms, list_sid, message_type);
    }
}
