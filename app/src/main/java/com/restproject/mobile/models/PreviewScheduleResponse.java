package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

public class PreviewScheduleResponse implements Serializable {
    private Schedule schedule;
    private Integer totalSessions;
    private Boolean wasSubscribed;
    private List<PreviewSessionResponse> sessionsOfSchedules;
    private Integer totalExercises;

    public PreviewScheduleResponse() {}

    public PreviewScheduleResponse(Schedule schedule, Integer totalSessions, Boolean wasSubscribed,
                                   List<PreviewSessionResponse> sessionsOfSchedules) {
        this.schedule = schedule;
        this.totalSessions = totalSessions;
        this.wasSubscribed = wasSubscribed;
        this.sessionsOfSchedules = sessionsOfSchedules;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Boolean isWasSubscribed() {
        return wasSubscribed;
    }

    public void setWasSubscribed(Boolean wasSubscribed) {
        this.wasSubscribed = wasSubscribed;
    }

    public List<PreviewSessionResponse> getSessionsOfSchedules() {
        return sessionsOfSchedules;
    }

    public void setSessionsOfSchedules(List<PreviewSessionResponse> sessionsOfSchedules) {
        this.sessionsOfSchedules = sessionsOfSchedules;
    }

    public Boolean getWasSubscribed() {
        return wasSubscribed;
    }

    public Integer getTotalExercises() {
        return totalExercises;
    }

    public void setTotalExercises(Integer totalExercises) {
        this.totalExercises = totalExercises;
    }

    public static class PreviewSessionResponse {
        private Session session;
        private List<String> exerciseNames;

        public PreviewSessionResponse() {
        }

        public PreviewSessionResponse(Session session, List<String> exerciseNames) {
            this.session = session;
            this.exerciseNames = exerciseNames;
        }

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }

        public List<String> getExerciseNames() {
            return exerciseNames;
        }

        public void setExerciseNames(List<String> exerciseNames) {
            this.exerciseNames = exerciseNames;
        }

        public static PreviewSessionResponse mapping(LinkedTreeMap data) {
            PreviewSessionResponse result = new PreviewSessionResponse();
            if (data.containsKey("session") && Objects.nonNull(data.get("session")))
                result.setSession(Session.mapping((LinkedTreeMap) data.get("session")));
            if (data.containsKey("exerciseNames") && Objects.nonNull(data.get("exerciseNames")))
                result.setExerciseNames((List<String>) data.get("exerciseNames"));
            return result;
        }
    }

    public static PreviewScheduleResponse mapping(LinkedTreeMap data) {
        PreviewScheduleResponse result = new PreviewScheduleResponse();
        if (data.containsKey("schedule") && Objects.nonNull(data.get("schedule")))
            result.setSchedule(Schedule.mapping((LinkedTreeMap) data.get("schedule")));
        if (data.containsKey("totalSessions") && Objects.nonNull(data.get("totalSessions")))
            result.setTotalSessions((int) Double.parseDouble(data.get("totalSessions").toString()));
        if (data.containsKey("wasSubscribed") && Objects.nonNull(data.get("wasSubscribed")))
            result.setWasSubscribed(Boolean.parseBoolean(data.get("wasSubscribed").toString()));
        if (data.containsKey("sessionsOfSchedules") && Objects.nonNull(data.get("sessionsOfSchedules"))) {
            result.setSessionsOfSchedules(((List<LinkedTreeMap>) data.get("sessionsOfSchedules"))
                .stream().map(PreviewSessionResponse::mapping)
                .collect(Collectors.toList()));
            result.setTotalExercises(result.getSessionsOfSchedules().stream()
                .map(obj -> obj.getExerciseNames().size())
                .reduce(0, Integer::sum));
        }
        return result;
    }
}