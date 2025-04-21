package com.restproject.mobile.utils;

import java.util.Map;

public class APIResponseWrapper {
    private Map<String, Object> headers;
    private APIResponseObject body;
    private String statusCode;
    private Integer statusCodeValue;

    public APIResponseWrapper() {
    }
    public APIResponseWrapper(Map<String, Object> headers, APIResponseObject body, String statusCode,
                              Integer statusCodeValue) {
        this.headers = headers;
        this.body = body;
        this.statusCode = statusCode;
        this.statusCodeValue = statusCodeValue;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public APIResponseObject getBody() {
        return body;
    }

    public void setBody(APIResponseObject body) {
        this.body = body;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStatusCodeValue() {
        return statusCodeValue;
    }

    public void setStatusCodeValue(Integer statusCodeValue) {
        this.statusCodeValue = statusCodeValue;
    }
}
