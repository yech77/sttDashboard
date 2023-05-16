/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stt.dash.backend.util.ws;


import com.stt.dash.backend.data.entity.SystemId;

/**
 * @author yech
 */
public class SystemIdOResponse extends OResponse {

    private static final long serialVersionUID = -6257533609372619257L;
    private String system_id = "";

    private String password = "";

    private String systemid_status;

    private String payment_type;

    public SystemIdOResponse() {
    }

    public String getSystem_id() {
        return system_id;
    }

    public void setSystem_id(String system_id) {
        this.system_id = system_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    @Override
    public String toString() {
        return "SystemIdOResponse{" + "system_id=" + system_id + ", payment_type=" + payment_type + '}';
    }

    public String getSystemid_status() {
        return systemid_status;
    }

    public void setSystemid_status(String systemid_status) {
        this.systemid_status = systemid_status;
    }

}
