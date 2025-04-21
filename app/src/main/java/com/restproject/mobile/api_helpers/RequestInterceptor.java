package com.restproject.mobile.api_helpers;

import android.content.Context;

import com.restproject.mobile.exception.ApplicationException;
import static com.restproject.mobile.storage_helpers.InternalStorageHelper.*;

import java.util.HashMap;

/**
 * [?] HOW TO USE IT
Volley.newRequestQueue(context.getApplicationContext()).add(new JsonObjectRequest(
         Request.Method.[GET, POST, PUT, DELETE],
         "ht_tp://localhost:0000/my-url",
         JSONObject jsonRequest || null,
         success -> { //--Do something when success },
         error -> { //--Do something when error }
) {
         @Override
         public Map<String, String> getHeaders() {
             return RequestInterceptor.[getPublicHeaders(),...];
         }
});
 */
public class RequestInterceptor {

    public static HashMap<String, String> getPublicHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        return headers;
    }

    public static HashMap<String, String> getPrivateAuthHeaders(Context context)
            throws ApplicationException {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        var refTok = readIS(context, "tokens.txt").split(";")[0];
        headers.put("Authorization", "Bearer " + refTok);
        return headers;
    }

    public static HashMap<String, String> getPrivateHeaders(Context context)
            throws ApplicationException {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        var accTok = readIS(context, "tokens.txt").split(";")[1];
        headers.put("Authorization", "Bearer " + accTok);
        return headers;
    }

    //--Overload
    public static HashMap<String, String> getPrivateHeaders(Context context, String contType)
            throws ApplicationException {
        HashMap<String, String> headers = getPrivateHeaders(context);
        headers.put("Content-type", contType);
        return headers;
    }

}
