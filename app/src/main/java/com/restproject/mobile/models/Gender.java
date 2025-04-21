package com.restproject.mobile.models;

import java.io.Serializable;

public class Gender implements Serializable {
    private Integer id;
    private String raw;

    public Gender() {
    }
    public Gender(Integer id, String raw) {
        this.id = id;
        this.raw = raw;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
