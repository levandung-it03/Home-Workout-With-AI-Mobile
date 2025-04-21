package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;
import static com.restproject.mobile.adapters.HomeDialogSessionListAdapter.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeDetailSchedule implements Serializable {
    private Schedule schedule;
    private Subscription subscription;
    private Double TDEE;
    private List<SessionInItemLayout> sessions;

    public HomeDetailSchedule() {
    }

    public HomeDetailSchedule(Schedule schedule, Subscription subscription,
                              Double TDEE, List<SessionInItemLayout> sessions) {
        this.schedule = schedule;
        this.subscription = subscription;
        this.TDEE = TDEE;
        this.sessions = sessions;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Double getTDEE() {
        return TDEE;
    }

    public void setTDEE(Double TDEE) {
        this.TDEE = TDEE;
    }

    public List<SessionInItemLayout> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionInItemLayout> sessions) {
        this.sessions = sessions;
    }

    public static HomeDetailSchedule mapping(LinkedTreeMap data) {
        HomeDetailSchedule result = new HomeDetailSchedule();
        if (data.containsKey("schedule") && Objects.nonNull(data.get("schedule")))
            result.setSchedule(Schedule.mapping((LinkedTreeMap) data.get("schedule")));
        if (data.containsKey("subscription") && Objects.nonNull(data.get("subscription")))
            result.setSubscription(Subscription.mapping((LinkedTreeMap) data.get("subscription")));
        if (data.containsKey("TDEE") && Objects.nonNull(data.get("TDEE")))
            result.setTDEE(Double.parseDouble(data.get("TDEE").toString()));
        if (data.containsKey("sessions") && Objects.nonNull(data.get("sessions"))) {
            var sessions = (List<LinkedTreeMap>) data.get("sessions");
            result.setSessions(sessions.stream().map(SessionInItemLayout::mapping)
                .collect(Collectors.toList()));
        }
        return result;
    }
}
