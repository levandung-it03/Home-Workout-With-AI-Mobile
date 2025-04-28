package com.restproject.mobile.utils;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class APIResponseObject<T> {
    private Integer applicationCode;
    private String message;
    private Integer httpStatusCode;
    private T data;
    private String[] responseTime;
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
        this.mappingDateFromMultipleTypes(responseTime);
    }

    public void parseTimeToDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
            Integer.parseInt(this.responseTime[0]),
            Integer.parseInt(this.responseTime[1]),
            Integer.parseInt(this.responseTime[2]),
            Integer.parseInt(this.responseTime[3]),
            Integer.parseInt(this.responseTime[4]),
            Integer.parseInt(this.responseTime[5])
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

    public String[] getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String[] responseTime) {
        this.responseTime = responseTime;
    }

    public String getResponseDateTime() {
        return responseDateTime;
    }

    public void setResponseDateTime(String responseDateTime) {
        this.responseDateTime = responseDateTime;
    }

    public void mappingDateFromMultipleTypes(String responseDateTime) {
        if (responseDateTime.contains("[")) {
            this.responseTime = new Gson().fromJson(responseDateTime, String[].class);
            this.parseTimeToDateTime();
        } else {
            this.responseDateTime = responseDateTime;
        }

    }
    @Override
    public String toString() {
        return "APIResponseObject{" +
                "applicationCode=" + applicationCode +
                ", message='" + message + '\'' +
                ", httpStatusCode=" + httpStatusCode +
                ", data=" + data +
                ", responseTime=" + Arrays.toString(responseTime) +
                ", responseDateTime='" + responseDateTime + '\'' +
                '}';
    }
}