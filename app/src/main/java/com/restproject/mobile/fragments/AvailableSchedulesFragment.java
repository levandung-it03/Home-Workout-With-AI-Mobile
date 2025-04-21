package com.restproject.mobile.fragments;

import static com.restproject.mobile.BuildConfig.BACKEND_ENDPOINT;
import static com.restproject.mobile.BuildConfig.PRIVATE_USER_DIR;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.MainActivity;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.activities.RequestEnums;
import com.restproject.mobile.adapters.AvaiChedScheduleListAdapter;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.models.HomeDetailSchedule;
import com.restproject.mobile.models.PaginatedDataResponse;
import com.restproject.mobile.models.Schedule;
import com.restproject.mobile.utils.APIBuilderForGET;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class AvailableSchedulesFragment extends PaginatedListFragment implements PrivateUIObject {
    private TextView explainTextView;
    private ListView scheduleListView;
    private AvaiChedScheduleListAdapter scheduleListAdapter;
    private final ArrayList<Schedule> scheduleList = new ArrayList<>();

    public AvailableSchedulesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_schedules, container, false);
        this.paginationPrevBtn = view.findViewById(R.id.page_availableSched_pagination_prevBtn);
        this.paginationCurPage = view.findViewById(R.id.page_availableSched_pagination_curPage);
        this.paginationNextBtn = view.findViewById(R.id.page_availableSched_pagination_nextBtn);
        this.setUpPagination();

        this.scheduleListView = view.findViewById(R.id.page_availableSched_scheduleList);
        this.explainTextView = view.findViewById(R.id.page_availableSched_explainTxtForEmptyList);
        this.scheduleListAdapter = new AvaiChedScheduleListAdapter(this.getContext(),
            R.layout.layout_avaisched_schedule_item, this.scheduleList);

        this.scheduleListView.setAdapter(this.scheduleListAdapter);
        this.scheduleListView.setOnItemClickListener(
            (parent, view1, position, id) -> {
                Schedule selectedSchedule = scheduleList.get(position);
                JSONObject requestObj = new JSONObject(Map.of("id", selectedSchedule.getScheduleId()));
                this.showScheduleDetailFragment(requestObj);
            });
        this.requestSchedules(this.getDataToRequestList());
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void checkAndShowExplainTagIfEmptyList() {
        if (this.scheduleList.isEmpty()) {
            this.scheduleListView.setVisibility(View.GONE);
            this.explainTextView.setVisibility(View.VISIBLE);
            this.explainTextView.setText("You've subscribed all schedules, please wait next updated version!");
        } else {
            this.scheduleListView.setVisibility(View.VISIBLE);
            this.explainTextView.setVisibility(View.GONE);
        }
    }

    public void requestSchedules(JSONObject requestData) {
        var context = this;
        var reqUrl = APIBuilderForGET.parseFromJsonObject(requestData,
            BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/get-available-schedules-of-user-pages");
        this.scheduleList.clear();
        var jsonReq = new JsonObjectRequest(
            Request.Method.GET,
            reqUrl,
            null,
            success -> {
                try {
                    var response = APIUtilsHelper.mapVolleySuccess(success);
                    var data = PaginatedDataResponse.mapping(response.getData());
                    this.scheduleList.addAll(data.getData().stream()
                        .map(Schedule::mapping)
                        .collect(Collectors.toList()));
                    this.scheduleListAdapter.notifyDataSetChanged();
                    this.totalPages = data.getTotalPages();
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
                .requestEnum(RequestEnums.AVAILABLE_SCHE_GET_ALL_SCHEDULE)
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

    private void showScheduleDetailFragment(JSONObject requestData) {
        var context = this;
        var jsonReq = new JsonObjectRequest(
            Request.Method.GET,
            APIBuilderForGET.parseFromJsonObject(requestData,
                BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/get-preview-schedule-to-perform"),
            null,
            success -> {
                try {
                    var data = HomeDetailSchedule.mapping(APIUtilsHelper.mapVolleySuccess(success).getData());
                    this.getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainLayout_dialogContainer, new DetailScheduleForHomeFragment(data))
                        .commit();
                    ((MainActivity) this.requireActivity()).openDialog();
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

    @Override
    public void recall(JSONObject reqData, RequestEnums reqEnum) {
        if (reqEnum.equals(RequestEnums.AVAILABLE_SCHE_GET_ALL_SCHEDULE))
            this.requestSchedules(reqData);
        if (reqEnum.equals(RequestEnums.AVAILABLE_SCHE_GET_SCHEDULE_DETAIL))
            this.showScheduleDetailFragment(reqData);
    }
}