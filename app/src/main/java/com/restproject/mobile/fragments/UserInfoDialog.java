package com.restproject.mobile.fragments;

import static com.restproject.mobile.BuildConfig.BACKEND_ENDPOINT;
import static com.restproject.mobile.BuildConfig.PRIVATE_USER_DIR;
import static com.restproject.mobile.utils.DateTimeHelper.*;
import static com.restproject.mobile.utils.InputValidators.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.restproject.mobile.models.UserInfo;
import com.restproject.mobile.utils.APIResponseObject;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.InputValidators;
import com.restproject.mobile.utils.VolleyErrorHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class UserInfoDialog extends Fragment implements PrivateUIObject {
    private final UserInfo data;
    private EditText firstName;
    private EditText lastName;
    private EditText dob;
    private Spinner genderSpin;
    private final ArrayList<ArrayList<Object>> SPIN_OPTIONS = new ArrayList<>();

    public UserInfoDialog(UserInfo data) {
        this.data = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        this.firstName = view.findViewById(R.id.page_profile_userInfoDialog_firstName);
        this.lastName = view.findViewById(R.id.page_profile_userInfoDialog_lastName);
        this.dob = view.findViewById(R.id.page_profile_userInfoDialog_dob);
        this.genderSpin = view.findViewById(R.id.page_profile_userInfoDialog_gender);
        Button submitBtn = view.findViewById(R.id.page_profile_userInfoDialog_submitBtn);

        this.mappingDataIntoEditTexts();
        submitBtn.setOnClickListener(v -> {
            var validateRes = this.validateUserInfo();
            if (Objects.nonNull(validateRes))
                Toast.makeText(this.requireContext(), "Invalid " + validateRes, Toast.LENGTH_SHORT).show();
            else
                this.requestUpdateUserInfo(this.getNewUserInfo());
        });
        return view;
    }

    private void mappingDataIntoEditTexts() {
        this.firstName.setText(this.data.getFirstName());
        this.lastName.setText(this.data.getLastName());
        this.dob.setText(formatDateToStr(addDaysIntoDate(this.data.getDob(), 0)));
        this.setUpGenderSpin();
    }

    private void setUpGenderSpin() {
        var jsonRequest = new JsonObjectRequest(
            Request.Method.GET,
            BuildConfig.BACKEND_ENDPOINT + BuildConfig.PUBLIC_DIR + "/user/v1/get-all-genders",
            null,
            success -> {
                try {
                    var response = APIUtilsHelper.mapListVolleySuccess(success);
                    this.SPIN_OPTIONS.add(new ArrayList<>());   //--For "raw" keys
                    this.SPIN_OPTIONS.add(new ArrayList<>());   //--For "id" keys
                    var selectedInd = new int[1];
                    response.getData().forEach(gender -> {
                        this.SPIN_OPTIONS.get(0).add(gender.get("raw").toString());
                        this.SPIN_OPTIONS.get(1).add(gender.get("id").toString());
                        if (gender.get("raw").toString().equals(this.data.getGender()))
                            selectedInd[0] = this.SPIN_OPTIONS.get(0).size() - 1; //--Item has just been pushed
                    });
                    var adapter = new ArrayAdapter<>(this.requireContext(),
                        android.R.layout.simple_spinner_item, this.SPIN_OPTIONS.get(0).toArray());
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    this.genderSpin.setAdapter(adapter);
                    this.genderSpin.setSelection(selectedInd[0]);
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

    private String validateUserInfo() {
        if (!InputValidators.isValidStr(getEdtStr(this.firstName)))
            return "First Name";
        if (!InputValidators.isValidStr(getEdtStr(this.lastName)))
            return "Last Name";
        if (!InputValidators.isValidStr(getEdtStr(this.dob)))
            return "Date of birth";
        if (!Pattern.matches("^[A-Za-zÀ-ỹ]{1,50}$", getEdtStr(this.firstName)))
            return "First Name";
        if (!Pattern.matches("^[A-Za-zÀ-ỹ]{1,50}( [A-Za-zÀ-ỹ]{1,50})*$",
            getEdtStr(this.lastName)))
            return "Last Name";
        if (!Pattern.matches("^([0-2][0-9]|(3)[0-1])/((0)[1-9]|(1)[0-2])/\\d{4}$",
            getEdtStr(this.dob)))
            return "Date of birth";
        return null;
    }

    private JSONObject getNewUserInfo() {
        HashMap<String, Object> requestOj = new HashMap<>();
        Boolean[] isUnchangedMarker = new Boolean[] {false, false, false, false};
        int genderId = 0;
        try {
            for (int ind = 0; ind < this.SPIN_OPTIONS.get(0).size(); ind++) {
                if (this.SPIN_OPTIONS.get(0).get(ind).toString()
                    .equals(this.genderSpin.getSelectedItem().toString())) {
                    genderId = Integer.parseInt(this.SPIN_OPTIONS.get(1).get(ind).toString());
                    if (this.SPIN_OPTIONS.get(0).get(ind).equals(this.genderSpin.getSelectedItem().toString()))
                        isUnchangedMarker[0] = true;
                    break;
                }
            }
        } catch (NullPointerException e) { e.fillInStackTrace(); }
        requestOj.put("genderId", genderId);
        requestOj.put("firstName", getEdtStr(this.firstName));
        if (requestOj.get("firstName").toString().equals(this.data.getFirstName()))
            isUnchangedMarker[1] = true;
        requestOj.put("lastName", getEdtStr(this.lastName));
        if (requestOj.get("lastName").toString().equals(this.data.getLastName()))
            isUnchangedMarker[2] = true;
        requestOj.put("dob", formatDateTimeFromEdt(getEdtStr(this.dob)));
        if (Objects.isNull(requestOj.get("dob")))
            return new JSONObject(Map.of("err", "Invalid Date of Birth!"));
        if (formatDateToSpringFormat(addDaysIntoDate(this.data.getDob(), 0))
            .equals(requestOj.get("dob").toString()))
            isUnchangedMarker[3] = true;
        if (Arrays.stream(isUnchangedMarker).anyMatch(isChanged -> !isChanged))
            return new JSONObject(requestOj);   //--One field's changed, this line will be reached.
        else
            return new JSONObject(Map.of("err", "Data isn't changed, don't submit it!"));
    }

    private void requestUpdateUserInfo(JSONObject requestData) {
        try {
            Toast.makeText(this.requireContext(), requestData.get("err").toString(),
                Toast.LENGTH_SHORT).show();
            return;
        } catch (JSONException e) {
            //--continue those under codes.
        }
        var context = this;
        var jsonReq = new JsonObjectRequest(Request.Method.PUT,
            BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/update-user-info", requestData,
            success -> {
                try {
                    var response = APIUtilsHelper.mapVolleySuccess(success);
                    if (context.requireActivity() instanceof MainActivity) {
                        var mainActivity = (MainActivity) context.requireActivity();
                        mainActivity.closeDialogBtn.callOnClick();
                        Toast.makeText(mainActivity.getBaseContext(), response.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    }
                } catch (RuntimeException e) {
                    e.fillInStackTrace();
                    Toast.makeText(this.getContext(), "An Error occurred. Please restart app.",
                        Toast.LENGTH_SHORT).show();
                }
            }, error ->
            APIUtilsHelper.handlePrivateVolleyRequestError(VolleyErrorHandler.builder()
                .activity((AppCompatActivity) context.requireActivity())
                .context(context.getContext())
                .app(context)
                .requestData(requestData)
                .requestEnum(RequestEnums.PROFILE_UPDATE_USER_INFO)
                .error(error))
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return RequestInterceptor.getPrivateHeaders(context.getContext());
            }
        };
        Volley.newRequestQueue(context.requireContext())
            .add(APIUtilsHelper.setVolleyRequestTimeOut(jsonReq, 30_000));
    }

    @Override
    public void recall(JSONObject reqData, RequestEnums reqEnum) {
        if (reqEnum.equals(RequestEnums.PROFILE_UPDATE_USER_INFO))
            this.requestUpdateUserInfo(reqData);
    }
}
