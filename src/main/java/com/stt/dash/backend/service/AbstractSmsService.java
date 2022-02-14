package com.stt.dash.backend.service;

import com.stt.dash.backend.data.entity.sms.AbstractSMS;
import com.stt.dash.backend.repositories.sms.*;
import com.stt.dash.ui.MainView;
import com.stt.dash.ui.utils.ODateUitls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

@Service
public class AbstractSmsService {

    private static final Logger log = LoggerFactory.getLogger(AbstractSmsService.class.getName());
    private final int pageSize = 3000;
    private final BaseSmsRepository[] monthRepos;

    public AbstractSmsService(JanSmsRepository jan_repo, FebSmsRepository feb_repo, MarSmsRepository mar_repo, AprSmsRepository apr_repo, MaySmsRepository may_repo, JunSmsRepository jun_repo, JulSmsRepository jul_repo, AugSmsRepository aug_repo, SepSmsRepository sep_repo, OctSmsRepository oct_repo, NovSmsRepository nov_repo, DecSmsRepository dec_repo) {
        monthRepos = new BaseSmsRepository[12];
        monthRepos[0] = jan_repo;
        monthRepos[1] = feb_repo;
        monthRepos[2] = mar_repo;
        monthRepos[3] = apr_repo;
        monthRepos[4] = may_repo;
        monthRepos[5] = jun_repo;
        monthRepos[6] = jul_repo;
        monthRepos[7] = aug_repo;
        monthRepos[8] = sep_repo;
        monthRepos[9] = oct_repo;
        monthRepos[10] = nov_repo;
        monthRepos[11] = dec_repo;
    }

    public Page<AbstractSMS> findAll(int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page, itemsPerPage, Sort.by("date"));
        return monthRepos[1].findAll(pageable);
    }

    public Page<AbstractSMS> findByPhoneNumer(int year, int month, List<String> list_sid, String destination) {
        return findByPhoneNumer(year, month, list_sid, destination, 0, pageSize);
    }

    public Page<AbstractSMS> findByPhoneNumer(LocalDate dateOne, LocalDate dateTwo, List<String> list_sid, String destination, int page) {
        return findByPhoneNumer(dateOne, dateTwo, list_sid, destination, page, pageSize);
    }

    public Page<AbstractSMS> findByPhoneNumer(int year, int month, List<String> list_sid, String destination, int page) {
        return findByPhoneNumer(year, month, list_sid, destination, page, pageSize);
    }

    public Page<AbstractSMS> findByPhoneNumer(int year, int month, List<String> list_sid, String destination, Collection<String> messageType, int page) {
        return findByPhoneNumer(year, month, list_sid, destination, messageType, page, pageSize);
    }

    public Page<AbstractSMS> findByPhoneNumer(int year, int month, List<String> list_sid, String destination, Collection<String> messageType, String carrierCharCode, int page) {
        return findByPhoneNumer(year, month, list_sid, destination, messageType, carrierCharCode, page, pageSize);
    }

    public Page<AbstractSMS> findByPhoneNumer(LocalDate dateOne, LocalDate dateTwo, List<String> list_sid, String destination, Collection<String> messageType, String carrierCharCode, int page) {
        return findByPhoneNumer(dateOne, dateTwo, list_sid, destination, messageType, carrierCharCode, page, pageSize);
    }

    public Page<AbstractSMS> findBySystemIdIn(LocalDate dateOne, LocalDate dateTwo, List<String> list_sid, int page) {
        return findBySystemIdIn(dateOne, dateTwo, list_sid, page, pageSize);
    }

    public Page<AbstractSMS> findByCarrier(LocalDate dateOne, LocalDate dateTwo, List<String> list_sid, String carrierCharCode, int page) {
        return findByCarrier(dateOne, dateTwo, list_sid, carrierCharCode, page, pageSize);
    }

    public Page<AbstractSMS> findByMessageType(LocalDate dateOne, LocalDate dateTwo, Collection<String> list_sid, Collection<String> messageType, int page) {
        return findByMessageType(dateOne, dateTwo, list_sid, messageType, page, pageSize);
    }

    public Page<AbstractSMS> getAllMessages(LocalDate dateOne, LocalDate dateTwo, List<String> systemIds, int page) {
        return getAllMessages(dateOne, dateTwo, systemIds, page, pageSize);
    }

    public Page<AbstractSMS> findByCarrierAndMessageType(LocalDate dateOne, LocalDate dateTwo, Collection<String> list_sid, String carrierCharCode, Collection<String> messageType, int page) {
        return findByCarrierAndMessageType(dateOne, dateTwo, list_sid, carrierCharCode, messageType, page, pageSize);
    }

    public Page<AbstractSMS> findByMessageTypeCase10(
            LocalDate dateOne,
            LocalDate dateTwo,
            List<String> list_sid,
            Collection<String> messageType,
            int page) {

        /* DATE */
        Date dateStart = ODateUitls.valueOf(dateOne);
        Date dateEnd = null;
        try {
            dateEnd = ODateUitls.parseToDateTo(dateTwo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Pageable paging = PageRequest.of(page, pageSize);
        return monthRepos[dateOne.getMonthValue() - 1].findByDateBetweenAndSystemIdInAndMessageTypeIn(
                dateStart, dateEnd, list_sid, messageType, paging);
    }

    /**
     * Usado para los casos: #1.
     *
     * @param dateOne
     * @param dateTwo
     * @param list_sid
     * @param destination
     * @param page
     * @param pageSize
     * @return
     */
    public Page<AbstractSMS> findByPhoneNumer(
            LocalDate dateOne,
            LocalDate dateTwo,
            List<String> list_sid,
            String destination,
            int page, int pageSize) {

        /* DATE */
        Date dateStart = ODateUitls.valueOf(dateOne);
        Date dateEnd = null;
        try {
            dateEnd = ODateUitls.parseToDateTo(dateTwo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Pageable paging = PageRequest.of(page, pageSize, Sort.by("date"));

        return monthRepos[dateOne.getMonthValue() - 1].findByDateBetweenAndSystemIdInAndDestination(
                dateStart,
                dateEnd, list_sid, destination, paging);
    }

    @Deprecated
    public Page<AbstractSMS> findByPhoneNumer(
            int year,
            int month,
            List<String> list_sid,
            String destination,
            int page, int pageSize) {
        try {

            /* DATE */
            Date dateStart = parseYearMonthToDate(year, month);
            Date dateEnd = parseYearMonthToDate(year, MainView.localDateTime.getMonth().plus(1).getValue());

            Pageable paging = PageRequest.of(page, pageSize);

            return monthRepos[month - 1].findByDateBetweenAndSystemIdInAndDestination(
                    dateStart,
                    dateEnd, list_sid, destination, paging);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Deprecated
    public Page<AbstractSMS> findByPhoneNumer(
            int year,
            int month,
            List<String> list_sid,
            String destination,
            Collection<String> messageType,
            int page, int pageSize) {
        try {

            /* DATE */
            Date dateStart = parseYearMonthToDate(year, month);
            Date dateEnd = parseYearMonthToDate(year, MainView.localDateTime.getMonth().plus(1).getValue());

            Pageable paging = PageRequest.of(page, pageSize);

            return monthRepos[month - 1].findByDateBetweenAndSystemIdInAndDestinationAndMessageTypeIn(
                    dateStart,
                    dateEnd, list_sid, destination, messageType, paging);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Usado para los casos: #9.
     *
     * @param dateOne
     * @param dateTwo
     * @param list_sid
     * @param destination
     * @param messageType
     * @param page
     * @param pageSize
     * @return
     */
    public Page<AbstractSMS> findByPhoneNumber(
            LocalDate dateOne,
            LocalDate dateTwo,
            List<String> list_sid,
            Collection<String> messageType,
            String destination,
            int page, int pageSize) {

        /* DATE */
        Date dateStart = ODateUitls.valueOf(dateOne);
        Date dateEnd = null;
        try {
            dateEnd = ODateUitls.parseToDateTo(dateTwo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Pageable paging = PageRequest.of(page, pageSize);
        return monthRepos[dateOne.getMonthValue() - 1].findByDateBetweenAndSystemIdInAndDestinationAndMessageTypeIn(
                dateStart,
                dateEnd, list_sid, destination, messageType, paging);
    }

    public Page<AbstractSMS> findByPhoneNumer(
            LocalDate dateOne,
            LocalDate dateTwo,
            List<String> list_sid,
            String destination,
            Collection<String> messageType,
            String carrierCharCode,
            int page, int pageSize) {
        Date dateEnd = null;
        try {
            /* DATE */
            Date dateStart = ODateUitls.valueOf(dateOne);
            dateEnd = ODateUitls.parseToDateTo(dateTwo);
            Pageable paging = PageRequest.of(page, pageSize);

            return monthRepos[dateOne.getMonthValue() - 1].findByDateBetweenAndSystemIdInAndDestinationAndMessageTypeInAndCarrierCharCode(
                    dateStart,
                    dateEnd,
                    list_sid,
                    destination,
                    messageType,
                    carrierCharCode,
                    paging);
        } catch (ParseException ex) {
            log.error("Error parsing date " + dateEnd);
            throw new UserFriendlyDataException("Error con la fecha final");
        }
    }

    @Deprecated
    public Page<AbstractSMS> findByPhoneNumer(
            int year,
            int month,
            List<String> list_sid,
            String destination,
            Collection<String> messageType,
            String carrierCharCode,
            int page, int pageSize) {
        try {

            /* DATE */
            Date dateStart = parseYearMonthToDate(year, month);
            Date dateEnd = parseYearMonthToDate(year, MainView.localDateTime.getMonth().plus(1).getValue());

            Pageable paging = PageRequest.of(page, pageSize);

            return monthRepos[month - 1].findByDateBetweenAndSystemIdInAndDestinationAndMessageTypeInAndCarrierCharCode(
                    dateStart,
                    dateEnd,
                    list_sid,
                    destination,
                    messageType,
                    carrierCharCode,
                    paging);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Page<AbstractSMS> findBySystemIdIn(int year, int month, List<String> list_sid) {
        return findBySystemIdIn(year, month, list_sid, 0);
    }

    /**
     * Usado para los casos: #2.
     *
     * @param dateOne
     * @param dateTwo
     * @param list_sid
     * @param page
     * @return
     */
    public Page<AbstractSMS> findBySystemIdIn(LocalDate dateOne, LocalDate dateTwo, List<String> list_sid, int page, int pageSize) {
        /* DATE */
        Date dateStart = ODateUitls.valueOf(dateOne);
        Date dateEnd = null;
        try {
            dateEnd = ODateUitls.parseToDateTo(dateTwo);
        } catch (ParseException e) {
            log.error("Error parsing date " + dateEnd);
            throw new UserFriendlyDataException("Error con la fecha final");
        }

        return monthRepos[dateOne.getMonthValue() - 1].findByDateBetweenAndSystemIdIn(
                dateStart,
                dateEnd, list_sid,
                //                    paging);
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "date", "carrierCharCode")));

    }

    @Deprecated
    public Page<AbstractSMS> findBySystemIdIn(int year, int month, List<String> list_sid, int page) {
        try {
            /* DATE */
            Date dateStart = parseYearMonthToDate(year, month);
            Date dateEnd = parseYearMonthToDate(year, MainView.localDateTime.getMonth().plus(1).getValue());

            Pageable paging = PageRequest.of(page, pageSize);
            return monthRepos[month - 1].findByDateBetweenAndSystemIdIn(
                    dateStart,
                    dateEnd, list_sid,
                    //                    paging);
                    PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "date", "carrierCharCode")));
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Page<AbstractSMS> findBySystemIdIn(int year, int month, int day, List<String> list_sid) {
        return findBySystemIdIn(year, month, day, list_sid, 0);
    }

    public Page<AbstractSMS> findBySystemIdIn(int year, int month, int day, List<String> list_sid, int page) {
        try {
            /* DATE */
            System.out.println("A convertir: " + year + " " + month + " " + day);
            Date dateStart = parseYearMonthDayToDate(year, month, day);
            LocalDateTime t = ODateUitls.valueOf(dateStart).plusDays(1);
            Date dateEnd = parseYearMonthDayHourToDate(t.getYear(), t.getMonthValue(), t.getDayOfMonth(), t.getHour());

            Pageable paging = PageRequest.of(page, pageSize);
            return monthRepos[month - 1].findByDateBetweenAndSystemIdIn(
                    dateStart,
                    dateEnd, list_sid, paging);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Page<AbstractSMS> findByMessageType(int year, int month, Collection<String> list_sid, Collection<String> messageType) {
        return findByMessageType(year, month, list_sid, messageType, 0);
    }

    /**
     * Usado para casos: #8, #10.
     *
     * @param dateOne
     * @param dateTwo
     * @param list_sid
     * @param messageType
     * @param page
     * @return
     */
    public Page<AbstractSMS> findByMessageType(LocalDate dateOne, LocalDate dateTwo, Collection<String> list_sid, Collection<String> messageType, int page, int pageSize) {
        /* DATE */
        Date dateStart = ODateUitls.valueOf(dateOne);
        Date dateEnd = null;
        try {
            dateEnd = ODateUitls.parseToDateTo(dateTwo);
        } catch (ParseException e) {
            log.error("Error parsing date " + dateEnd);
            throw new UserFriendlyDataException("Error con la fecha final");
        }

        Pageable paging = PageRequest.of(page, pageSize, Sort.by("date"));
        log.info("Repositorio: {} - {}", monthRepos[dateOne.getMonthValue() - 1], dateOne.getMonthValue() - 1);
        return monthRepos[dateOne.getMonthValue() - 1].findByDateBetweenAndSystemIdInAndMessageTypeIn(
                dateStart,
                dateEnd,
                list_sid,
                messageType,
                paging);
    }

    @Deprecated
    public Page<AbstractSMS> findByMessageType(int year, int month, Collection<String> list_sid, Collection<String> messageType, int page) {
        try {
            /* DATE */
            Date dateStart = parseYearMonthToDate(year, month);
            Date dateEnd = parseYearMonthToDate(year, MainView.localDateTime.getMonth().plus(1).getValue());

            Pageable paging = PageRequest.of(page, pageSize);
            return monthRepos[month - 1].findByDateBetweenAndSystemIdInAndMessageTypeIn(
                    dateStart,
                    dateEnd,
                    list_sid,
                    messageType,
                    paging);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Page<AbstractSMS> findByCarrier(int year, int month, List<String> list_sid, String carrierCharCode) {
        return findByCarrier(year, month, list_sid, carrierCharCode, 0);
    }

    @Deprecated
    public Page<AbstractSMS> findByCarrier(int year, int month, List<String> list_sid, String carrierCharCode, int page) {
        try {
            /* DATE */
            Date dateStart = parseYearMonthToDate(year, month);
            Date dateEnd = parseYearMonthToDate(year, MainView.localDateTime.getMonth().plus(1).getValue());

            Pageable paging = PageRequest.of(page, pageSize);
            return monthRepos[month - 1].findByDateBetweenAndSystemIdInAndCarrierCharCode(
                    dateStart,
                    dateEnd,
                    list_sid, carrierCharCode,
                    paging);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Usado para los casos: #4, #6.
     *
     * @param dateOne
     * @param dateTwo
     * @param list_sid
     * @param carrierCharCode
     * @param page
     * @param pageSize
     * @return
     */
    public Page<AbstractSMS> findByCarrier(LocalDate dateOne, LocalDate dateTwo, List<String> list_sid, String carrierCharCode, int page, int pageSize) {

        /* DATE */
        Date dateStart = ODateUitls.valueOf(dateOne);
        Date dateEnd = null;
        try {
            dateEnd = ODateUitls.parseToDateTo(dateTwo);
        } catch (ParseException e) {
            log.error("Error parsing date " + dateEnd);
            throw new UserFriendlyDataException("Error con la fecha final");
        }

        Pageable paging = PageRequest.of(page, pageSize);
        return monthRepos[dateOne.getMonthValue() - 1].findByDateBetweenAndSystemIdInAndCarrierCharCode(
                dateStart,
                dateEnd,
                list_sid, carrierCharCode,
                paging);

    }

    public Page<AbstractSMS> findByCarrierAndMessageType(int year, int month, Collection<String> list_sid,
                                                         String carrierCharCode, Collection<String> messageType) {
        return findByCarrierAndMessageType(year, month, list_sid, carrierCharCode, messageType, 0);
    }

    @Deprecated
    public Page<AbstractSMS> findByCarrierAndMessageType(int year, int month, Collection<String> list_sid,
                                                         String carrierCharCode, Collection<String> messageType, int page) {
        try {
            /* DATE */
            Date dateStart = parseYearMonthToDate(year, month);
            Date dateEnd = parseYearMonthToDate(year, MainView.localDateTime.getMonth().plus(1).getValue());

            Pageable paging = PageRequest.of(page, pageSize);
            return monthRepos[month - 1].findByDateBetweenAndSystemIdInAndCarrierCharCodeAndMessageTypeIn(
                    dateStart,
                    dateEnd,
                    list_sid,
                    carrierCharCode,
                    messageType,
                    paging);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Usado para los casos: #12, #14.
     *
     * @param dateOne
     * @param dateTwo
     * @param list_sid
     * @param carrierCharCode
     * @param messageType
     * @param page
     * @param pageSize
     * @return
     */
    public Page<AbstractSMS> findByCarrierAndMessageType(LocalDate dateOne, LocalDate dateTwo, Collection<String> list_sid,
                                                         String carrierCharCode, Collection<String> messageType, int page, int pageSize) {
        /* DATE */
        Date dateStart = ODateUitls.valueOf(dateOne);
        Date dateEnd = null;
        try {
            dateEnd = ODateUitls.parseToDateTo(dateTwo);
        } catch (ParseException e) {
            log.error("Error parsing date " + dateEnd);
            throw new UserFriendlyDataException("Error con la fecha final");
        }

        Pageable paging = PageRequest.of(page, pageSize, Sort.by("date"));
        return monthRepos[dateOne.getMonthValue() - 1].findByDateBetweenAndSystemIdInAndCarrierCharCodeAndMessageTypeIn(
                dateStart,
                dateEnd,
                list_sid,
                carrierCharCode,
                messageType,
                paging);
    }

    public List<AbstractSMS> getAllMessages(int year, int month, int day, List<String> systemIds) {
        return getAllMessages(year, month, day, systemIds, 0);
    }

    /**
     * Usado para el caso #0; Se buscan todos los sms.
     *
     * @param dateOne
     * @param dateTwo
     * @param systemIds
     * @param page
     * @param pageSize
     * @return
     */
    public Page<AbstractSMS> getAllMessages(LocalDate dateOne, LocalDate dateTwo, List<String> systemIds, int page, int pageSize) {
        /* DATE */
        Date dateStart = ODateUitls.valueOf(dateOne);
        Date dateEnd = null;
        try {
            dateEnd = ODateUitls.parseToDateTo(dateTwo);
        } catch (ParseException e) {
            log.error("Error parsing date " + dateEnd);
            throw new UserFriendlyDataException("Error con la fecha final");
        }

        Pageable paging = PageRequest.of(page, pageSize);
        Page<AbstractSMS> p = monthRepos[dateOne.getMonthValue() - 1].findByDateBetweenAndSystemIdIn(dateStart,
                dateEnd,
                systemIds,
                paging);
        return p;
    }

    public List<AbstractSMS> getAllMessages(int year, int month, int day, List<String> systemIds, int page) {
        List<AbstractSMS> list = new ArrayList<>();

        try {
            /* DATE */
            System.out.println("A convertir: " + year + " " + month + " " + day);
            Date dateStart = parseYearMonthDayToDate(year, month, day);
            LocalDateTime t = ODateUitls.valueOf(dateStart).plusDays(1);
            Date dateEnd = parseYearMonthDayHourToDate(t.getYear(), t.getMonthValue(), t.getDayOfMonth(), t.getHour());

            Pageable paging = PageRequest.of(page, pageSize);
            Page<AbstractSMS> p = monthRepos[month - 1].findByDateBetweenAndSystemIdIn(dateStart,
                    dateEnd,
                    systemIds,
                    paging);
            System.out.println("Ordenado " + p);
            list.addAll(p.toList());
            return list;

        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<AbstractSMS> getAllMessages(int year, int month, int day, int hour, List<String> systemIds) {
        return getAllMessages(year, month, day, hour, systemIds, 0);
    }

    /**
     * Devuelve todos los mensajes de una hora dada.
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param systemIds
     * @return
     */
    public List<AbstractSMS> getAllMessages(int year, int month, int day, int hour, List<String> systemIds, int page) {
        List<AbstractSMS> newList = new ArrayList<>();
        Date dateStart = null;
        Date dateEnd = null;
        Pageable paging = PageRequest.of(page, pageSize);
        try {
            /* DATE */
            System.out.println("A convertir: " + year + " " + month + " " + day + " " + hour);
            dateStart = parseYearMonthDayHourToDate(year, month, day, hour);
            LocalDateTime t = ODateUitls.valueOf(dateStart).plusHours(1);
            dateEnd = parseYearMonthDayHourToDate(t.getYear(), t.getMonthValue(), t.getDayOfMonth(), t.getHour());

        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        Page<AbstractSMS> msgList = monthRepos[month - 1].findByDateBetweenAndSystemIdIn(dateStart,
                dateEnd,
                systemIds,
                paging);
        newList = msgList.getContent();
        return newList;
    }

    public List<AbstractSMS> getAllMessages(int year, int month, int day, int hour, String messageType, List<String> systemIds) {
        return getAllMessages(year, month, day, hour, messageType, systemIds, 0);
    }

    public List<AbstractSMS> getAllMessages(int year, int month, int day, int hour, String messageType, List<String> systemIds, int page) {
        List<AbstractSMS> newList = new ArrayList<>();
        Date dateStart = null;
        Date dateEnd = null;
        Pageable paging = PageRequest.of(page, pageSize);
        try {
            /* DATE */
            dateStart = parseYearMonthDayHourToDate(year, month, day, hour);
            LocalDateTime t = MainView.localDateTime.plusHours(1);
            dateEnd = parseYearMonthDayHourToDate(t.getYear(), t.getMonthValue(), t.getDayOfMonth(), t.getHour());

        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        Page<AbstractSMS> msgList = monthRepos[month - 1].findByDateBetweenAndSystemIdInAndMessageTypeIn(dateStart,
                dateEnd,
                systemIds,
                Arrays.asList(messageType),
                paging);
        newList = msgList.getContent();
        return newList;
    }

    public List<AbstractSMS> getAllFilteredMessages(int year, int month, int day,
                                                    int hour, List<String> systemIds, String systemId, String messagesText,
                                                    String messageType, String iso2, String carrierCharCode, String source,
                                                    String destination, String msgSended, String msgReceived) {

        return getAllFilteredMessages(year, month, day, hour, systemIds, systemId, messagesText,
                messageType, iso2, carrierCharCode, source, destination, msgSended, msgReceived, 0);
    }

    public List<AbstractSMS> getAllFilteredMessages(int year, int month, int day,
                                                    int hour, List<String> systemIds, String systemId, String messagesText,
                                                    String messageType, String iso2, String carrierCharCode, String source,
                                                    String destination, String msgSended, String msgReceived, int page) {

        List<AbstractSMS> newList = new ArrayList<>();

        Date dateStart = null;
        Date dateEnd = null;
        Pageable paging = PageRequest.of(page, pageSize);
        try {
            /* DATE */
            System.out.println("A convertir: " + year + " " + month + " " + day + " " + hour);
            dateStart = parseYearMonthDayHourToDate(year, month, day, hour);
            LocalDateTime t = ODateUitls.valueOf(dateStart).plusHours(1);
            dateEnd = parseYearMonthDayHourToDate(t.getYear(), t.getMonthValue(), t.getDayOfMonth(), t.getHour());

        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(AbstractSmsService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        Page<AbstractSMS> msgList = monthRepos[month - 1].findByDateBetweenAndSystemIdInAndSystemIdLikeAndMessagesTextLikeAndMessageTypeLikeAndIso2LikeAndCarrierCharCodeLikeAndSourceLikeAndDestinationLikeAndMsgSendedLikeAndMsgReceivedLike(dateStart,
                dateEnd,
                systemIds,
                systemId,
                messagesText,
                messageType,
                iso2,
                carrierCharCode,
                source,
                destination,
                msgSended,
                msgReceived,
                paging);
        newList = msgList.getContent();
        return newList;
    }

    /**
     * devuelve una fecha con year y month y con dia 01.
     *
     * @param year
     * @param month
     * @return
     * @throws ParseException
     */
    public static synchronized Date parseYearMonthToDate(int year, int month) throws ParseException {
        return ODateUitls.dd_MM_yyyy.parse("01/" + month + "/" + year);
    }

    /**
     * devuelve una fecha con year, month y day.
     *
     * @param year
     * @param month
     * @param day
     * @return
     * @throws ParseException
     */
    public static synchronized Date parseYearMonthDayToDate(int year, int month, int day) throws ParseException {
        return ODateUitls.dd_MM_yyyy.parse(day + "/" + month + "/" + year);
    }

    /**
     * devuelve una fecha con year, month y day.
     *
     * @param year
     * @param month
     * @param day
     * @return
     * @throws ParseException
     */
    public static synchronized Date parseYearMonthDayHourToDate(int year, int month, int day, int hour) throws ParseException {
        return ODateUitls.dd_MM_yyyy_HH_mm_SS.parse(day + "/" + month + "/" + year + " " + hour + ":00:00");
    }
}

