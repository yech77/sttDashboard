package com.stt.dash.ui;

import com.stt.dash.app.OMessageType;
import com.stt.dash.backend.data.SmsByYearMonth;
import com.stt.dash.backend.data.entity.Carrier;
import com.stt.dash.backend.service.SmsHourService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MonthlySmsShowGridPresenter extends SmsShowGridPresenter<SmsByYearMonth> {
    private List<String> messageType = new ArrayList<>();

    public MonthlySmsShowGridPresenter(SmsHourService smsHourService, List<Integer> monthToShowList, List<String> stringListGenericBean, Viewnable<SmsByYearMonth> view) {
        super(smsHourService, monthToShowList, stringListGenericBean, view);
        List<SmsByYearMonth>
                monthToShowDataList = getGroupSmsBy(stringListGenericBean, monthToShowList);
        Calendar c = Calendar.getInstance();
        /* INcluir los MONTH faltantes con 0 */
        /* Recorro la lista por cada tipo de mensaje */
        messageType.forEach(smstype -> {
            /* Recorro la lista por cada mes */
            monthToShowList.forEach(monthRunner -> {
                /* Busco en toda la lista */
                Optional<SmsByYearMonth> first = monthToShowDataList
                        .stream()
                        .filter(sms -> {
                            return sms.getGroupBy() == monthRunner &&
                                    sms.getMessageType().equalsIgnoreCase(smstype);
                        })
                        .findFirst();
                /* si la combinacion mes y tipo de mensaje no existe se crea con 0 */
                if (!first.isPresent()) {
                    monthToShowDataList.add(new SmsByYearMonth(0, 2022, monthRunner, smstype));
                }
            });
        });
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    public MonthlySmsShowGridPresenter(SmsHourService smsHourService, Integer monthToShow, List<String> stringListGenericBean, List<String> messageType, Viewnable<SmsByYearMonth> view) {
        super(smsHourService, view);
        this.messageType = messageType;
        List<SmsByYearMonth>
                monthToShowDataList = getGroupSmsBy(stringListGenericBean, monthToShow);
        /* Incluir los SystemID que no estan en la lista con 0 */
        /* Recorro la lista por cada tipo de mensaje */
        messageType.forEach(smstype -> {
            stringListGenericBean.forEach(systemid -> {
                        /* Busco en toda la lista */
                        Optional<SmsByYearMonth> first = monthToShowDataList
                                .stream()
                                .filter(sms -> {
                                    return sms.getSomeCode().equalsIgnoreCase(systemid) &&
                                            sms.getMessageType().equalsIgnoreCase(smstype);
                                })
                                .findFirst();
                        /* si la combinacion systemid y tipo de mensaje no existe se crea con 0 */
                        if (!first.isPresent()) {
                            monthToShowDataList.add(new SmsByYearMonth(0, 2022, monthToShow, systemid, smstype));
                        }
                    }
            );
        });
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    public MonthlySmsShowGridPresenter(SmsHourService smsHourService, Integer actualYear, List<Integer> monthToShowList, List<String> systemidList, Set<OMessageType> messageTypeSet, Set<Carrier> carrierSet, Viewnable<SmsByYearMonth> view) {
        super(smsHourService, view);
        this.messageType = messageType;
        List<SmsByYearMonth>
                monthToShowDataList = getGroupSmsBy(actualYear, monthToShowList, systemidList, messageTypeSet, carrierSet);
//        /* Incluir los Carrier que no estan en la lista con 0 */
//        /* Recorro la lista por cada tipo de mensaje */
//        messageType.forEach(smstype -> {
//            carrierSet.forEach(carrier -> {
//                        /* Busco en toda la lista */
//                        Optional<SmsByYearMonth> first = monthToShowDataList
//                                .stream()
//                                .filter(sms -> {
//                                    return sms.getSomeCode().equalsIgnoreCase(carrier.getCarrierCharcode()) &&
//                                            sms.getMessageType().equalsIgnoreCase(smstype);
//                                })
//                                .findFirst();
//                        /* si la combinacion systemid y tipo de mensaje no existe se crea con 0 */
//                        if (!first.isPresent()) {
//                            monthToShowDataList.add(new SmsByYearMonth(0, 2022, monthToShow, carrier, smstype));
//                        }
//                    }
//            );
//        });
        updateDataProvider(monthToShowDataList);
        updateInView(dataProvider);
    }

    @Override
    public List<SmsByYearMonth> getGroupSmsBy(List<String> systemidList, List<Integer> monthList) {
        return smsHourService.groupSmsMessageTypeByYeMoWhYeMoInSyIn(2022, monthList, systemidList);
    }

    @Override
    public List<SmsByYearMonth> getGroupSmsBy(List<String> stringList, Integer month) {
        return smsHourService.groupYeMoSyTyWhYeMoInSyInTyIn(2022, month, messageType, stringList);
    }

    public List<SmsByYearMonth> getGroupSmsBy(Integer actualYear, List<Integer> monthToShowList, List<String> systemidList, Set<OMessageType> messageTypeSet, Set<Carrier> carrierSet) {
        return smsHourService.groupSmsCarrierAndMessageTypeByYeMoWhYeMoSyIn_CarrierInTyIn(
                actualYear,
                monthToShowList,
                carrierSet,
                messageTypeSet,
                systemidList);
    }
}
