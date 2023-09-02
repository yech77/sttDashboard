package com.stt.dash.utils.ws;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UtilDto {
    String noPass;
    List<String> alerts = new ArrayList<>();

    public boolean isExistAlerts() {
        return !alerts.isEmpty();
    }

    public boolean isExistNoPass() {
        return noPass != null;
    }

    public boolean isExistAlertsOrNoPass() {
        return isExistAlerts() || isExistNoPass();
    }
}
