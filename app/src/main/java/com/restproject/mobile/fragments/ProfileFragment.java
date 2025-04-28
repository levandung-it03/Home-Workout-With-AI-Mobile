package com.restproject.mobile.fragments;

import static com.restproject.mobile.BuildConfig.BACKEND_ENDPOINT;
import static com.restproject.mobile.BuildConfig.PRIVATE_AUTH_DIR;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.BuildConfig;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.MainActivity;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.activities.RequestEnums;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.exception.ApplicationException;
import com.restproject.mobile.models.UserInfo;
import com.restproject.mobile.storage_helpers.InternalStorageHelper;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;

import org.json.JSONObject;

import java.util.Map;

public class ProfileFragment extends Fragment implements PrivateUIObject, RefreshableFragment {
    private UserInfo data;
    private TextView fullName;
    private TextView coins;

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
        this.fullName = view.findViewById(R.id.page_profile_fullName);
        this.coins = view.findViewById(R.id.page_profile_coins);
        RelativeLayout logoutBtn = view.findViewById(R.id.page_profile_logoutVirtualBtn);
        Button updateUserInfoBtn = view.findViewById(R.id.page_profile_updateUserInfo);
        Button changePassword = view.findViewById(R.id.page_profile_changePassword);

        updateUserInfoBtn.setOnClickListener(v -> showUserInfoDialog());
        changePassword.setOnClickListener(v -> showChangePassDialog());
        logoutBtn.setOnClickListener(v -> requestLogout());
        this.requestUserInfo();
        return view;
    }

    private void showChangePassDialog() {
        this.getParentFragmentManager()
            .beginTransaction()
            .replace(R.id.mainLayout_dialogContainer, new ChangePasswordFragment())
            .commit();
        ((MainActivity) this.requireActivity()).openDialog();
    }

    private void showUserInfoDialog() {
        this.requestUserInfo(); //--Call Request to refresh new User-Info
        this.getParentFragmentManager()
            .beginTransaction()
            .replace(R.id.mainLayout_dialogContainer, new UserInfoDialog(this.data))
            .commit();
        ((MainActivity) this.requireActivity()).openDialog();
    }

    @SuppressLint("SetTextI18n")
    private void initializeFragData() {
        this.fullName.setText(this.data.getLastName() + " " + this.data.getFirstName());
        this.coins.setText(this.data.getCoins() + "₵");
    }

    public void requestUserInfo() {
        var context = this;
        var jsonReq = new JsonObjectRequest(
            Request.Method.GET,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PRIVATE_USER_DIR + "/v1/get-info",
            null,
            success -> {
                var response = APIUtilsHelper.mapVolleySuccess(success);
                this.data = UserInfo.mapping(response.getData());
                this.initializeFragData();
            }, error ->
                APIUtilsHelper.handlePrivateVolleyRequestError(VolleyErrorHandler.builder()
                    .activity((AppCompatActivity) context.requireActivity())
                    .context(context.getContext())
                    .app(context)
                    .requestData(null)
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


    public void requestLogout() {
        var context = this;
        try {
            var reqData = new JSONObject(Map.of("token",
                InternalStorageHelper.readIS(context.requireContext(), "tokens.txt").split(";")[1]));
            var jsonReq = new JsonObjectRequest(
                Request.Method.POST,
                BACKEND_ENDPOINT + PRIVATE_AUTH_DIR + "/v1/logout",
                reqData,
                success -> {
                    var response = APIUtilsHelper.mapVolleySuccess(success);
                    Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    this.clearTokensAndOpenLogin();
                }, error -> {
                    Toast.makeText(this.requireContext(), APIUtilsHelper.readErrorFromVolley(error).getMessage(),
                        Toast.LENGTH_SHORT).show();
                    this.clearTokensAndOpenLogin();
            }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    return RequestInterceptor.getPrivateAuthHeaders(context.requireContext());
                }
            };
            Volley.newRequestQueue(this.requireContext())
                .add(APIUtilsHelper.setVolleyRequestTimeOut(jsonReq, 30_000));
        } catch (ApplicationException e) {
            e.fillInStackTrace();
            Toast.makeText(this.requireContext(), "An Error occurred. Please restart app.",
                Toast.LENGTH_SHORT).show();
            this.clearTokensAndOpenLogin();
        }
    }

    public void clearTokensAndOpenLogin() {
        try {
            InternalStorageHelper.writeIS(this.requireContext(), "tokens.txt", "");
        } catch (ApplicationException e) {
            e.fillInStackTrace();
            Toast.makeText(this.requireContext(), "An Error occurred. Please restart app.",
                Toast.LENGTH_SHORT).show();
        }
        if (this.getActivity() instanceof MainActivity) {
            var mainActivity = (MainActivity) this.getActivity();
            mainActivity.showLoginFragment();
        }
    }

    @Override
    public void recall(JSONObject reqData, RequestEnums reqEnum) {
        if (reqEnum.equals(RequestEnums.PROFILE_GET_USER_INFO))
            this.requestUserInfo();
    }

    @Override
    public void refresh() {
        requireActivity().runOnUiThread(() -> {
            this.requestUserInfo();
        });
    }
}