package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class Subscription implements Serializable {
    private Long subscriptionId;
    private String completedTime;
    private Byte repRatio;
    private String aim;
    private Integer efficientDays;
    private Double bmr;
    private Float weightAim;
    private LinkedTreeMap changingCoinsHistories;

    public Subscription() {
    }

    public Subscription(Long subscriptionId, String completedTime,
                        Byte repRatio, String aim, Integer efficientDays, Double bmr,
                        Float weightAim, LinkedTreeMap changingCoinsHistories) {
        this.subscriptionId = subscriptionId;
        this.completedTime = completedTime;
        this.repRatio = repRatio;
        this.aim = aim;
        this.efficientDays = efficientDays;
        this.bmr = bmr;
        this.weightAim = weightAim;
        this.changingCoinsHistories = changingCoinsHistories;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(String completedTime) {
        this.completedTime = completedTime;
    }

    public Byte getRepRatio() {
        return repRatio;
    }

    public void setRepRatio(Byte repRatio) {
        this.repRatio = repRatio;
    }

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    public Integer getEfficientDays() {
        return efficientDays;
    }

    public void setEfficientDays(Integer efficientDays) {
        this.efficientDays = efficientDays;
    }

    public Double getBmr() {
        return bmr;
    }

    public void setBmr(Double bmr) {
        this.bmr = bmr;
    }

    public Float getWeightAim() {
        return weightAim;
    }

    public void setWeightAim(Float weightAim) {
        this.weightAim = weightAim;
    }

    public LinkedTreeMap getChangingCoinsHistories() {
        return changingCoinsHistories;
    }

    public void setChangingCoinsHistories(LinkedTreeMap changingCoinsHistories) {
        this.changingCoinsHistories = changingCoinsHistories;
    }

    /**
     * @param data:LinkedTreeMap
     * @return result:Subscription
     */
    public static Subscription mapping(LinkedTreeMap data) {
        Subscription result = new Subscription();
        if (data.containsKey("subscriptionId") && Objects.nonNull(data.get("subscriptionId")))
            result.setSubscriptionId((long) Double.parseDouble(data.get("subscriptionId").toString()));
        if (data.containsKey("completedTime") && Objects.nonNull(data.get("completedTime")))
            result.setCompletedTime(data.get("completedTime").toString());
        if (data.containsKey("repRatio") && Objects.nonNull(data.get("repRatio")))
            result.setRepRatio((byte) Double.parseDouble(data.get("repRatio").toString()));
        if (data.containsKey("aim") && Objects.nonNull(data.get("aim")))
            result.setAim(data.get("aim").toString());
        if (data.containsKey("efficientDays") && Objects.nonNull(data.get("efficientDays")))
            result.setEfficientDays((int) Double.parseDouble(data.get("efficientDays").toString()));
        if (data.containsKey("bmr") && Objects.nonNull(data.get("bmr")))
            result.setBmr(Double.parseDouble(data.get("bmr").toString()));
        if (data.containsKey("weightAim") && Objects.nonNull(data.get("weightAim")))
            result.setWeightAim(Float.parseFloat(data.get("weightAim").toString()));
        if (data.containsKey("changingCoinsHistories")
            && Objects.nonNull(data.get("changingCoinsHistories")))
            result.setChangingCoinsHistories((LinkedTreeMap) data.get("changingCoinsHistories"));
        return result;
    }
}
