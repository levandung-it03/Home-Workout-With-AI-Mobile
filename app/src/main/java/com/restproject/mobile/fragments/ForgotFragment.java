package com.restproject.mobile.fragments;

import static com.restproject.mobile.utils.APIUtilsHelper.mapVolleySuccess;
import static com.restproject.mobile.utils.APIUtilsHelper.readErrorFromVolley;
import static com.restproject.mobile.utils.APIUtilsHelper.setVolleyRequestTimeOut;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.BuildConfig;
import com.restproject.mobile.R;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.utils.APIResponseObject;
import com.restproject.mobile.utils.InputValidators;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ForgotFragment extends Fragment {
    private final Integer[] otpTimeHolder = new Integer[1];
    private ScrollView basicInfoFrame;
    private ScrollView otpFrame;
    private EditText email;
    private TextView otpCurrentAge;
    private EditText otpCode;
    private ImageButton prevFragBtn;
    private CountDownTimer otpAgeTimer;


    public ForgotFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot, container, false);
        this.basicInfoFrame = view.findViewById(R.id.page_forgotPass_basicInfoFrame);
        this.otpFrame = view.findViewById(R.id.page_forgotPass_otpFrame);
        this.email = view.findViewById(R.id.page_forgotPass_emailEdt);
        this.otpCurrentAge = view.findViewById(R.id.page_forgotPass_otpCurrentAge);
        this.otpCode = view.findViewById(R.id.page_forgotPass_otpFromEmail);
        this.prevFragBtn = view.findViewById(R.id.page_forgotPass_prevFrag);
        Button submitBtn = view.findViewById(R.id.page_forgotPass_submitBtn);
        Button confirmOtpBtn = view.findViewById(R.id.page_forgotPass_confirmOtpBtn);

        this.prevFragBtn.setOnClickListener((v) -> this.turnBackToLoginFrag());
        submitBtn.setOnClickListener((v) -> this.requestOtpByEmailAndGetItsAge());
        confirmOtpBtn.setOnClickListener((v) -> this.confirmOtpAndSendRandPassToEmail());
        return view;
    }

    private void turnBackToLoginFrag() {
        this.requireActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.main, new LoginFragment())
            .commit();
    }

    private void requestOtpByEmailAndGetItsAge() {
        String validatedRes = this.validateValues();
        if (!Objects.isNull(validatedRes)) {
            Toast.makeText(this.requireContext(), "Invalid " + validatedRes + "!", Toast.LENGTH_SHORT).show();
            return;
        }
        var jsonRequest = new JsonObjectRequest(Request.Method.POST,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PUBLIC_AUTH_DIR + "/v1/get-forgot-password-otp",
            new JSONObject(Map.of("email", this.email.getText().toString())),
            success -> {
                var response = mapVolleySuccess(success);
                if (Objects.isNull(response.getData())) {
                    Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                int ageInSeconds = (int) Double.parseDouble(response.getData().get("ageInSeconds").toString());
                //--Show OTP Input Frame.
                this.setUpOtpCurrentAgeTimer(ageInSeconds);
                this.isOpenOTPFrame(true);
            }, error -> {
            //--Close OTP Input Frame.
            APIResponseObject<Void> response = readErrorFromVolley(error);
            Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            this.isOpenOTPFrame(false);
        }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPublicHeaders();
            }
        };
        Volley.newRequestQueue(requireContext()).add(setVolleyRequestTimeOut(jsonRequest, 30_000));
    }

    private String validateValues() {
        if (!InputValidators.isValidStr(this.email.getText().toString()))
            return "Email";
        if (!Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
            this.email.getText().toString()))
            return "Email";
        return null;
    }

    private void isOpenOTPFrame(boolean otpFrameOpened) {
        if (otpFrameOpened) {
            this.basicInfoFrame.setVisibility(View.GONE);
            this.otpFrame.setVisibility(View.VISIBLE);
            this.otpCode.setText("");
            this.prevFragBtn.setOnClickListener((v) -> {
                this.basicInfoFrame.setVisibility(View.VISIBLE);
                this.otpFrame.setVisibility(View.GONE);
                this.otpCode.setText("");
                if (this.otpAgeTimer != null) this.otpAgeTimer.cancel();
                this.prevFragBtn.setOnClickListener((view) -> this.turnBackToLoginFrag());
            });
        } else {
            if (this.otpAgeTimer != null) this.otpAgeTimer.cancel();
            this.basicInfoFrame.setVisibility(View.VISIBLE);
            this.otpFrame.setVisibility(View.GONE);
            this.prevFragBtn.setOnClickListener((v) -> this.turnBackToLoginFrag());
        }
    }

    private void setUpOtpCurrentAgeTimer(int age) {
        var context = this;
        context.otpAgeTimer = new CountDownTimer(age * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                context.otpCurrentAge.setText(secondsLeft + "s left");
                otpTimeHolder[0] = secondsLeft;
            }
            public void onFinish() {
                context.otpCurrentAge.setText("Otp Expired");
                context.otpCurrentAge.setTextColor(
                    ContextCompat.getColor(context.otpCurrentAge.getContext(), R.color.red));
            }
        }.start();
    }

    private void confirmOtpAndSendRandPassToEmail() {
        JSONObject jsonReqObj;
        try {
            String otpCode = this.otpCode.getText().toString().toUpperCase();
            if (otpCode.isEmpty()) throw new NullPointerException();
            jsonReqObj = new JSONObject()
                .put("email", this.email.getText().toString())
                .put("otpCode", otpCode);
        } catch (NullPointerException | JSONException e) {
            e.fillInStackTrace();
            Toast.makeText(this.requireContext(), "Invalid OTP Code", Toast.LENGTH_SHORT).show();
            return;
        }
        var jsonRequest = new JsonObjectRequest(Request.Method.POST,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PUBLIC_AUTH_DIR + "/v1/generate-random-password",
            jsonReqObj,
            success -> {
                var response = mapVolleySuccess(success);
                if (Objects.isNull(response.getData()))
                    Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                this.turnBackToLoginFrag();
            }, error -> {
            APIResponseObject<Void> response = readErrorFromVolley(error);
            Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
        }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPublicHeaders();
            }
        };
        Volley.newRequestQueue(requireContext()).add(setVolleyRequestTimeOut(jsonRequest, 30_000));
    }
}