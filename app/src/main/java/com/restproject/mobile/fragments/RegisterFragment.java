package com.restproject.mobile.fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.BuildConfig;
import com.restproject.mobile.R;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.utils.APIResponseObject;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.DateTimeHelper;
import com.restproject.mobile.utils.InputValidators;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {
    private final HashMap<String, Integer> SPIN_OPTIONS = new HashMap<>();
    private final Integer[] otpTimeHolder = new Integer[1];
    private ScrollView basicInfoFrame;
    private ScrollView otpFrame;
    private EditText firstName;
    private EditText lastName;
    private EditText dob;
    private Spinner genderSpin;
    private EditText email;
    private EditText passEdt;
    private EditText rePassEdt;
    private CheckBox toggleHidePass;
    private TextView otpCurrentAge;
    private EditText otpCode;
    private ImageButton prevFragBtn;
    private CountDownTimer otpAgeTimer;

    public RegisterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        this.basicInfoFrame = view.findViewById(R.id.page_register_basicInfoFrame);
        this.otpFrame = view.findViewById(R.id.page_register_otpFrame);
        this.firstName = view.findViewById(R.id.page_register_firstName);
        this.lastName = view.findViewById(R.id.page_register_lastName);
        this.dob = view.findViewById(R.id.page_register_dob);
        this.genderSpin = view.findViewById(R.id.page_register_genderSpinner);
        this.email = view.findViewById(R.id.page_register_emailEdt);
        this.passEdt = view.findViewById(R.id.page_register_passEdt);
        this.rePassEdt = view.findViewById(R.id.page_register_rePassEdt);
        this.toggleHidePass = view.findViewById(R.id.page_register_showPassChkBox);
        this.otpCurrentAge = view.findViewById(R.id.page_register_otpCurrentAge);
        this.otpCode = view.findViewById(R.id.page_register_otpFromEmail);
        this.prevFragBtn = view.findViewById(R.id.page_register_prevFrag);
        Button submitBtn = view.findViewById(R.id.page_register_submitBtn);
        Button registerBtn = view.findViewById(R.id.page_register_registerBtn);
        Button resendOtpBtn = view.findViewById(R.id.page_register_resendOtpBtn);

        this.setUpGenderSpin();
        this.setUpTogglePasswordEdt();
        this.prevFragBtn.setOnClickListener((v) -> this.turnBackToLoginFrag());
        submitBtn.setOnClickListener((v) -> this.requestOtpByEmailAndGetItsAge());
        registerBtn.setOnClickListener((v) -> this.confirmOtp());
        resendOtpBtn.setOnClickListener((v) -> this.checkAndResendOtp());
        return view;
    }

    private void setUpGenderSpin() {
        var jsonRequest = new JsonObjectRequest(
            Request.Method.GET,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PUBLIC_DIR + "/user/v1/get-all-genders",
            null,
            success -> {
                try {
                    var response = APIUtilsHelper.mapListVolleySuccess(success);
                    response.getData().forEach(gender -> {
                        this.SPIN_OPTIONS.put(
                            gender.get("raw").toString(),
                            (int) Double.parseDouble(gender.get("id").toString()));
                    });
                    var adapter = new ArrayAdapter<>(this.requireContext(),
                        android.R.layout.simple_spinner_item, this.SPIN_OPTIONS.keySet().toArray());
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    this.genderSpin.setAdapter(adapter);
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }, error -> {
                APIResponseObject<Void> response = APIUtilsHelper.readErrorFromVolley(error);
                Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPublicHeaders();
            }
        };
        Volley.newRequestQueue(requireContext()).add(APIUtilsHelper
            .setVolleyRequestTimeOut(jsonRequest, 30_000));
    }

    private void setUpTogglePasswordEdt() {
        this.toggleHidePass.setOnClickListener(v -> {
            this.passEdt.setInputType(toggleHidePass.isChecked()
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            this.passEdt.setSelection(this.passEdt.getText().length());

            this.rePassEdt.setInputType(toggleHidePass.isChecked()
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            this.rePassEdt.setSelection(this.rePassEdt.getText().length());
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

    private String validateValues() {
        if (!InputValidators.isValidStr(this.email.getText().toString()))
            return "Email";
        if (!InputValidators.isValidStr(this.firstName.getText().toString()))
            return "First Name";
        if (!InputValidators.isValidStr(this.lastName.getText().toString()))
            return "Last Name";
        if (!InputValidators.isValidStr(this.dob.getText().toString()))
            return "Date of birth";
        if (!InputValidators.isValidStr(this.passEdt.getText().toString()))
            return "Password";
        if (!InputValidators.isValidStr(this.rePassEdt.getText().toString()))
            return "Password";
        if (!this.passEdt.getText().toString().equals(this.rePassEdt.getText().toString()))
            return "Password";
        if (!Pattern.matches("^[A-Za-zÀ-ỹ]{1,50}$", this.firstName.getText().toString()))
            return "First Name";
        if (!Pattern.matches("^[A-Za-zÀ-ỹ]{1,50}( [A-Za-zÀ-ỹ]{1,50})*$",
            this.lastName.getText().toString()))
            return "Last Name";
        if (!Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
            this.email.getText().toString()))
            return "Email";
        //--Not need to check rePass if they're the same.
        if (this.passEdt.getText().toString().length() < 6)
            return "Password";
        if (!Pattern.matches("^([0-2][0-9]|(3)[0-1])/((0)[1-9]|(1)[0-2])/\\d{4}$",
            this.dob.getText().toString()))
            return "Date of birth";
        return null;
    }

    private void requestOtpByEmailAndGetItsAge() {
        String validatedRes = this.validateValues();
        if (!Objects.isNull(validatedRes)) {
            Toast.makeText(this.requireContext(), "Invalid " + validatedRes + "!", Toast.LENGTH_SHORT).show();
            return;
        }
        var jsonRequest = new JsonObjectRequest(Request.Method.POST,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PUBLIC_AUTH_DIR + "/v1/get-register-otp",
            new JSONObject(Map.of("email", this.email.getText().toString())),
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
                APIResponseObject<Void> response = APIUtilsHelper.readErrorFromVolley(error);
                Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                this.isOpenOTPFrame(false);
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPublicHeaders();
            }
        };
        Volley.newRequestQueue(requireContext()).add(APIUtilsHelper
            .setVolleyRequestTimeOut(jsonRequest, 30_000));
    }

    private void checkAndResendOtp() {
        if (this.otpTimeHolder[0] <= 1) {
            this.requestOtpByEmailAndGetItsAge();
            Toast.makeText(this.requireContext(), "Re-send OTP to Email successfully!",
                Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.requireContext(), "Current OTP is still valid, please check email!",
                Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmOtp() {
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
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PUBLIC_AUTH_DIR + "/v1/verify-register-otp",
            jsonReqObj,
            success -> {
                var response = APIUtilsHelper.mapVolleySuccess(success);
                if (Objects.isNull(response.getData()))
                    Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                else this.requestRegister(response.getData().get("otpCode").toString());
            }, error -> {
                APIResponseObject<Void> response = APIUtilsHelper.readErrorFromVolley(error);
                Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPublicHeaders();
            }
        };
        Volley.newRequestQueue(requireContext()).add(APIUtilsHelper
            .setVolleyRequestTimeOut(jsonRequest, 30_000));
    }

    private void requestRegister(String hiddenOtp) {
        HashMap<String, Object> fullDataToRegister = new HashMap<String, Object>(Map.of(
            "firstName", this.firstName.getText().toString(),
            "lastName", this.lastName.getText().toString(),
            "genderId", Objects.requireNonNull(
                this.SPIN_OPTIONS.get(this.genderSpin.getSelectedItem().toString())),
            "email", this.email.getText().toString(),
            "password", this.passEdt.getText().toString(),
            "otpCode", hiddenOtp
        ));
        String acceptedDob = DateTimeHelper.formatDateTimeFromEdt(this.dob.getText().toString());
        if (acceptedDob == null) {
            Toast.makeText(this.requireContext(), "Invalid Date of birth", Toast.LENGTH_SHORT).show();
            return;
        }
        fullDataToRegister.put("dob", acceptedDob);
        var jsonRequest = new JsonObjectRequest(Request.Method.POST,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PUBLIC_AUTH_DIR + "/v1/register-user",
            new JSONObject(fullDataToRegister),
            success -> {
                var response = APIUtilsHelper.mapVolleySuccess(success);
                Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                this.turnBackToLoginFrag();
            }, error -> {
                APIResponseObject<Void> response = APIUtilsHelper.readErrorFromVolley(error);
                Toast.makeText(this.requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                this.isOpenOTPFrame(false);
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPublicHeaders();
            }
        };
        Volley.newRequestQueue(requireContext()).add(APIUtilsHelper
            .setVolleyRequestTimeOut(jsonRequest, 30_000));
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
            });
        } else {
            if (this.otpAgeTimer != null) this.otpAgeTimer.cancel();
            this.basicInfoFrame.setVisibility(View.VISIBLE);
            this.otpFrame.setVisibility(View.GONE);
            this.prevFragBtn.setOnClickListener((v) -> this.turnBackToLoginFrag());
        }
    }

    private void turnBackToLoginFrag() {
        this.requireActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.main, new LoginFragment())
            .commit();
    }
}