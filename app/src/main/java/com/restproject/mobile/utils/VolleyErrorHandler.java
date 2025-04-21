package com.restproject.mobile.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.activities.RequestEnums;

import org.json.JSONObject;

public class VolleyErrorHandler {
    private AppCompatActivity activity;
    private Context context;
    private PrivateUIObject app;
    private JSONObject requestData;
    private RequestEnums requestEnum;
    private VolleyError error;

    public VolleyErrorHandler() {
        super();
    }

    public static VolleyErrorHandler builder() {
        return new VolleyErrorHandler();
    }

    public VolleyErrorHandler activity(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    public VolleyErrorHandler context(Context context) {
        this.context = context;
        return this;
    }

    public VolleyErrorHandler app(PrivateUIObject app) {
        this.app = app;
        return this;
    }

    public VolleyErrorHandler requestData(JSONObject requestData) {
        this.requestData = requestData;
        return this;
    }

    public VolleyErrorHandler requestEnum(RequestEnums requestEnum) {
        this.requestEnum = requestEnum;
        return this;
    }

    public VolleyErrorHandler error(VolleyError error) {
        this.error = error;
        return this;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PrivateUIObject getApp() {
        return app;
    }

    public void setApp(PrivateUIObject app) {
        this.app = app;
    }

    public JSONObject getRequestData() {
        return requestData;
    }

    public void setRequestData(JSONObject requestData) {
        this.requestData = requestData;
    }

    public RequestEnums getRequestEnum() {
        return requestEnum;
    }

    public void setRequestEnum(RequestEnums requestEnum) {
        this.requestEnum = requestEnum;
    }

    public VolleyError getError() {
        return error;
    }

    public void setError(VolleyError error) {
        this.error = error;
    }
}
