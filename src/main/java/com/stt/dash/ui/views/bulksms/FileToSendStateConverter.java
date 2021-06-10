package com.stt.dash.ui.views.bulksms;

import com.stt.dash.backend.data.OrderState;
import com.stt.dash.backend.data.Status;
import com.stt.dash.ui.dataproviders.DataProviderUtil;
import com.vaadin.flow.templatemodel.ModelEncoder;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileToSendStateConverter implements ModelEncoder<Status, String> {

    private Map<String, Status> values;

    public FileToSendStateConverter(){
        values = Arrays.stream(Status.values()).collect(Collectors.toMap(Status::toString,
                Function.identity()));

    }
    @Override
    public String encode(Status modelValue) {
        return DataProviderUtil.convertIfNotNull(modelValue, Status::toString);
    }

    @Override
    public Status decode(String s) {
        return null;
    }
}
