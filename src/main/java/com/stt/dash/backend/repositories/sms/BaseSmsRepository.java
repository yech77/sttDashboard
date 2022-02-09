package com.stt.dash.backend.repositories.sms;

import com.stt.dash.backend.data.entity.sms.AprSms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface BaseSmsRepository<T, ID> extends JpaRepository<T, ID> {

    @Override
    public Page<T> findAll(Pageable pageable);

    /**
     * Buscar por SystemId
     *
     * @param date1
     * @param date2
     * @param list_sid
     * @param pageable
     * @return
     */
    public Page<T> findByDateBetweenAndSystemIdIn(Date date1,
                                                  Date date2,
                                                  List<String> list_sid,
                                                  Pageable pageable);

    /**
     * Buscar por SystemId
     *
     * @param date1
     * @param date2
     * @param list_sid
     * @param sort
     * @return
     */
    public List<T> findByDateBetweenAndSystemIdIn(Date date1,
                                                  Date date2,
                                                  List<String> list_sid,
                                                  Sort sort);

    /**
     * Buscar por MessageType
     *
     * @param date1
     * @param date2
     * @param list_sid
     * @param messageTypeSms
     * @param pageable
     * @return
     */
    public Page<T> findByDateBetweenAndSystemIdInAndMessageTypeIn(Date date1,
                                                                  Date date2,
                                                                  Collection<String> list_sid,
                                                                  Collection<String> messageTypeSms,
                                                                  Pageable pageable);

    /**
     * Buscar numero de destino.
     *
     * @param date1
     * @param date2
     * @param list_sid
     * @param destination
     * @param messageTypeSms
     * @param pageable
     * @return
     */
    public Page<T> findByDateBetweenAndSystemIdInAndDestinationAndMessageTypeIn(Date date1,
                                                                                Date date2,
                                                                                List<String> list_sid,
                                                                                String destination,
                                                                                Collection<String> messageTypeSms,
                                                                                Pageable pageable);

    /**
     * Buscar numero de destino entre tipo de mensajes
     *
     * @param date1
     * @param date2
     * @param list_sid
     * @param destination
     * @param pageable
     * @return
     */
    public Page<T> findByDateBetweenAndSystemIdInAndDestination(Date date1,
                                                                Date date2,
                                                                List<String> list_sid,
                                                                String destination,
                                                                Pageable pageable);


    /**
     * @param date1
     * @param date2
     * @param list_sid
     * @param destination
     * @param messageTypeSms
     * @param carrierCharCode
     * @param pageable
     * @return
     */
    public Page<T> findByDateBetweenAndSystemIdInAndDestinationAndMessageTypeInAndCarrierCharCode(
            Date date1,
            Date date2,
            Collection<String> list_sid,
            String destination,
            Collection<String> messageTypeSms,
            String carrierCharCode,
            Pageable pageable);

    Page<T> findByDateBetweenAndSystemIdInAndMessageTypeInAndCarrierCharCode(
            Date date1,
            Date date2,
            Collection<String> list_sid,
            Collection<String> messageTypeSms,
            String carrierCharCode,
            Pageable pageable);

    /**
     * Buscar por Carrier
     *
     * @param date1
     * @param date2
     * @param list_sid
     * @param carrierCharCode
     * @param pageable
     * @return
     */
    public Page<T> findByDateBetweenAndSystemIdInAndCarrierCharCode(Date date1,
                                                                    Date date2,
                                                                    Collection<String> list_sid,
                                                                    String carrierCharCode,
                                                                    Pageable pageable);

    /**
     * Buscar por Carrier, MessageType
     *
     * @param date1
     * @param date2
     * @param list_sid
     * @param carrierCharCode
     * @param messageTypeSms
     * @param pageable
     * @return
     */
    public Page<T> findByDateBetweenAndSystemIdInAndCarrierCharCodeAndMessageTypeIn(Date date1,
                                                                                    Date date2,
                                                                                    Collection<String> list_sid,
                                                                                    String carrierCharCode,
                                                                                    Collection<String> messageTypeSms,
                                                                                    Pageable pageable);

    public Page<T> findByDateBetweenAndSystemIdInAndSystemIdLikeAndMessagesTextLikeAndMessageTypeLikeAndIso2LikeAndCarrierCharCodeLikeAndSourceLikeAndDestinationLikeAndMsgSendedLikeAndMsgReceivedLike(
            Date date1,
            Date date2,
            List<String> list_sid,
            String systemId,
            String messagesText,
            String messageType,
            String iso2,
            String carrierCharCode,
            String source,
            String destination,
            String msgSended,
            String msgReceived,
            Pageable pageable);
}

