package com.restproject.mobile.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.restproject.mobile.R;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.exception.ApplicationException;
import com.restproject.mobile.fragments.LoginFragment;
import com.restproject.mobile.storage_helpers.InternalStorageHelper;

import static com.restproject.mobile.storage_helpers.InternalStorageHelper.*;
import static com.restproject.mobile.BuildConfig.*;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class APIUtilsHelper {

    public static APIResponseObject<Void> mapVoidVolleySuccess(JSONObject response) {
        try {
            return new APIResponseObject<Void>(
                Integer.parseInt(response.get("applicationCode").toString()),
                response.get("message").toString(),
                Integer.parseInt(response.get("httpStatusCode").toString()),
                null,
                response.get("responseTime").toString()
            );
        } catch (Exception e) {
            return new APIResponseObject<>("Error from server to read response");
        }
    }

    public static APIResponseObject<List<LinkedTreeMap>> mapListVolleySuccess(JSONObject response) {
        try {
            return new APIResponseObject<>(
                Integer.parseInt(response.get("applicationCode").toString()),
                response.get("message").toString(),
                Integer.parseInt(response.get("httpStatusCode").toString()),
                new Gson().fromJson(response.get("data").toString(), List.class),
                response.get("responseTime").toString()
            );
        } catch (Exception e) {
            return new APIResponseObject<>("Error from server to read response");
        }
    }

    public static APIResponseObject<LinkedTreeMap> mapVolleySuccess(JSONObject response) {
        try {
            return new APIResponseObject<>(
                Integer.parseInt(response.get("applicationCode").toString()),
                response.get("message").toString(),
                Integer.parseInt(response.get("httpStatusCode").toString()),
                new Gson().fromJson(response.get("data").toString(), LinkedTreeMap.class),
                response.get("responseTime").toString()
            );
        } catch (Exception e) {
            return new APIResponseObject<>("Error from server to read response");
        }
    }

    public static APIResponseObject<LinkedTreeMap> mapVolleySuccessStringTime(JSONObject response) {
        try {
            return new APIResponseObject<>(
                    Integer.parseInt(response.get("applicationCode").toString()),
                    response.get("message").toString(),
                    Integer.parseInt(response.get("httpStatusCode").toString()),
                    new Gson().fromJson(response.get("data").toString(), LinkedTreeMap.class),
                    response.get("responseTime").toString()
            );
        } catch (Exception e) {
            return new APIResponseObject<>("Error from server to read response");
        }
    }

    public static APIResponseObject<String> mapSimpleVolleySuccess(JSONObject response) {
        try {
            return new APIResponseObject<>(
                Integer.parseInt(response.get("applicationCode").toString()),
                response.get("message").toString(),
                Integer.parseInt(response.get("httpStatusCode").toString()),
                response.get("data").toString(),
                response.get("responseTime").toString()
            );
        } catch (Exception e) {
            return new APIResponseObject<>("Error from server to read response");
        }
    }

    public static APIResponseObject<Void> readErrorFromVolley(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            String json = new String(error.networkResponse.data);
            try {
                APIResponseObject<Void> response = new Gson().fromJson(json, APIResponseWrapper.class).getBody();
                if (response.getMessage() == null)
                    throw new NullPointerException();
                return response;
            } catch (NullPointerException e) {
                APIResponseObject<Void> response = new Gson().fromJson(json, APIResponseObject.class);
                if (response.getMessage() == null)
                    throw new NullPointerException();
                return response;
            } catch (Exception e) {
                return new APIResponseObject<>("Error from server to read response");
            }
        } else {
            return new APIResponseObject<>("No network response from server");
        }
    }

    public static APIResponseObject<Void> readErrorFromVolleyStringTime(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            String json = new String(error.networkResponse.data);
            try {
                Log.e("VolleyError", "Server response: " + json);

                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

                int applicationCode = jsonObject.has("applicationCode") ? jsonObject.get("applicationCode").getAsInt() : -1;
                String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "Unknown error";
                int httpStatusCode = jsonObject.has("httpStatusCode") ? jsonObject.get("httpStatusCode").getAsInt() : -1;
                String responseDateTime = jsonObject.has("responseTime") ? jsonObject.get("responseTime").getAsString() : "Unknown time";

                // Dùng constructor thứ 3
                return new APIResponseObject<>(applicationCode, message, httpStatusCode, null, responseDateTime);

            } catch (NullPointerException e) {
                APIResponseObject<Void> response = new Gson().fromJson(json, APIResponseObject.class);
                if (response.getMessage() == null)
                    throw new NullPointerException();
                return response;
            } catch (Exception e) {
                return new APIResponseObject<>("Error from server to read response");
            }
        } else {
            return new APIResponseObject<>("No network response from server");
        }
    }

    /**
     * This method will handle Private Requests, and not disrupt the Session when reach another err.
     * @param info: Basic Info
     */
    public static void handleSpecialPrivateVolleyRequestError(VolleyErrorHandler info) {
        APIResponseObject<Void> response = APIUtilsHelper.readErrorFromVolley(info.getError());
        if (response.getApplicationCode() != null) {
            if (response.getApplicationCode().equals(EXPIRED_TKN_ERR_CODE)) {
                try {
                    String[] tokens = readIS(info.getContext(), "tokens.txt").split(",");
                    var jsonRequestObject = new JsonObjectRequest(
                        Request.Method.POST,
                        BACKEND_ENDPOINT + PRIVATE_AUTH_DIR + "/v1/refresh-token",
                        new JSONObject().put("token", tokens[1]),
                        success -> {
                            var res = APIUtilsHelper.mapVolleySuccess(success);
                            APIUtilsHelper.saveTokensAfterAuthentication(info.getContext(), Map.of(
                                "accessToken", Objects.requireNonNull(res.getData().get("token")).toString(),
                                "refreshToken", tokens[0]
                            ));
                            System.out.println("From refreshAccessToken(): " + res.getMessage());
                            info.getApp().recall(info.getRequestData(), info.getRequestEnum());
                        },
                        error -> {
                            var e = new Exception("Application is wrong, please restart it");
                            //--If Refresh-Token is also expired, inflate Login-Fragment.
                            if (readErrorFromVolley(error).getApplicationCode().equals(EXPIRED_TKN_ERR_CODE))
                                e = new Exception("Too long Login session, login in again!");
                            //--If something is wrong from Backend-System, inflate Login-Fragment.
                            clearInterStorageAndToast(e, info);
                        }
                    ) {
                        //--Change Headers to put Refresh-Token as Authorization Bearer.
                        @Override
                        public Map<String, String> getHeaders() {
                            return RequestInterceptor.getPrivateAuthHeaders(info.getContext());
                        }
                    };
                    //--Calling request.
                    Volley
                        .newRequestQueue(info.getContext())
                        .add(APIUtilsHelper.setVolleyRequestTimeOut(jsonRequestObject, 30_000));
                } catch (Exception e) {
                    clearInterStorageAndToast(e, info);
                }
            } else {
                Toast.makeText(info.getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            clearInterStorageAndToast(new Exception("Application is wrong, please restart it"), info);
        }
    }

    public static void handlePrivateVolleyRequestError(VolleyErrorHandler info) {
        APIResponseObject<Void> response = APIUtilsHelper.readErrorFromVolley(info.getError());
        if (response.getApplicationCode() != null && response.getApplicationCode().equals(EXPIRED_TKN_ERR_CODE)) {
            try {
                //--File Content: `refresh_token;access_token`
                String[] tokens = readIS(info.getContext(), "tokens.txt").split(",");
                var jsonRequestObject = new JsonObjectRequest(
                    Request.Method.POST,
                    BACKEND_ENDPOINT + PRIVATE_AUTH_DIR + "/v1/refresh-token",
                //--Put the old Access-Token for verification current-user.
                    new JSONObject().put("token", tokens[1]),
                //--Success Lambda function.
                    success -> {
                        var res = APIUtilsHelper.mapVolleySuccess(success);
                        APIUtilsHelper.saveTokensAfterAuthentication(info.getContext(), Map.of(
                            "accessToken", Objects.requireNonNull(res.getData().get("token")).toString(),
                            "refreshToken", tokens[0]
                        ));
                        System.out.println("From refreshAccessToken(): " + res.getMessage());
                        info.getApp().recall(info.getRequestData(), info.getRequestEnum());
                    },
                //--Error Lambda function.
                    error -> {
                        var e = new Exception("Application is wrong, please restart it");
                        //--If Refresh-Token is also expired, inflate Login-Fragment.
                        if (readErrorFromVolley(error).getApplicationCode().equals(EXPIRED_TKN_ERR_CODE))
                            e = new Exception("Too long Login session, login in again!");
                        //--If something is wrong from Backend-System, inflate Login-Fragment.
                        clearInterStorageAndToast(e, info);
                    }
                ) {
                //--Change Headers to put Refresh-Token as Authorization Bearer.
                    @Override
                    public Map<String, String> getHeaders() {
                        return RequestInterceptor.getPrivateAuthHeaders(info.getContext());
                    }
                };
                //--Calling request.
                Volley
                    .newRequestQueue(info.getContext())
                    .add(APIUtilsHelper.setVolleyRequestTimeOut(jsonRequestObject, 30_000));
            } catch (Exception e) {
                clearInterStorageAndToast(e, info);
            }
        } else {
            clearInterStorageAndToast(new Exception(response.getMessage()), info);
        }
    }

    public static void clearInterStorageAndToast(Exception e, VolleyErrorHandler info) {
        e.fillInStackTrace();
        Toast.makeText(info.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    //--If something is wrong in Mobile, inflate Login-Fragment.
        InternalStorageHelper.writeIS(info.getContext(), "tokens.txt", "");
        info.getActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.main, new LoginFragment())
            .commit();
    }

    public static boolean saveTokensAfterAuthentication(Context context, Map<String, Object> response) {
        try {
            String tokens = Objects.requireNonNull(response.get("refreshToken")) + ";"
                + Objects.requireNonNull(response.get("accessToken"));
            writeIS(context, "tokens.txt", tokens);
            return true;
        } catch (ApplicationException e) {
            System.out.println(e.getMessage());
            Toast.makeText(context, "Application is wrong, please restart it", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static JsonObjectRequest setVolleyRequestTimeOut(JsonObjectRequest request, int timeout) {
        request.setRetryPolicy(new DefaultRetryPolicy(timeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        return request;
    }
    public static VolleyMultipartRequest setVolleyMultipartRequestTimeOut(VolleyMultipartRequest request, int timeout) {
        request.setRetryPolicy(new DefaultRetryPolicy(timeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        return request;
    }



}
