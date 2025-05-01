package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.Objects;

public class SepayQRResponse {
    private String url;
    private String accountTarget;
    private String bankName;
    private String description;

    public SepayQRResponse() {
    }

    public SepayQRResponse(String url, String accountTarget, String bankName, String description) {
        this.url = url;
        this.accountTarget = accountTarget;
        this.bankName = bankName;
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAccountTarget(String accountTarget) {
        this.accountTarget = accountTarget;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public String getAccountTarget() {
        return accountTarget;
    }

    public String getBankName() {
        return bankName;
    }

    public String getDescription() {
        return description;
    }

    public static SepayQRResponse mapping(LinkedTreeMap data) {
        SepayQRResponse result = new SepayQRResponse();
        if (data.containsKey("url") && Objects.nonNull(data.get("url")))
            result.setUrl(data.get("url").toString());
        if (data.containsKey("accountTarget") && Objects.nonNull(data.get("accountTarget")))
            result.setAccountTarget(data.get("accountTarget").toString());
        if (data.containsKey("bankName") && Objects.nonNull(data.get("bankName")))
            result.setBankName(data.get("bankName").toString());
        if (data.containsKey("description") && Objects.nonNull(data.get("description")))
            result.setDescription(data.get("description").toString());
        return result;
    }
}
