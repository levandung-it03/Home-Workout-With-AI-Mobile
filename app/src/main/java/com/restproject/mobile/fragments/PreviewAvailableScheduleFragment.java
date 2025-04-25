package com.restproject.mobile.fragments;

import static com.restproject.mobile.BuildConfig.BACKEND_ENDPOINT;
import static com.restproject.mobile.BuildConfig.PRIVATE_USER_DIR;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.internal.LinkedTreeMap;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.MainActivity;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.activities.RequestEnums;
import com.restproject.mobile.adapters.AdapterHelper;
import com.restproject.mobile.adapters.AvaiSchedSessionsListAdapter;
import com.restproject.mobile.adapters.ExNamesListAdapter;
import com.restproject.mobile.adapters.ViewPaperAdapter;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.models.PreviewScheduleResponse;
import com.restproject.mobile.utils.APIBuilderForGET;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PreviewAvailableScheduleFragment extends Fragment implements PrivateUIObject {
    private PreviewScheduleResponse data = null;
    private final Long scheduleId;
    private TextView scheduleName;
    private TextView level;
    private TextView coins;
    private TextView totalSessions;
    private TextView totalExercises;
    private ListView sessionList;
    private Spinner chooseAimSpin;
    private Spinner chooseLevelSpin;
    private Button subscribeBtn;
    private LinearLayout dialogContainer;
    private TextView explainTextView;
    private final HashMap<String, Integer> AIM_TYPES = new HashMap<>();
    private final HashMap<String, Integer> LEVEL_IDS = new HashMap<>(Map.of(
        "Original Level (100% level)", 100,
        "Original Level (90% level)", 90,
        "Original Level (80% level)", 80
    ));

    public PreviewAvailableScheduleFragment(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview_available_schedule, container, false);
        this.scheduleName = view.findViewById(R.id.page_availableSched_dialog_scheduleName);
        this.level = view.findViewById(R.id.page_availableSched_dialog_scheduleLevel);
        this.coins = view.findViewById(R.id.page_availableSched_dialog_coins);
        this.totalSessions = view.findViewById(R.id.page_availableSched_dialog_totalSessions);
        this.totalExercises = view.findViewById(R.id.page_availableSched_dialog_totalExercises);
        this.sessionList = view.findViewById(R.id.page_availableSched_dialog_sessionList);
        this.chooseAimSpin = view.findViewById(R.id.page_availableSched_dialog_chooseAimSpin);
        this.chooseLevelSpin = view.findViewById(R.id.page_availableSched_dialog_chooseLevelSpin);
        this.subscribeBtn = view.findViewById(R.id.page_availableSched_dialog_subscribeBtn);
        this.dialogContainer = view.findViewById(R.id.page_availableSched_dialog_dialogContainer);
        this.explainTextView = view.findViewById(R.id.page_availableSched_dialog_explainTextView);

        this.checkAndShowExplainTagIfEmptyList();
        this.requestPreviewSchedule(new JSONObject(Map.of("id", this.scheduleId)));
        return view;
    }

    public void checkAndShowExplainTagIfEmptyList() {
        if (Objects.isNull(this.data)) {
            this.dialogContainer.setVisibility(View.GONE);
            this.explainTextView.setVisibility(View.VISIBLE);
        } else {
            this.dialogContainer.setVisibility(View.VISIBLE);
            this.explainTextView.setVisibility(View.GONE);
        }
    }

    private void requestPreviewSchedule(JSONObject requestData) {
        var context = this;
        var jsonReq = new JsonObjectRequest(
            Request.Method.GET,
            APIBuilderForGET.parseFromJsonObject(requestData,
                BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/get-preview-schedule-info-for-user-to-subscribe"),
            null,
            success -> {
                try {
                    var response = APIUtilsHelper.mapVolleySuccess(success).getData();
                    this.data = PreviewScheduleResponse.mapping(response);
                    this.mappingDataIntoViews();
                    this.checkAndShowExplainTagIfEmptyList();
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
                .requestEnum(RequestEnums.AVAILABLE_SCHE_GET_SCHEDULE_DETAIL)
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

    @SuppressLint("SetTextI18n")
    public void mappingDataIntoViews() {
        this.scheduleName.setText(data.getSchedule().getName());
        AdapterHelper.checkAndChangeLevelTag(this.level, data.getSchedule().getLevelEnum());
        this.coins.setText(data.getSchedule().getCoins().toString() + "₵");
        this.totalSessions.setText(data.getTotalSessions().toString() + "/week");
        this.totalExercises.setText(data.getTotalExercises().toString() + "/schedule");
        this.sessionList.setAdapter(new AvaiSchedSessionsListAdapter(this.requireContext(),
            R.layout.layout_avaisched_session_item, this.data.getSessionsOfSchedules()));
        //--Set-up Level Spinner
        this.requestAllAimsForSpin(null);
        var adapter = new ArrayAdapter<>(this.requireContext(),
            android.R.layout.simple_spinner_item, this.LEVEL_IDS.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.chooseLevelSpin.setAdapter(adapter);
        this.subscribeBtn.setOnClickListener(v -> {
            JSONObject reqData = new JSONObject(Map.of(
                "aimType", Objects.requireNonNull(this.AIM_TYPES.get(
                    this.chooseAimSpin.getSelectedItem().toString())),
                "repRatio", Objects.requireNonNull(this.LEVEL_IDS.get(
                    this.chooseLevelSpin.getSelectedItem().toString())),
                "scheduleId", this.data.getSchedule().getScheduleId()
            ));
            this.requestSubscribeSchedule(reqData);
        });
    }

    private void requestAllAimsForSpin(JSONObject ignored) {
        var context = this;
        this.AIM_TYPES.clear();
        var jsonReq = new JsonObjectRequest(
            Request.Method.GET,
            BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/get-all-aims",
            null,
            success -> {
                try {
                    var options = new ArrayList<String>();
                    var response = APIUtilsHelper.mapListVolleySuccess(success);
                    for (LinkedTreeMap obj: response.getData()) {
                        options.add(obj.get("name").toString());
                        this.AIM_TYPES.put(
                            obj.get("name").toString(),
                            (int) Double.parseDouble(obj.get("type").toString()));
                    }
                    var adapter = new ArrayAdapter<>(this.requireContext(),
                        android.R.layout.simple_spinner_item, options.toArray(new String[0]));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    this.chooseAimSpin.setAdapter(adapter);
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
                .requestData(ignored)
                .requestEnum(RequestEnums.GET_ALL_AIMS)
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

    private void requestSubscribeSchedule(JSONObject reqData) {
        var context = this;
        var jsonReq = new JsonObjectRequest(
            Request.Method.POST,
            BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/subscribe-schedule",
            reqData,
            success -> {
                try {
                    var response = APIUtilsHelper.mapVolleySuccess(success);
                    Toast.makeText(this.getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    this.closeDialogAndReturnToHome();
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
                .requestData(reqData)
                .requestEnum(RequestEnums.SUBSCRIBE_SCHEDULE)
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

    private void closeDialogAndReturnToHome() {
        if (this.getActivity() instanceof MainActivity) {
            var activity = (MainActivity) this.getActivity();
            activity.closeDialogBtn.callOnClick();
            activity.navigationView.getMenu().findItem(R.id.navBar_subscribeSchedule).setChecked(false);
            activity.viewPager.setCurrentItem(0, false);
            activity.viewPaperAdapter.refreshData(0);
        }
    }

    @Override
    public void recall(JSONObject reqData, RequestEnums reqEnum) {
        if (reqEnum.equals(RequestEnums.GET_ALL_AIMS))
            this.requestAllAimsForSpin(reqData);
        if (reqEnum.equals(RequestEnums.SUBSCRIBE_SCHEDULE))
            this.requestSubscribeSchedule(reqData);
        if (reqEnum.equals(RequestEnums.AVAILABLE_SCHE_GET_SCHEDULE_DETAIL))
            this.requestPreviewSchedule(reqData);
    }
}