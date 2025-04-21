package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Schedule implements Serializable {
    private Long scheduleId;
    private String name;
    private String description;
    private String levelEnum;
    private Long coins;

    public Schedule() {
    }

    public Schedule(Long scheduleId, String name, String description, String levelEnum, Long coins) {
        this.scheduleId = scheduleId;
        this.name = name;
        this.description = description;
        this.levelEnum = levelEnum;
        this.coins = coins;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevelEnum() {
        return levelEnum;
    }

    public void setLevelEnum(String levelEnum) {
        this.levelEnum = levelEnum;
    }

    public Long getCoins() {
        return coins;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    /**
     * Used to map data after JSON's converted to LinkedTreeMap (by Gson).
     * @param data:LinkedTreeMap
     * @return result:Schedule
     */
    public static Schedule mapping(LinkedTreeMap data) {
        Schedule schedule = new Schedule();
        if (data.containsKey("scheduleId") && Objects.nonNull(data.get("scheduleId")))
            schedule.setScheduleId((long) Double.parseDouble(data.get("scheduleId").toString()));
        if (data.containsKey("name") && Objects.nonNull(data.get("name")))
            schedule.setName(data.get("name").toString());
        if (data.containsKey("description") && Objects.nonNull(data.get("description")))
            schedule.setDescription(data.get("description").toString());
        if (data.containsKey("levelEnum") && Objects.nonNull(data.get("levelEnum")))
            schedule.setLevelEnum(data.get("levelEnum").toString());
        if (data.containsKey("coins") && Objects.nonNull(data.get("coins")))
            schedule.setCoins((long) Double.parseDouble(data.get("coins").toString()));
        return schedule;
    }
}
