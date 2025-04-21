package com.restproject.mobile.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Objects;

public class APIBuilderForGET {
    private final HashMap<String, Object> dataHolder;
    private String url = "";

    private APIBuilderForGET(String url) {
        this.dataHolder = new HashMap<String, Object>();
        this.url = url;
    }

    public static APIBuilderForGET builder(String url) {
        return new APIBuilderForGET(url);
    }

    public static String parseFromJsonObject(JSONObject jsonObject, String url) {
        StringBuilder result = new StringBuilder(url).append("?");
        jsonObject.keys().forEachRemaining(key -> {
            try {
                Object value = jsonObject.get(key);
                String encodedKey = URLEncoder.encode(key, "UTF-8");
                String encodedValue;

                if (value instanceof JSONArray) {
                    encodedValue = URLEncoder.encode(value.toString(), "UTF-8");
                } else {
                    encodedValue = URLEncoder.encode(String.valueOf(value), "UTF-8");
                }

                result.append(encodedKey).append("=").append(encodedValue).append("&");
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        });
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public APIBuilderForGET valuesPair(String key, Object data) {
        this.dataHolder.put(key, data);
        return this;
    }

    public String build() {
        StringBuilder result = new StringBuilder();
        if (!this.url.isEmpty()) result.append(url).append("?");

        for (String key : dataHolder.keySet())
            result
                .append(key).append("=")
                .append(Objects.requireNonNull(dataHolder.get(key)))
                .append("&");
        result.subSequence(0, result.lastIndexOf("&"));
        return result.toString();
    }
}
