package com.restproject.mobile.models;

public class OptionItem {
    public int value;
    public String text;

    public OptionItem(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }
    public String getValueString(){

    return Integer.toString(value);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
