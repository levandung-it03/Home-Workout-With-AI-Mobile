package com.restproject.mobile.fragments;

import static com.restproject.mobile.BuildConfig.BACKEND_ENDPOINT;
import static com.restproject.mobile.BuildConfig.PRIVATE_USER_DIR;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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

public class PreviewGenarateScheduleFragment extends Fragment implements PrivateUIObject {
    private PreviewScheduleResponse data = null;
    private final Long scheduleId;
    private final Long aimType;
    private final Long repRatio;
    private final Long weight;
    private final Float bodyFat;
    private final Long aimRatio;
    private final Long weightAimByDiet;

    private TextView scheduleName;

    public PreviewGenarateScheduleFragment(Long scheduleId, Long aimType, Long repRatio, Long weight, Float bodyFat, Long aimRatio, Long weightAimByDiet) {
        this.scheduleId = scheduleId;
        this.aimType = aimType;
        this.repRatio = repRatio;
        this.weight = weight;
        this.bodyFat = bodyFat;
        this.aimRatio = aimRatio;
        this.weightAimByDiet = weightAimByDiet;
    }

    private TextView level;
    private TextView coins;
    private TextView totalSessions;
    private TextView totalExercises;
    private ListView sessionList;
    private Button subscribeBtn;
    private LinearLayout dialogContainer;
    private TextView explainTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview_generate_schedule, container, false);
        this.scheduleName = view.findViewById(R.id.page_availableSched_dialog_scheduleName);
        this.level = view.findViewById(R.id.page_availableSched_dialog_scheduleLevel);
        this.coins = view.findViewById(R.id.page_availableSched_dialog_coins);
        this.totalSessions = view.findViewById(R.id.page_availableSched_dialog_totalSessions);
        this.totalExercises = view.findViewById(R.id.page_availableSched_dialog_totalExercises);
        this.sessionList = view.findViewById(R.id.page_availableSched_dialog_sessionList);
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
        this.subscribeBtn.setOnClickListener(v -> {
            JSONObject reqData = new JSONObject(Map.of(
                    "aimType", aimType,
                    "repRatio", repRatio,
                    "weight", weight,
                    "scheduleId", this.data.getSchedule().getScheduleId(),
                    "bodyFat", bodyFat,
                    "aimRatio", aimRatio == -1 ? JSONObject.NULL : aimRatio,
                    "weightAimByDiet", weightAimByDiet == -1 ? JSONObject.NULL : weightAimByDiet
            ));
            this.requestSubscribeSchedule(reqData);
        });
    }

    private void requestSubscribeSchedule(JSONObject reqData) {
        var context = this;
        var jsonReq = new JsonObjectRequest(
            Request.Method.POST,
            BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/subscribe-schedule-with-AI",
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
                .requestEnum(RequestEnums.SUBSCRIBE_SCHEDULE_AI)
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
            activity.navigationView.getMenu().findItem(R.id.navBar_generateSchedule).setChecked(false);
            activity.viewPager.setCurrentItem(0, false);
            activity.viewPaperAdapter.refreshData(0);
        }
    }

    @Override
    public void recall(JSONObject reqData, RequestEnums reqEnum) {
        if (reqEnum.equals(RequestEnums.SUBSCRIBE_SCHEDULE_AI))
            this.requestSubscribeSchedule(reqData);
        if (reqEnum.equals(RequestEnums.AVAILABLE_SCHE_GET_SCHEDULE_DETAIL))
            this.requestPreviewSchedule(reqData);
    }
}