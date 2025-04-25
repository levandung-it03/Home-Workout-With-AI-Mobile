package com.restproject.mobile.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.BuildConfig;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.activities.RequestEnums;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.models.UserInfo;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;

import org.json.JSONObject;

import java.util.Map;

public class ProfileFragment extends Fragment implements PrivateUIObject {
    private UserInfo data;
    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.requestUserInfo(null);
        return view;
    }

    private void requestUserInfo(JSONObject requestData) {
        var context = this;
        var jsonReq = new JsonObjectRequest(
            Request.Method.GET,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PRIVATE_USER_DIR + "/v1/get-info",
            null,
            success -> {
                var response = APIUtilsHelper.mapVolleySuccess(success);
                this.data = UserInfo.mapping(response.getData());
            }, error ->
                APIUtilsHelper.handlePrivateVolleyRequestError(VolleyErrorHandler.builder()
                    .activity((AppCompatActivity) context.requireActivity())
                    .context(context.getContext())
                    .app(context)
                    .requestData(requestData)
                    .requestEnum(RequestEnums.PROFILE_GET_USER_INFO)
                    .error(error))
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPrivateAuthHeaders(context.getContext());
            }
        };
        Volley.newRequestQueue(context.requireContext())
            .add(APIUtilsHelper.setVolleyRequestTimeOut(jsonReq, 30_000));
    }

    @Override
    public void recall(JSONObject reqData, RequestEnums reqEnum) {
        if (reqEnum.equals(RequestEnums.PROFILE_GET_USER_INFO))
            this.requestUserInfo(null);
    }
}