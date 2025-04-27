package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class UserInfo implements Serializable {
    private Long userInfoId;
    private String firstName;
    private String lastName;
    private String gender;
    private int[] dob;
    private Long coins;


    public UserInfo() {
    }

    public UserInfo(Long userInfoId, String firstName, String lastName, String gender, int[] dob, Long coins) {
        this.userInfoId = userInfoId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.coins = coins;
    }

    public Long getUserInfoId() {
        return userInfoId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public int[] getDob() {
        return dob;
    }

    public Long getCoins() {
        return coins;
    }

    public void setUserInfoId(Long userInfoId) {
        this.userInfoId = userInfoId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDob(int[] dob) {
        this.dob = dob;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public static UserInfo mapping(LinkedTreeMap data) {
        UserInfo result = new UserInfo();
        if (data.containsKey("userInfoId") && Objects.nonNull(data.get("userInfoId")))
            result.setUserInfoId((long) Double.parseDouble(data.get("userInfoId").toString()));
        if (data.containsKey("firstName") && Objects.nonNull(data.get("firstName")))
            result.setFirstName(data.get("firstName").toString());
        if (data.containsKey("lastName") && Objects.nonNull(data.get("lastName")))
            result.setLastName(data.get("lastName").toString());
        if (data.containsKey("gender") && Objects.nonNull(data.get("gender")))
            result.setGender(data.get("gender").toString());
        if (data.containsKey("gender") && Objects.nonNull(data.get("gender")))
            result.setGender(data.get("gender").toString());
        if (data.containsKey("dob") && Objects.nonNull(data.get("dob"))) {
            var respondedDob = (ArrayList<Object>) data.get("dob");
            result.setDob(respondedDob.stream()
                .mapToInt(num -> (int) Double.parseDouble(num.toString()))
                .toArray());
        }
        if (data.containsKey("coins") && Objects.nonNull(data.get("coins")))
            result.setCoins((long) Double.parseDouble(data.get("coins").toString()));
        return result;
    }
}
