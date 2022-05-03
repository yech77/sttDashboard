package com.stt.dash.backend.service;

import com.stt.dash.app.OMessageType;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.SmsByYearMonthDay;
import com.stt.dash.backend.data.SmsByYearMonthDayHour;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.repositories.SmsHourRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        return smshour_repo.groupSmsClientByYeMoWhYeMoSyIn(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonthDay> getClientGroupByYearMonthDay(int yearSms, int monthSms, int daySms, List<String> list_sid) {
        return smshour_repo.groupSmsClientByYeMoDaWhYeMoDaSyIn(yearSms, monthSms, daySms, list_sid);
    }

    public List<SmsByYearMonth> getGroupByYearMonth(int yearSms, String messageType, List<String> list_sid) {
        return smshour_repo.groupSmsByYeMoWhYeTySyIn(yearSms, messageType, list_sid);
    }

    public List<SmsByYearMonth> getGroupByYearMonth(int yearSms, int monthSms, List<String> list_sid) {
//        my.add("getGroupByYearMonth", yearSms, monthSms, list_sid);
        return smshour_repo.groupSmsByYeMoWhYeMoSyIn(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupByYearMonthDay(int yearSms, int monthSms, String messageType, List<String> list_sid) {
        return smshour_repo.groupSmsByYeMoDaWhYeMoTySyIn(yearSms, monthSms, messageType, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupByYearMonthDay(int yearSms, int monthSms, List<String> list_sid) {
//        my.add("getGroupByYearMonthDay", yearSms, monthSms, list_sid);
        return smshour_repo.groupSmsByYeMoDaWhYeMoSyIn(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupByYearMonthDayHour(int yearSms, int monthSms, int daySms, String messageType, List<String> list_sid) {
        return smshour_repo.groupSmsByYeMoDaHoWhYeMoDaTySyIn(yearSms, monthSms, daySms, messageType, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupByYearMonthDayHour(int yearSms, int monthSms, int daySms, List<String> list_sid) {
        List<SmsByYearMonthDayHour> hourList = smshour_repo.groupSmsByYeMoDaHoWhYeMoDaSyIn(yearSms, monthSms, daySms, list_sid);
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

        List<SmsByYearMonth> monthlyList = smshour_repo.groupSmsByYeMoWhYeSyIn(yearSms, list_sid);
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
        return smshour_repo.groupSmsCarrierByYeMoWhYeMoInSyIn(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonth> groupCarrierByYeMoWhMoInMessageTypeIn(int yearSms, List<Integer> monthSms, List<String> messageTypeSms, List<String> list_sid) {
        return smshour_repo.groupSmsCarrierTyByYeMoWhYeMoSyIn_TyIn(yearSms, monthSms, messageTypeSms, list_sid);
    }

    public List<SmsByYearMonthDay> groupSmsCarrierByYeMoDaWhYeMoDaSyIn_TyIn(int yearSms, int monthSms, int daySms, List<String> messageTypeSms, List<String> list_sid) {
        return smshour_repo.groupSmsCarrierByYeMoDaWhYeMoDaSyIn_TyIn(yearSms, monthSms, daySms, messageTypeSms, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupCarrierByYeMoDaWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = new ArrayList<>(messageTypeSms.size());
        for (OMessageType messageTypeSm : messageTypeSms) {
            l.add(messageTypeSm.name());
        }
        return smshour_repo.groupSmsCarrierByYeMoDaWhYeMoDaSyIn_TyIn(yearSms, monthSms, daySms, l, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupSystemIdByYeMoDaWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = new ArrayList<>(messageTypeSms.size());
        for (OMessageType messageTypeSm : messageTypeSms) {
            l.add(messageTypeSm.name());
        }
        return smshour_repo.groupSystemIdByYeMoDaWhYeMoDaEqMessageTypeIn(yearSms, monthSms, daySms, l, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupCarrierByYeMoDaHoWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, List<String> messageTypeSms, List<String> list_sid) {
        return smshour_repo.groupSmsCarrierTyByYeMoDaHoWhYeMoDaSyIn_TyIn(yearSms, monthSms, daySms, messageTypeSms, list_sid);
    }

    public List<SmsByYearMonthDayHour> getGroupCarrierByYeMoDaHoWhYeMoDayEqMessageTypeIn(int yearSms, int monthSms, int daySms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        Collection<String> l = messageTypeSms.stream().map(OMessageType::name).collect(Collectors.toList());
        return smshour_repo.groupSmsCarrierTyByYeMoDaHoWhYeMoDaSyIn_TyIn(yearSms, monthSms, daySms, l, list_sid);
    }

    public List<SmsByYearMonthDayHour> groupSmsCarrierAndMessageTypeByYeMoDaHoWhYeMoDaSyIn_CarrierTyIn(int yearSms, int monthSms, int daySms, Collection<String> carrier_list, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        Collection<String> l = messageTypeSms.stream().map(OMessageType::name).collect(Collectors.toList());
        return smshour_repo.groupSmsCarrierAndMessageTypeByYeMoDaHoWhYeMoDaSyIn_CarrierTyIn(yearSms, monthSms, daySms, carrier_list, l, list_sid);
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
    public List<SmsByYearMonth> groupCarrierByYeMoWhMoInMessageTypeIn(int yearSms, List<Integer> monthSms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
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
            yearBefore = smshour_repo.groupSmsCarrierTyByYeMoWhYeMoSyIn_TyIn(yearSms - 1, monthsOfPrevYear, messagesType, list_sid);
        }
        if (yearBefore == null) {
            yearBefore = new ArrayList<>();
        }
        log.info("Searching: year[{}] months[{}] message type[{}] sids[{}]", yearSms, monthSms, messagesType, list_sid);
        yearBefore.addAll(smshour_repo.groupSmsCarrierTyByYeMoWhYeMoSyIn_TyIn(yearSms, monthSms, messagesType, list_sid));
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
    public List<SmsByYearMonth> groupSmsCarrierAndMessageTypeByYeMoWhYeMoSyIn_CarrierInTyIn(int yearSms, List<Integer> monthSms, Set<Carrier> carrierSet, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> messagesTypeList = messageTypeSms.stream().map(OMessageType::name).collect(Collectors.toList());
        List<String> carrierList = carrierSet.stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
        log.info("Preparing Searching: year[{}] months[{}] Message Type [{}] sids[{}]",
                yearSms, monthSms, messageTypeSms, list_sid);
        List<SmsByYearMonth> yearBefore = null;
        List<Integer> monthsOfPrevYear = findMonthsOfPreviousYear(monthSms);

        if (monthsOfPrevYear != null) {
            log.info("Month ({}) before actual year detected. Searching year[{}] month[{}] Message Type [{}] sid[{}]",
                    monthsOfPrevYear, yearSms - 1, monthsOfPrevYear, messageTypeSms, list_sid);
            yearBefore = smshour_repo.groupSmsCarrierAndMessageTypeByYeMoWhYeMoInSyIn_CarrierInTyIn(yearSms - 1, monthsOfPrevYear, carrierList, messagesTypeList, list_sid);
        }
        if (yearBefore == null) {
            yearBefore = new ArrayList<>();
        }
        log.info("Searching: year[{}] months[{}] carrier [{}] message type[{}] sids[{}]", yearSms, monthSms, carrierList, messagesTypeList, list_sid);
        yearBefore.addAll(smshour_repo.groupSmsCarrierAndMessageTypeByYeMoWhYeMoInSyIn_CarrierInTyIn(yearSms, monthSms, carrierList, messagesTypeList, list_sid));
        return yearBefore;
    }

    public List<SmsByYearMonth> groupSmsCarrierAndMessageTypeByYeMoWhYeMoSyIn_CarrierInTyIn(int yearSms, int monthSms, Set<Carrier> carrierSet, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> messagesTypeList = messageTypeSms.stream().map(OMessageType::name).collect(Collectors.toList());
        List<String> carrierList = carrierSet.stream().map(Carrier::getCarrierCharcode).collect(Collectors.toList());
        return smshour_repo.groupSmsCarrierAndMessageTypeByYeMoWhYeMoSyIn_CarrierInTyIn(yearSms, monthSms, carrierList, messagesTypeList, list_sid);
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
        return smshour_repo.groupSmsCarrierAndMessageTypeByYeMoWhYeMoSyIn_TyIn(yearSms, monthSms, messageType, list_sid);
    }

    public List<SmsByYearMonthDay> getGroupCarrierByYearMonthDayMessageType(int yearSms, int monthSms, int daySms, String messageType, List<String> list_sid) {
        List<String> lis = new ArrayList<>(1);
        lis.add(messageType);
        return groupSmsCarrierByYeMoDaWhYeMoDaSyIn_TyIn(yearSms, monthSms, daySms, lis, list_sid);
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

    public List<SmsByYearMonthDay> groupSmsCarrierMessageTypeByYeMoDaWhYeMoSyIn_CarrierTyIn(int yearSms, int monthSms, Collection<String> carrier_list, Collection<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = messageTypeSms.stream().map(OMessageType::name).collect(Collectors.toList());
        return this.groupSmsCarrierMessageTypeByYeMoDaWhYeMoSyIn_CarrierTyIn(yearSms, monthSms, carrier_list, l, list_sid);
    }

    public List<SmsByYearMonthDay> groupSmsCarrierMessageTypeByYeMoDaWhYeMoSyIn_CarrierTyIn(int yearSms, int monthSms, Collection<String> carrier_list, List<String> messageTypeSms, List<String> list_sid) {
        List<SmsByYearMonthDay> smsByYearMonthDays = smshour_repo.groupSmsCarrierMessageTypeByYeMoDaWhYeMoSyIn_CarrierTyIn(yearSms, monthSms, carrier_list, messageTypeSms, list_sid);
        Calendar c = Calendar.getInstance();
        carrier_list.stream().forEach(actualCarrierForeach -> {
            messageTypeSms.stream().forEach(actualMessageTypeForeach -> {
                for (int actualDayFor = 1; actualDayFor < c.get(Calendar.DAY_OF_MONTH); actualDayFor++) {
                    boolean ithasit = false;
                    for (SmsByYearMonthDay smsByYearMonthDay : smsByYearMonthDays) {
                        if (smsByYearMonthDay.getGroupBy() == actualDayFor &&
                                smsByYearMonthDay.getMessageType().equalsIgnoreCase(actualMessageTypeForeach) &&
                                smsByYearMonthDay.getSomeCode().equalsIgnoreCase(actualCarrierForeach)) {
                            ithasit = true;
                            break;
                        }
                    }
                    if (!ithasit) {
                        smsByYearMonthDays.add(
                                new SmsByYearMonthDay(0, yearSms, monthSms, actualDayFor, actualCarrierForeach, actualMessageTypeForeach));
                    }
                }
            });
        });
        return smsByYearMonthDays;
    }

    public List<SmsByYearMonthDay> groupSmsCarrierMessageTypeByYeMoDaWhYeMoSyIn_CarrierTyIn(int yearSms, int monthSms, int daySms, Collection<String> carrier_list, List<String> messageTypeSms, List<String> list_sid) {
        List<SmsByYearMonthDay> smsByYearMonthDays = smshour_repo.groupSmsCarrierMessageTypeByYeMoDaWhYeMoSyIn_CarrierTyIn(yearSms, monthSms, daySms, carrier_list, messageTypeSms, list_sid);
        Calendar c = Calendar.getInstance();
        carrier_list.stream().forEach(actualCarrierForeach -> {
            messageTypeSms.stream().forEach(actualMessageTypeForeach -> {
//                for (int actualDayFor = 1; actualDayFor < c.get(Calendar.DAY_OF_MONTH); actualDayFor++) {
                boolean ithasit = false;
                for (SmsByYearMonthDay smsByYearMonthDay : smsByYearMonthDays) {
                    if (smsByYearMonthDay.getGroupBy() == daySms &&
                            smsByYearMonthDay.getMessageType().equalsIgnoreCase(actualMessageTypeForeach) &&
                            smsByYearMonthDay.getSomeCode().equalsIgnoreCase(actualCarrierForeach)) {
                        ithasit = true;
                        break;
                    }
                }
                if (!ithasit) {
                    smsByYearMonthDays.add(
                            new SmsByYearMonthDay(0, yearSms, monthSms, daySms, actualCarrierForeach, actualMessageTypeForeach));
                }
//                }
            });
        });
        return smsByYearMonthDays;
    }

    public List<SmsByYearMonthDay> groupSmsByYeMoDaSyWhYeMoSyIn_TyIn(int yearSms, int monthSms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = messageTypeSms
                .stream()
                .map(OMessageType::name)
                .collect(Collectors.toList());
        List<SmsByYearMonthDay> hourList = smshour_repo.groupSmsByYeMoDaSyWhYeMoSyIn_TyIn(yearSms, monthSms, l, list_sid);
        Calendar c = Calendar.getInstance();
        /* Recorre desde el dia 1 hasta el dia actual*/
        for (int actualDayFor = 1; actualDayFor <= c.get(Calendar.DAY_OF_MONTH); actualDayFor++) {
            for (String actualSystemidFor : list_sid) {
                for (OMessageType actualMessageTypeFor : OMessageType.values()) {
                    boolean thisHasIt = false;
                    for (SmsByYearMonthDay actualSmsDayFor : hourList) {
                        if (actualSmsDayFor.getDaySms() == actualDayFor &&
                                actualSmsDayFor.getMessageType().equalsIgnoreCase(actualMessageTypeFor.name()) &&
                                actualSmsDayFor.getSomeCode().equalsIgnoreCase(actualSystemidFor)) {
                            thisHasIt = true;
                            break;
                        }
                    }
                    if (!thisHasIt) {
                        hourList.add(
                                new SmsByYearMonthDay(0, yearSms, monthSms, actualDayFor, actualSystemidFor, actualMessageTypeFor.name())
                        );
                    }
                }
            }
        }
        return hourList;
    }

    public List<SmsByYearMonthDay> groupSmsByYeMoDaSyWhYeMoSyIn_TyIn(int yearSms, int monthSms, int daySms, Set<OMessageType> messageTypeSms, List<String> list_sid) {
        List<String> l = messageTypeSms
                .stream()
                .map(OMessageType::name)
                .collect(Collectors.toList());
        List<SmsByYearMonthDay> hourList = smshour_repo.groupSmsByYeMoDaSyWhYeMoDaSyIn_TyIn(yearSms, monthSms, daySms, l, list_sid);
        Calendar c = Calendar.getInstance();
        /* Recorre desde el dia 1 hasta el dia actual*/
//        for (int actualDayFor = 1; actualDayFor <= c.get(Calendar.DAY_OF_MONTH); actualDayFor++) {
        for (String actualSystemidFor : list_sid) {
            for (OMessageType actualMessageTypeFor : OMessageType.values()) {
                boolean thisHasIt = false;
                for (SmsByYearMonthDay actualSmsDayFor : hourList) {
                    if (actualSmsDayFor.getDaySms() == daySms &&
                            actualSmsDayFor.getMessageType().equalsIgnoreCase(actualMessageTypeFor.name()) &&
                            actualSmsDayFor.getSomeCode().equalsIgnoreCase(actualSystemidFor)) {
                        thisHasIt = true;
                        break;
                    }
                }
                if (!thisHasIt) {
                    hourList.add(
                            new SmsByYearMonthDay(0, yearSms, monthSms, daySms, actualSystemidFor, actualMessageTypeFor.name())
                    );
                }
            }
//            }
        }
        return hourList;
    }

    public List<SmsByYearMonth> getGroupSmsByYearMonthMessageType(int yearSms, int monthSms, List<String> list_sid) {
        return smshour_repo.groupSmsMessageTypeByYeMoWhYeMoSyIn(yearSms, monthSms, list_sid);
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
    public List<SmsByYearMonth> groupSmsMessageTypeByYeMoWhYeMoInSyIn(int yearSms, List<Integer> monthSms, List<String> list_sid) {
        log.info("Preparing Searching: year[{}] months[{}] sids[{}]", yearSms, monthSms, list_sid);
        List<SmsByYearMonth> yearBefore = null;
        List<Integer> monthsOfPrevYear = findMonthsOfPreviousYear(monthSms);
        if (monthsOfPrevYear != null) {
            log.info("Month ({}) before actual year detected. Searching year[{}] month[{}] sid[{}]",
                    monthsOfPrevYear, yearSms - 1, monthsOfPrevYear, list_sid);
            yearBefore = smshour_repo.groupSmsMessageTypeByYeMoWhYeMoInSyIn(yearSms - 1, monthsOfPrevYear, list_sid);
        }
        if (yearBefore == null) {
            yearBefore = new ArrayList<>();
        }
        log.info("Searching: year[{}] months[{}] sids[{}]", yearSms, monthSms, list_sid);
        yearBefore.addAll(smshour_repo.groupSmsMessageTypeByYeMoWhYeMoInSyIn(yearSms, monthSms, list_sid));
        return yearBefore;
    }

    /**
     * Busca Year, Month In y sids INs
     *
     * @param yearSms
     * @param month
     * @param systemidStringList
     * @return
     */
    public List<SmsByYearMonth> groupSmsSyTyByYeMoWhYeMoSyInTyIn(int yearSms, Integer month, List<String> messageType, List<String> systemidStringList) {
        return smshour_repo.groupSmsSyTyByYeMoWhYeMoSyInTyIn(yearSms, month, messageType, systemidStringList);
    }

    public List<SmsByYearMonth> groupSmsSyTyByYeMoWhYeMoInSyInTyIn(int yearSms, List<Integer> month, List<String> messageType, List<String> systemidStringList) {
        return smshour_repo.groupSmsSyTyByYeMoWhYeMoInSyInTyIn(yearSms, month, messageType, systemidStringList);
    }

    /**
     * Busca Year, Month In y sids INs
     *
     * @param yearSms
     * @param month
     * @param systemidStringList
     * @return
     */
    public List<SmsByYearMonth> groupSmsSyTyByYeMoWhYeMoSyInTyIn(int yearSms, Integer month, List<String> systemidStringList) {
        return smshour_repo.groupSmsMessageTypeByYeMoWhYeMoSyIn(yearSms, month, systemidStringList);
    }

    /**
     * Busca Year, Month, Day, sids IN y message-type IN
     *
     * @param yearSms
     * @param month
     * @param systemidStringList
     * @return
     */
    public List<SmsByYearMonthDay> groupSmsMessageTypeByYeMoDaSyWhYeMoDaSyIn_TyIn(int yearSms, int month, int day, List<String> messageType, List<String> systemidStringList) {
        return smshour_repo.groupSmsMessageTypeByYeMoDaSyWhYeMoDaSyIn_TyIn(yearSms, month, day, messageType, systemidStringList);
    }

    ///////////////////////

    /**
     * Devuelve los meses que se deben buscar con el anio anterior y modifica
     * dejando solo los meses con busqueda del anio actual.
     *
     * @param monthSms puede ser modificado y su size debe ser igual a tres.
     * @return Meses del anio anterior o null si no tiene.
     */
    private List<Integer> findMonthsOfPreviousYear(List<Integer> monthSms) {
        if (monthSms == null || monthSms.size() < 3) {
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
            /* La antigua posicion 1*/
            monthSms.remove(0);
        }
        return l;
    }

    public List<SmsByYearMonthDay> groupSmsByYeMoDaTyWhYeMoSyIn(int yearSms, int monthSms, List<String> list_sid) {
        return smshour_repo.groupSmsMessageTypeByYeMoDaWhYeMoSyIn(yearSms, monthSms, list_sid);
    }

    public List<SmsByYearMonthDay> groupSmsMessageTypeByYeMoDaWhYeMoDaSyIn(int yearSms, int monthSms, int daySms, List<String> list_sid) {
        return smshour_repo.groupSmsMessageTypeByYeMoDaWhYeMoDaSyIn(yearSms, monthSms, daySms, list_sid);
    }

    public List<SmsByYearMonthDayHour> groupSmsYeMoDaHoTyWhYeMoDaSyIn(int yearSms, int monthSms, int daySms, List<String> list_sid) {
        List<SmsByYearMonthDayHour> hourList = smshour_repo.groupSmsMessageTypeByYeMoDaHoWhYeMoDaSyIn(yearSms, monthSms, daySms, list_sid);
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
                /* TODO Descablear year*/
                SmsByYearMonthDayHour o = new SmsByYearMonthDayHour(0, c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), hourRunner, "N/A");
                hourList.add(o);
            }
        }
        return hourList;

    }

    public List<SmsByYearMonthDayHour> groupSmsSystemidMessageTypeByYeMoDaHoWhYeMoDaSyIn(int yearSms, int monthSms, int daySms, List<String> list_sid) {
        List<SmsByYearMonthDayHour> hourList = smshour_repo.groupSmsSystemidMessageTypeByYeMoDaHoWhYeMoDaSyIn(yearSms, monthSms, daySms, list_sid);
        Calendar c = Calendar.getInstance();
        /* Recorre desde las 0 horas hasta la hora actual*/
        for (int actualHourFor = 0; actualHourFor <= c.get(Calendar.HOUR_OF_DAY); actualHourFor++) {
            /* Recorre todos los MessageType solicitados */
            for (OMessageType mt : OMessageType.values()) {
                for (String actualCarrierFor : list_sid) {
                    boolean thisHasIt = false;
                    for (SmsByYearMonthDayHour actualSmsHourFor : hourList) {
                        if (actualSmsHourFor.getHourSms() == actualHourFor &&
                                actualSmsHourFor.getMessageType().equalsIgnoreCase(mt.name()) &&
                                actualSmsHourFor.getSomeCode().equalsIgnoreCase(actualCarrierFor)) {
                            thisHasIt = true;
                            break;
                        }
                    }
                    if (!thisHasIt) {
                        hourList.add(
                                new SmsByYearMonthDayHour(0, yearSms, monthSms, daySms, actualHourFor, actualCarrierFor, mt.name())
                        );
                    }
                }
            }
        }
        return hourList;

    }

    public List<SmsByYearMonthDayHour> groupSmsYeMoDaHoTyWhYeMoDaSyIn(int yearSms, int monthSms, int daySms, int hourSms, List<String> list_sid) {
        List<SmsByYearMonthDayHour> hourList = smshour_repo.groupSmsMessageTypeByYeMoDaHoWhYeMoDaHoSyIn(yearSms, monthSms, daySms, hourSms, list_sid);
        return hourList;

    }

    public List<SmsByYearMonthDayHour> groupSmsSystemidMessageTypeByYeMoDaHoWhYeMoDaHoSyIn(int yearSms, int monthSms, int daySms, int hourSms, List<String> list_sid) {
        List<SmsByYearMonthDayHour> hourList = smshour_repo.groupSmsSystemidMessageTypeByYeMoDaHoWhYeMoDaHoSyIn(yearSms, monthSms, daySms, hourSms, list_sid);
        Calendar c = Calendar.getInstance();
        /* Recorre desde las 0 horas hasta la hora actual*/
        for (String actualSystemidFor : list_sid) {
            for (OMessageType actualMessageTypeFor : OMessageType.values()) {
                boolean thisHasIt = false;
                for (SmsByYearMonthDayHour actualSmsHourFor : hourList) {
                    if (actualSmsHourFor.getHourSms() == hourSms &&
                            actualSmsHourFor.getMessageType().equalsIgnoreCase(actualMessageTypeFor.name()) &&
                            actualSmsHourFor.getSomeCode().equalsIgnoreCase(actualSystemidFor)) {
                        thisHasIt = true;
                        break;
                    }
                }
                if (!thisHasIt) {
                    hourList.add(
                            new SmsByYearMonthDayHour(0, yearSms, monthSms, daySms, hourSms, actualSystemidFor, actualMessageTypeFor.name())
                    );
                }
            }
        }
        return hourList;

    }

    public List<SmsByYearMonthDay> groupSmsCarrierTyByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(int yearSms, int monthSms, int daySms, List<String> stringMessagesTypeList, List<String> stringCarrierStringList, List<String> stringSystemidList) {
        return smshour_repo.groupSmsCarrierTyByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(yearSms, monthSms, daySms, stringMessagesTypeList, stringCarrierStringList, stringSystemidList);
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

    /**
     * Consulta Operadora Chart. Completa con 0 hour-carrier-messagetype.
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param carrierList
     * @param messageTypeList
     * @param systemidStringList
     * @return
     */
    public List<SmsByYearMonthDayHour> groupSmsCarrierAndMessageTypeByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(int yearSms, int monthSms, int daySms, List<String> carrierList, List<String> messageTypeList, List<String> systemidStringList) {
        List<SmsByYearMonthDayHour> hourList = smshour_repo.groupSmsCarrierAndMessageTypeByYeMoDaHoWhYeMoDaSyIn_CarrierInTyIn(yearSms, monthSms, daySms, carrierList, messageTypeList, systemidStringList);
        Calendar c = Calendar.getInstance();
        /* Recorre desde las 0 horas hasta la hora actual*/
        for (int actualHourFor = 0; actualHourFor <= c.get(Calendar.HOUR_OF_DAY); actualHourFor++) {
            /* Recorre todos los MessageType solicitados */
            for (String actualMessageTypeFor : messageTypeList) {
                for (String actualCarrierFor : carrierList) {
                    boolean thisHasIt = false;
                    for (SmsByYearMonthDayHour actualSmsHourFor : hourList) {
                        if (actualSmsHourFor.getHourSms() == actualHourFor &&
                                actualSmsHourFor.getMessageType().equalsIgnoreCase(actualMessageTypeFor) &&
                                actualSmsHourFor.getSomeCode().equalsIgnoreCase(actualCarrierFor)) {
                            thisHasIt = true;
                            break;
                        }
                    }
                    if (!thisHasIt) {
                        hourList.add(
                                new SmsByYearMonthDayHour(0, yearSms, monthSms, daySms, actualHourFor, actualCarrierFor, actualMessageTypeFor)
                        );
                    }
                }
            }
        }
        return hourList;
    }

    /**
     * Consulta Operadora Chart. Completa con 0 hour-carrier-messagetype.
     *
     * @param yearSms
     * @param monthSms
     * @param daySms
     * @param carrierList
     * @param messageTypeList
     * @param systemidStringList
     * @return
     */
    public List<SmsByYearMonthDayHour> groupSmsCarrierAndMessageTypeByYeMoDaWhYeMoDaSyIn_CarrierInTyIn(int yearSms, int monthSms, int daySms, int hourSms, List<String> carrierList, List<String> messageTypeList, List<String> systemidStringList) {
        List<SmsByYearMonthDayHour> hourList = smshour_repo.groupSmsCarrierAndMessageTypeByYeMoDaHoWhYeMoDaHoSyIn_CarrierInTyIn(yearSms, monthSms, daySms, hourSms, carrierList, messageTypeList, systemidStringList);
        Calendar c = Calendar.getInstance();
        /* Recorre desde las 0 horas hasta la hora actual*/
//        for (int actualHourFor = 0; actualHourFor <= c.get(Calendar.HOUR_OF_DAY); actualHourFor++) {
        /* Recorre todos los MessageType solicitados */
        for (String actualMessageTypeFor : messageTypeList) {
            for (String actualCarrierFor : carrierList) {
                boolean thisHasIt = false;
                for (SmsByYearMonthDayHour actualSmsHourFor : hourList) {
                    if (actualSmsHourFor.getHourSms() == hourSms &&
                            actualSmsHourFor.getMessageType().equalsIgnoreCase(actualMessageTypeFor) &&
                            actualSmsHourFor.getSomeCode().equalsIgnoreCase(actualCarrierFor)) {
                        thisHasIt = true;
                        break;
                    }
                }
                if (!thisHasIt) {
                    hourList.add(
                            new SmsByYearMonthDayHour(0, yearSms, monthSms, daySms, hourSms, actualCarrierFor, actualMessageTypeFor)
                    );
                }
            }
//            }
        }
        return hourList;
    }
}
