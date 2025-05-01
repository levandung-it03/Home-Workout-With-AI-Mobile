package com.restproject.mobile.fragments;

import static com.restproject.mobile.utils.InputValidators.*;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.BuildConfig;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.MainActivity;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.activities.RequestEnums;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;

import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ChangePasswordFragment extends Fragment implements PrivateUIObject {
    private final Integer[] otpTimeHolder = new Integer[1];
    private String hiddenOtp;
    private EditText emailEdt, passwordEdt, otpEdt, newPassEdt, confNewPassEdt;
    private LinearLayout block1, block2, block3;
    private TextView otpCurrentAge;
    private CountDownTimer otpAgeTimer;
    private CheckBox toggleHidePass;
    private RelativeLayout preBlockVirtualBtn;

    public ChangePasswordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        this.emailEdt = view.findViewById(R.id.page_changePass_emailEdt);
        this.passwordEdt = view.findViewById(R.id.page_changePass_passwordEdt);
        this.otpEdt = view.findViewById(R.id.page_changePass_otpEdt);
        this.newPassEdt = view.findViewById(R.id.page_changePass_newPassEdt);
        this.confNewPassEdt = view.findViewById(R.id.page_changePass_confNewPassEdt);
        this.otpCurrentAge = view.findViewById(R.id.page_changePass_otpCurrentAge);
        this.toggleHidePass = view.findViewById(R.id.page_register_showPassChkBox);
        this.block1 = view.findViewById(R.id.page_changePass_getOtpBlock);
        this.block2 = view.findViewById(R.id.page_changePass_verifyOtpBlock);
        this.block3 = view.findViewById(R.id.page_changePass_newPassBlock);
        this.preBlockVirtualBtn = view.findViewById(R.id.page_changePass_preBlockBtn);
        Button getOtpBtn = view.findViewById(R.id.page_changePass_getOtpBtn);
        Button verifyOtpBtn = view.findViewById(R.id.page_changePass_verifyOtpBtn);
        Button submitBtn = view.findViewById(R.id.page_changePass_submitBtn);

        this.setUpTogglePasswordEdt();
        getOtpBtn.setOnClickListener(v -> {
            String validatedRes = this.validateGetOtpFormValues();
            if (!Objects.isNull(validatedRes)) {
                Toast.makeText(this.requireContext(), "Invalid " + validatedRes + "!",
                    Toast.LENGTH_SHORT).show();
                return;
            }
            this.requestGetOtpToChangePass(new JSONObject(Map.of(
                "email", getEdtStr(this.emailEdt),
                "password", getEdtStr(this.passwordEdt)
            )));
        });
        verifyOtpBtn.setOnClickListener(v -> {
            var otpCode = getEdtStr(this.otpEdt);
            if (otpCode.length() < 4) {
                Toast.makeText(this.getContext(), "OTP is wrong!", Toast.LENGTH_SHORT).show();
                return;
            }
            this.requestVerifyOtp(new JSONObject(Map.of("otpCode",
                getEdtStr(this.otpEdt).toUpperCase())));
        });
        submitBtn.setOnClickListener(v -> {
            String validatedRes = this.validateChangePasswordFormValues();
            if (!Objects.isNull(validatedRes)) {
                Toast.makeText(this.requireContext(), "Invalid " + validatedRes + "!",
                    Toast.LENGTH_SHORT).show();
                return;
            }
            this.requestChangePassword(new JSONObject(Map.of(
                "otpCode", this.hiddenOtp.trim().toUpperCase(),
                "password", getEdtStr(this.newPassEdt)
            )));
        });
        return view;
    }

    private void setUpTogglePasswordEdt() {
        this.toggleHidePass.setOnClickListener(v -> {
            this.newPassEdt.setInputType(toggleHidePass.isChecked()
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            this.newPassEdt.setSelection(this.newPassEdt.getText().length());

            this.confNewPassEdt.setInputType(toggleHidePass.isChecked()
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            this.confNewPassEdt.setSelection(this.confNewPassEdt.getText().length());
        });
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

    private String validateGetOtpFormValues() {
        if (!isValidStr(getEdtStr(this.emailEdt)))
            return "Email";
        if (!isValidStr(getEdtStr(this.passwordEdt)))
            return "Password";
        if (getEdtStr(this.passwordEdt).length() < 6)
            return "Password";
        if (!Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", getEdtStr(this.emailEdt)))
            return "Email";
        return null;
    }

    private String validateChangePasswordFormValues() {
        if (!isValidStr(getEdtStr(this.newPassEdt)))
            return "Both Password";
        if (!isValidStr(getEdtStr(this.confNewPassEdt)))
            return "Both Password";
        if (getEdtStr(this.newPassEdt).length() < 6)
            return "Password Length";
        if (!getEdtStr(this.newPassEdt).equals(getEdtStr(this.confNewPassEdt)))
            return "Both Password";
        return null;
    }

    private void requestGetOtpToChangePass(JSONObject requestData) {
        var context = this;
        var jsonRequest = new JsonObjectRequest(Request.Method.POST,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PRIVATE_USER_DIR + "/v1/get-otp-to-change-password",
            requestData,
            success -> {
                var response = APIUtilsHelper.mapVolleySuccess(success);
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
            APIUtilsHelper.handleSpecialPrivateVolleyRequestError(VolleyErrorHandler.builder()
                .activity((AppCompatActivity) context.requireActivity())
                .context(context.getContext())
                .app(context)
                .requestData(requestData)
                .requestEnum(RequestEnums.HOME_GET_ALL_SCHEDULE)
                .error(error));
        }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPrivateAuthHeaders(context.getContext());
            }
        };
        Volley.newRequestQueue(requireContext()).add(APIUtilsHelper
            .setVolleyRequestTimeOut(jsonRequest, 30_000));
    }

    private void requestVerifyOtp(JSONObject requestData) {
        var context = this;
        if (otpTimeHolder[0] < 1) {
            Toast.makeText(this.getContext(), "OTP Is Expired, please turn back!",
                Toast.LENGTH_SHORT).show();
            return;
        }
        var jsonRequest = new JsonObjectRequest(Request.Method.POST,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PRIVATE_USER_DIR + "/v1/verify-change-password-otp",
            requestData,
            success -> {
                var response = APIUtilsHelper.mapVolleySuccess(success);
                if (Objects.isNull(response.getData())) {
                    Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                this.hiddenOtp = response.getData().get("otpCode").toString();
                this.isOpenNewPasswordFrame(true);
            }, error -> {
            //--Not close OTP Input Frame.
            APIUtilsHelper.handleSpecialPrivateVolleyRequestError(VolleyErrorHandler.builder()
                .activity((AppCompatActivity) context.requireActivity())
                .context(context.getContext())
                .app(context)
                .requestData(requestData)
                .requestEnum(RequestEnums.HOME_GET_ALL_SCHEDULE)
                .error(error));
        }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPrivateAuthHeaders(context.getContext());
            }
        };
        Volley.newRequestQueue(requireContext()).add(APIUtilsHelper
            .setVolleyRequestTimeOut(jsonRequest, 30_000));
    }

    private void requestChangePassword(JSONObject requestData) {
        var context = this;
        var jsonRequest = new JsonObjectRequest(Request.Method.POST,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PRIVATE_USER_DIR + "/v1/change-password",
            requestData,
            success -> {
                var response = APIUtilsHelper.mapVolleySuccess(success);
                Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                ((MainActivity) this.requireActivity()).closeDialogBtn.callOnClick();
            }, error -> {
            //--Close OTP Input Frame.
            APIUtilsHelper.handlePrivateVolleyRequestError(VolleyErrorHandler.builder()
                .activity((AppCompatActivity) context.requireActivity())
                .context(context.getContext())
                .app(context)
                .requestData(requestData)
                .requestEnum(RequestEnums.HOME_GET_ALL_SCHEDULE)
                .error(error));
        }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPrivateAuthHeaders(context.getContext());
            }
        };
        Volley.newRequestQueue(requireContext()).add(APIUtilsHelper
            .setVolleyRequestTimeOut(jsonRequest, 30_000));
    }

    private void isOpenOTPFrame(boolean otpFrameOpened) {
        if (otpFrameOpened) {
            this.block1.setVisibility(View.GONE);
            this.block2.setVisibility(View.VISIBLE);
            this.otpEdt.setText("");   //--Refresh if this is not the 1fst time OTPBlock is opened
            this.preBlockVirtualBtn.setVisibility(View.VISIBLE);
            this.preBlockVirtualBtn.setOnClickListener((v) -> {
                this.block1.setVisibility(View.VISIBLE);
                this.block2.setVisibility(View.GONE);
                this.otpEdt.setText("");
                if (this.otpAgeTimer != null) this.otpAgeTimer.cancel();
                this.preBlockVirtualBtn.setVisibility(View.GONE);
                this.hiddenOtp = "";
            });
        } else {
            if (this.otpAgeTimer != null) this.otpAgeTimer.cancel();
            this.block1.setVisibility(View.VISIBLE);
            this.block2.setVisibility(View.GONE);
            this.preBlockVirtualBtn.setVisibility(View.GONE);
            this.otpEdt.setText("");
            this.hiddenOtp = "";
        }
    }

    private void isOpenNewPasswordFrame(boolean passFrameOpened) {
        if (passFrameOpened) {
            this.block2.setVisibility(View.GONE);
            this.block3.setVisibility(View.VISIBLE);
            this.otpEdt.setText("");   //--Refresh if this is not the 1fst time OTPBlock is opened
            this.preBlockVirtualBtn.setVisibility(View.VISIBLE);
            this.preBlockVirtualBtn.setOnClickListener((v) -> {
                this.block1.setVisibility(View.VISIBLE);
                this.block3.setVisibility(View.GONE);
                this.otpEdt.setText("");
                if (this.otpAgeTimer != null) this.otpAgeTimer.cancel();
                this.preBlockVirtualBtn.setVisibility(View.GONE);
                this.otpEdt.setText("");
                this.hiddenOtp = "";
            });
        } else {
            if (this.otpAgeTimer != null) this.otpAgeTimer.cancel();
            this.block1.setVisibility(View.VISIBLE);
            this.block3.setVisibility(View.GONE);
            this.preBlockVirtualBtn.setVisibility(View.GONE);
            this.otpEdt.setText("");
            this.hiddenOtp = "";
        }
    }

    @Override
    public void recall(JSONObject reqData, RequestEnums reqEnum) {
        if (reqEnum.equals(RequestEnums.CHANGE_PASS_GET_OTP))
            this.requestGetOtpToChangePass(reqData);
        if (reqEnum.equals(RequestEnums.CHANGE_PASS_VERIFY_OTP))
            this.requestVerifyOtp(reqData);
        if (reqEnum.equals(RequestEnums.CHANGE_PASS_CHANGE_PASSWORD))
            this.requestChangePassword(reqData);
    }
}
