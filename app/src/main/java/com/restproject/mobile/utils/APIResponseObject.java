package com.restproject.mobile.utils;


import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class APIResponseObject<T> {
    private Integer applicationCode;
    private String message;
    private Integer httpStatusCode;
    private T data;
    private String responseTime;
    private String[] responseTimeArr;
    private String responseDateTime;


    public APIResponseObject() {}
    public APIResponseObject(String message) {
        this.message = message;
    }
    public APIResponseObject(Integer applicationCode, @NotNull String message, Integer httpStatusCode,
                             T data, String responseTime) {
        this.applicationCode = applicationCode;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
        this.data = data;
        this.responseTime = responseTime;
        this.mappingDateFromMultipleTypes(responseTime);
    }

    public void parseTimeToDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
            Integer.parseInt(this.responseTimeArr[0]),
            Integer.parseInt(this.responseTimeArr[1]),
            Integer.parseInt(this.responseTimeArr[2]),
            Integer.parseInt(this.responseTimeArr[3]),
            Integer.parseInt(this.responseTimeArr[4]),
            Integer.parseInt(this.responseTimeArr[5])
        );
        Date time = calendar.getTime();
        this.setResponseDateTime(DateTimeHelper.formatDateTimeToStr(time));
    }

    public Integer getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(Integer applicationCode) {
        this.applicationCode = applicationCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String[] getResponseTimeArr() {
        return responseTimeArr;
    }

    public void setResponseTimeArr(String[] responseTimeArr) {
        this.responseTimeArr = responseTimeArr;
    }

    public String getResponseDateTime() {
        return responseDateTime;
    }

    public void setResponseDateTime(String responseDateTime) {
        this.responseDateTime = responseDateTime;
    }

    public void mappingDateFromMultipleTypes(String responseTime) {
        if (responseTime.contains("[")) {
            this.responseTimeArr = new Gson().fromJson(responseTime, String[].class);
            this.parseTimeToDateTime();
        } else if (responseTime.contains(".")){
            var dateTime = new Date((long) Double.parseDouble(responseTime) * 1000);
            this.responseDateTime = DateTimeHelper.formatDateTimeToStr(dateTime);
        }
    }

    @Override
    public String toString() {
        return "APIResponseObject{" +
                "applicationCode=" + applicationCode +
                ", message='" + message + '\'' +
                ", httpStatusCode=" + httpStatusCode +
                ", data=" + data +
                ", responseTime=" + responseTime +
                ", responseDateTime='" + responseDateTime + '\'' +
                '}';
    }
}