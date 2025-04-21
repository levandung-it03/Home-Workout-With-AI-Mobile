package com.restproject.mobile.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.MainActivity;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.utils.APIBuilderForGET;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.APIResponseObject;
import com.restproject.mobile.utils.CryptoService;
import com.restproject.mobile.utils.InputValidators;
import com.restproject.mobile.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class LoginFragment extends Fragment {
    private EditText emailEdt;
    private EditText passEdt;
    private CheckBox toggleHidePass;

    public LoginFragment() {
        super();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        this.emailEdt = view.findViewById(R.id.page_login_emailEdt);
        this.passEdt = view.findViewById(R.id.page_login_passEdt);
        this.toggleHidePass = view.findViewById(R.id.page_login_showPassChkBox);
        Button submitLoginBtn = view.findViewById(R.id.page_login_submitBtn);
        Button registerBtn = view.findViewById(R.id.page_login_registerBtn);
        Button loginByGGBtn = view.findViewById(R.id.page_login_loginByGGBtn);
        Button forgotPassBtn = view.findViewById(R.id.page_login_forgotPassBtn);

        this.toggleHidePass.setOnClickListener(v -> togglePasswordEdt());
        submitLoginBtn.setOnClickListener(v -> {
            String email = emailEdt.getText().toString();
            String pass = passEdt.getText().toString();
            if (!InputValidators.isValidStr(email) || !InputValidators.isValidStr(pass)) {
                Toast.makeText(requireContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                requestLogin(new JSONObject().put("email", email)
                    .put("password", CryptoService.encrypt(pass)));
            } catch (JSONException e) {
                e.fillInStackTrace();
                Toast.makeText(requireContext(), "Error parsing login data", Toast.LENGTH_SHORT).show();
            }
        });
        registerBtn.setOnClickListener(v -> this.openRegisterFragment());
        forgotPassBtn.setOnClickListener(v -> this.openForgotPassFragment());
        loginByGGBtn.setOnClickListener(v -> this.requestLoginOauth2());
        return view;
    }

    private void togglePasswordEdt() {
        passEdt.setInputType(toggleHidePass.isChecked()
            ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passEdt.setSelection(passEdt.getText().length());   //--Move cursor to end
    }

    private void requestLogin(JSONObject request) {
        var context = this.requireContext();
        var reqUrl = BuildConfig.BACKEND_ENDPOINT + BuildConfig.PUBLIC_AUTH_DIR + "/v1/authenticate";
        var jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, reqUrl, request,
            success -> {
                var res = APIUtilsHelper.mapVolleySuccess(success);
                APIUtilsHelper.saveTokensAfterAuthentication(context, res.getData());
                Toast.makeText(context, res.getMessage(), Toast.LENGTH_SHORT).show();

                if (this.getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .remove(LoginFragment.this) // Xóa LoginFragment khỏi layout
                        .commit();
                    mainActivity.isNavVisible = false;
                    mainActivity.setUpNavigation();
                }
            },
            error -> {
                APIResponseObject<Void> response = APIUtilsHelper.readErrorFromVolley(error);
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPublicHeaders();
            }
        };
        Volley.newRequestQueue(context)
            .add(APIUtilsHelper.setVolleyRequestTimeOut(jsonObjectRequest, 30_000));
    }

    private void openRegisterFragment() {
        this.requireActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.main, new RegisterFragment())
            .commit();
    }

    private void openForgotPassFragment() {
        this.requireActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.main, new ForgotFragment())
            .commit();
    }

    private void requestLoginOauth2() {
        var context = this.requireContext();
        var reqUrl = APIBuilderForGET
            .builder(BuildConfig.BACKEND_ENDPOINT + BuildConfig.PUBLIC_AUTH_DIR + "/v1/oauth2-authentication-url")
            .valuesPair("loginType", BuildConfig.OAUTH2_DEFAULT_GG_PASS)
            .valuesPair("redirectUrl", "")
            .build();
        var jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, reqUrl, null,
            success -> {
                APIResponseObject<String> res = APIUtilsHelper.mapSimpleVolleySuccess(success);
                String oauth2Url = CryptoService.decrypt(res.getData());
                this.requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new Oauth2Fragment(oauth2Url))
                    .commit();
            },
            error -> {
                APIResponseObject<Void> response = APIUtilsHelper.readErrorFromVolley(error);
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPublicHeaders();
            }
        };
        Volley.newRequestQueue(context)
            .add(APIUtilsHelper.setVolleyRequestTimeOut(jsonObjectRequest, 30_000));
    }
}
