package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class Session implements Serializable {
    private Long sessionId;
    private String name;
    private String levelEnum;
    private String description;
    private Integer switchExerciseDelay;
    private List<LinkedTreeMap> muscles;
    private String musclesStr;

    public Session() {
    }

    public Session(Long sessionId, String name, String levelEnum, String description,
                   Integer switchExerciseDelay, List<LinkedTreeMap> muscles) {
        this.sessionId = sessionId;
        this.name = name;
        this.levelEnum = levelEnum;
        this.description = description;
        this.switchExerciseDelay = switchExerciseDelay;
        this.muscles = muscles;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevelEnum() {
        return levelEnum;
    }

    public void setLevelEnum(String levelEnum) {
        this.levelEnum = levelEnum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSwitchExerciseDelay() {
        return switchExerciseDelay;
    }

    public void setSwitchExerciseDelay(Integer switchExerciseDelay) {
        this.switchExerciseDelay = switchExerciseDelay;
    }

    public List<LinkedTreeMap> getMuscles() {
        return muscles;
    }

    public void setMuscles(List<LinkedTreeMap> muscles) {
        this.muscles = muscles;
    }

    public String getMusclesStr() {
        return musclesStr;
    }

    public void setMusclesStr(String musclesStr) {
        this.musclesStr = musclesStr;
    }

    /**
     * Used to map data after JSON's converted to LinkedTreeMap (by Gson).
     * @param data:LinkedTreeMap
     * @return result:Session
     */
    public static Session mapping(LinkedTreeMap data) {
        Session result = new Session();
        if (data.containsKey("sessionId") && Objects.nonNull(data.get("sessionId")))
            result.setSessionId((long) Double.parseDouble(data.get("sessionId").toString()));
        if (data.containsKey("name") && Objects.nonNull(data.get("name")))
            result.setName(data.get("name").toString());
        if (data.containsKey("levelEnum") && Objects.nonNull(data.get("levelEnum")))
            result.setLevelEnum(data.get("levelEnum").toString());
        if (data.containsKey("description") && Objects.nonNull(data.get("description")))
            result.setDescription(data.get("description").toString());
        if (data.containsKey("switchExerciseDelay") && Objects.nonNull(data.get("switchExerciseDelay")))
            result.setSwitchExerciseDelay((int) Double.parseDouble(data.get("switchExerciseDelay").toString()));
        if (data.containsKey("muscles") && Objects.nonNull(data.get("muscles"))) {
            result.setMuscles((List<LinkedTreeMap>) data.get("muscles"));
            result.setMusclesStr(result.getMuscles().stream()
                .map(map -> map.get("muscleName").toString())
                .collect(Collectors.joining(" - ")));
        }
        return result;
    }
}
