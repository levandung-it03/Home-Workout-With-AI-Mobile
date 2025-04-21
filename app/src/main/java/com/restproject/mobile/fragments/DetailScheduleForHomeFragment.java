package com.restproject.mobile.fragments;

import static com.restproject.mobile.BuildConfig.BACKEND_ENDPOINT;
import static com.restproject.mobile.BuildConfig.PRIVATE_USER_DIR;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.activities.RequestEnums;
import com.restproject.mobile.adapters.AdapterHelper;
import com.restproject.mobile.adapters.HomeDialogSessionListAdapter;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.models.HomeDetailSchedule;
import com.restproject.mobile.utils.APIBuilderForGET;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.DateTimeHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DetailScheduleForHomeFragment extends Fragment implements PrivateUIObject {
    private final HomeDetailSchedule data;
    private TextView schedName;
    private TextView schedAim;
    private TextView schedLevel;
    private TextView schedDesc;
    private TextView schedSubTime;
    private Spinner schedDifficultySpin;
    private ListView sessionList;
    private boolean isInitialized = true;

    public DetailScheduleForHomeFragment(HomeDetailSchedule data) {
        this.data = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_schedule_for_home, container, false);
        this.schedName = view.findViewById(R.id.page_home_dialog_scheduleName);
        this.schedAim = view.findViewById(R.id.page_home_dialog_scheduleAim);
        this.schedLevel = view.findViewById(R.id.page_home_dialog_scheduleLevel);
        this.schedDesc = view.findViewById(R.id.page_home_dialog_scheduleDesc);
        this.schedSubTime = view.findViewById(R.id.page_home_dialog_scheduleSubTime);
        this.schedDifficultySpin = view.findViewById(R.id.page_home_dialog_scheduleChangeDifficulty);
        this.sessionList = view.findViewById(R.id.page_home_dialog_sessionList);

        this.initializeDataFromRequest();
        return view;
    }

    private void initializeDataFromRequest() {
        this.schedName.setText(data.getSchedule().getName());
        this.schedAim.setText(data.getSubscription().getAim().replace("_", " "));
        AdapterHelper.checkAndChangeLevelTag(this.schedLevel, data.getSchedule().getLevelEnum());
        this.schedDesc.setText(data.getSchedule().getDescription());
        this.schedSubTime.setText(DateTimeHelper.formatDateTimeFromGson(
            data.getSubscription().getChangingCoinsHistories().get("changingTime").toString()));
        this.setUpDifficultiesSpinner();
        this.sessionList.setAdapter(new HomeDialogSessionListAdapter(this.requireContext(),
            R.layout.layout_home_dialog_session_item, data.getSessions()));
    }

    private void setUpDifficultiesSpinner() {
        var context = this;
        var difficulties = new String[]{"Original Level (100% level)", "Average (90% level)",
            "Easy (80% level)"};
        var repRatios = new int[]{100, 90, 80};
        var adapter = new ArrayAdapter<>(this.requireContext(),
            android.R.layout.simple_spinner_item, difficulties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.schedDifficultySpin.setAdapter(adapter);
        for (int index = 0; index < repRatios.length; index++)
            if (repRatios[index] == this.data.getSubscription().getRepRatio())
                this.schedDifficultySpin.setSelection(index);

        this.schedDifficultySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isInitialized)
                    context.requestChangeDifficulty(new JSONObject(Map.of(
                        "newRepRatio", repRatios[(int) id],
                        "scheduleId", data.getSchedule().getScheduleId()
                    )));
                isInitialized = false;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void requestChangeDifficulty(JSONObject requestObj) {
        var context = this;
        var jsonReq = new JsonObjectRequest(
            Request.Method.PUT,
            APIBuilderForGET.parseFromJsonObject(requestObj,
                BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/update-subscribed-schedule-rep-ratio"),
            null,
            success -> {
                try {
                    var response = APIUtilsHelper.mapVolleySuccess(success);
                    Toast.makeText(this.getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
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
                .requestData(requestObj)
                .requestEnum(RequestEnums.HOME_GET_SCHEDULE_DETAIL)
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
        if (reqEnum.equals(RequestEnums.HOME_UPDATE_REP_RATIO))
            this.requestChangeDifficulty(reqData);
    }
}