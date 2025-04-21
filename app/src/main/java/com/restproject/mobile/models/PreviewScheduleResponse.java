package com.restproject.mobile.models;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class PreviewScheduleResponse implements Serializable {
    private Schedule schedule;
    private int totalSessions;
    private boolean wasSubscribed;
    private Set<Map<String, Object>> sessionsOfSchedules;

    public PreviewScheduleResponse() {
    }

    public PreviewScheduleResponse(Schedule schedule, int totalSessions, boolean wasSubscribed,
                                   Set<Map<String, Object>> sessionsOfSchedules) {
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

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public boolean isWasSubscribed() {
        return wasSubscribed;
    }

    public void setWasSubscribed(boolean wasSubscribed) {
        this.wasSubscribed = wasSubscribed;
    }

    public Set<Map<String, Object>> getSessionsOfSchedules() {
        return sessionsOfSchedules;
    }

    public void setSessionsOfSchedules(Set<Map<String, Object>> sessionsOfSchedules) {
        this.sessionsOfSchedules = sessionsOfSchedules;
    }

}