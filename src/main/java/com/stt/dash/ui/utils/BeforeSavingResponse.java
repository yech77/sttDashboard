package com.stt.dash.ui.utils;

public class BeforeSavingResponse {
    private boolean success;
    private String message;
    private Object data;
    private Class clazz;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setData(Object data, Class clazz) {
        this.data = data;
        this.clazz = clazz;
    }
}
