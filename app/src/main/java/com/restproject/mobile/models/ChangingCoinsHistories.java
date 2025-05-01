package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;
import com.restproject.mobile.utils.DateTimeHelper;

import java.util.Objects;

public class ChangingCoinsHistories {
    private String changingCoinsHistoriesId;
    private UserInfo userInfo;
    private String description;
    private Long changingCoins;
    private String changingTime;
    private String changingCoinsType;

    public ChangingCoinsHistories() {
    }

    public ChangingCoinsHistories(String changingCoinsHistoriesId, UserInfo userInfo,
                                  String description, Long changingCoins, String changingTime,
                                  String changingCoinsType) {
        this.changingCoinsHistoriesId = changingCoinsHistoriesId;
        this.userInfo = userInfo;
        this.description = description;
        this.changingCoins = changingCoins;
        this.changingTime = changingTime;
        this.changingCoinsType = changingCoinsType;
    }

    public String getChangingCoinsHistoriesId() {
        return changingCoinsHistoriesId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getDescription() {
        return description;
    }

    public Long getChangingCoins() {
        return changingCoins;
    }

    public String getChangingTime() {
        return changingTime;
    }

    public String getChangingCoinsType() {
        return changingCoinsType;
    }

    public void setChangingCoinsHistoriesId(String changingCoinsHistoriesId) {
        this.changingCoinsHistoriesId = changingCoinsHistoriesId;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setChangingCoins(Long changingCoins) {
        this.changingCoins = changingCoins;
    }

    public void setChangingTime(String changingTime) {
        this.changingTime = changingTime;
    }

    public void setChangingCoinsType(String changingCoinsType) {
        this.changingCoinsType = changingCoinsType;
    }

    public static ChangingCoinsHistories mapping(LinkedTreeMap data) {
        ChangingCoinsHistories result = new ChangingCoinsHistories();
        if (data.containsKey("changingCoinsHistoriesId") && Objects.nonNull(data.get("changingCoinsHistoriesId")))
            result.setChangingCoinsHistoriesId(data.get("changingCoinsHistoriesId").toString());
        if (data.containsKey("userInfo") && Objects.nonNull(data.get("userInfo")))
            result.setUserInfo(UserInfo.mapping((LinkedTreeMap) data.get("userInfo")));
        if (data.containsKey("description") && Objects.nonNull(data.get("description")))
            result.setDescription(data.get("description").toString());
        if (data.containsKey("changingCoins") && Objects.nonNull(data.get("changingCoins")))
            result.setChangingCoins((long) Double.parseDouble(data.get("changingCoins").toString()));
        if (data.containsKey("changingCoinsType") && Objects.nonNull(data.get("changingCoinsType")))
            result.setChangingCoinsType(data.get("changingCoinsType").toString());
        if (data.containsKey("changingTime") && Objects.nonNull(data.get("changingTime")))
            result.setChangingTime(DateTimeHelper.formatDateTimeFromGson(
                data.get("changingTime").toString()));
        return result;
    }
}
