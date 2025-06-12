package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.Objects;

public class ExerciseDetailOfSession implements Serializable {
    private Long id;
    private Integer ordinal;
    private Integer iteration;
    private Double downRepsRatio;
    private Integer raiseSlackInSecond;
    private Integer slackInSecond;
    private Boolean needSwitchExerciseDelay;
    private Exercise exercise;

    public ExerciseDetailOfSession() {
    }

    public ExerciseDetailOfSession(Long id, Integer ordinal, Integer iteration, Double downRepsRatio,
                                   Integer raiseSlackInSecond, Integer slackInSecond,
                                   Boolean needSwitchExerciseDelay, Exercise exercise) {
        this.id = id;
        this.ordinal = ordinal;
        this.iteration = iteration;
        this.downRepsRatio = downRepsRatio;
        this.raiseSlackInSecond = raiseSlackInSecond;
        this.slackInSecond = slackInSecond;
        this.needSwitchExerciseDelay = needSwitchExerciseDelay;
        this.exercise = exercise;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Integer getIteration() {
        return iteration;
    }

    public void setIteration(Integer iteration) {
        this.iteration = iteration;
    }

    public Double getDownRepsRatio() {
        return downRepsRatio;
    }

    public void setDownRepsRatio(Double downRepsRatio) {
        this.downRepsRatio = downRepsRatio;
    }

    public Integer getRaiseSlackInSecond() {
        return raiseSlackInSecond;
    }

    public void setRaiseSlackInSecond(Integer raiseSlackInSecond) {
        this.raiseSlackInSecond = raiseSlackInSecond;
    }

    public Integer getSlackInSecond() {
        return slackInSecond;
    }

    public void setSlackInSecond(Integer slackInSecond) {
        this.slackInSecond = slackInSecond;
    }

    public Boolean getNeedSwitchExerciseDelay() {
        return needSwitchExerciseDelay;
    }

    public void setNeedSwitchExerciseDelay(Boolean needSwitchExerciseDelay) {
        this.needSwitchExerciseDelay = needSwitchExerciseDelay;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public static ExerciseDetailOfSession mapping(LinkedTreeMap data) {
        ExerciseDetailOfSession result = new ExerciseDetailOfSession();

        if (data.containsKey("id") && Objects.nonNull(data.get("id")))
            result.setId(((Double) data.get("id")).longValue());

        if (data.containsKey("ordinal") && Objects.nonNull(data.get("ordinal")))
            result.setOrdinal(((Double) data.get("ordinal")).intValue());

        if (data.containsKey("iteration") && Objects.nonNull(data.get("iteration")))
            result.setIteration(((Double) data.get("iteration")).intValue());

        if (data.containsKey("downRepsRatio") && Objects.nonNull(data.get("downRepsRatio")))
            result.setDownRepsRatio((Double) data.get("downRepsRatio"));

        if (data.containsKey("raiseSlackInSecond") && Objects.nonNull(data.get("raiseSlackInSecond")))
            result.setRaiseSlackInSecond(((Double) data.get("raiseSlackInSecond")).intValue());

        if (data.containsKey("slackInSecond") && Objects.nonNull(data.get("slackInSecond")))
            result.setSlackInSecond(((Double) data.get("slackInSecond")).intValue());

        if (data.containsKey("needSwitchExerciseDelay") && Objects.nonNull(data.get("needSwitchExerciseDelay")))
            result.setNeedSwitchExerciseDelay((Boolean) data.get("needSwitchExerciseDelay"));

        if (data.containsKey("exercise") && Objects.nonNull(data.get("exercise")))
            result.setExercise(Exercise.mapping((LinkedTreeMap) data.get("exercise")));

        return result;
    }
}


