package com.restproject.mobile.fragments;

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
import com.restproject.mobile.activities.MainActivity;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.activities.RequestEnums;
import com.restproject.mobile.adapters.HomeScheduleListAdapter;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.models.HomeDetailSchedule;
import com.restproject.mobile.models.Schedule;
import com.restproject.mobile.utils.APIBuilderForGET;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;

import org.json.JSONObject;

import static com.restproject.mobile.BuildConfig.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements PrivateUIObject {
    private TextView explainTextView;
    private Spinner scheduleTypeSpin;
    private ListView scheduleListView;
    private HomeScheduleListAdapter scheduleListAdapter;
    private final ArrayList<Schedule> scheduleList = new ArrayList<>();
    private final String[] SPIN_OPTIONS = new String[]{"In-progress", "Completed"};

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        this.scheduleTypeSpin = view.findViewById(R.id.page_home_scheduleTypeSpinner);
        this.scheduleListView = view.findViewById(R.id.page_home_scheduleList);
        this.explainTextView = view.findViewById(R.id.page_home_explainTxtForEmptyList);
        this.scheduleListAdapter = new HomeScheduleListAdapter(this.getContext(),
            R.layout.layout_home_schedule_item, this.scheduleList);

        this.scheduleListView.setAdapter(this.scheduleListAdapter);
        this.scheduleListView.setOnItemClickListener(
            (parent, view1, position, id) -> {
                this.getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainLayout_dialogContainer,
                        new DetailScheduleForHomeFragment(scheduleList.get(position).getScheduleId()))
                    .commit();
                ((MainActivity) this.requireActivity()).openDialog();
            });
        this.setUpScheduleTypeSpin();
        this.scheduleTypeSpin.setSelection(0);
        return view;
    }

    public void setUpScheduleTypeSpin() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireContext(),
            android.R.layout.simple_spinner_item, SPIN_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        var context = this;
        this.scheduleTypeSpin.setAdapter(adapter);
        this.scheduleTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                context.requestSchedules(context.getDataToRequestSchedules());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void checkAndShowExplainTagIfEmptyList() {
        boolean isCompleted = this.scheduleTypeSpin.getSelectedItem().toString().equals(SPIN_OPTIONS[1]);
        if (this.scheduleList.isEmpty()) {
            this.scheduleListView.setVisibility(View.GONE);
            this.explainTextView.setVisibility(View.VISIBLE);
            this.explainTextView.setText(isCompleted
                ? "You haven't completed any Schedule yet, keep it up!"
                : "Please subscribes more Schedules!");
        } else {
            this.scheduleListView.setVisibility(View.VISIBLE);
            this.explainTextView.setVisibility(View.GONE);
        }
    }

    public JSONObject getDataToRequestSchedules() {
        JSONObject requestData;
        try {
            boolean isCompleted = this.scheduleTypeSpin.getSelectedItem().toString().equals(SPIN_OPTIONS[1]);
            requestData = new JSONObject().put("isCompleted", String.valueOf(isCompleted));
        } catch (Exception e) {
            e.fillInStackTrace();
            Toast.makeText(this.getContext(), "An Error occurred. Please restart app.",
                Toast.LENGTH_SHORT).show();
            requestData = null;
        }
        return requestData;
    }

    public void requestSchedules(JSONObject requestData) {
        var context = this;
        var reqUrl = APIBuilderForGET.parseFromJsonObject(requestData,
            BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/get-schedules-of-user");
        this.scheduleList.clear();
        var jsonReq = new JsonObjectRequest(
            Request.Method.GET,
            reqUrl,
            null,
            success -> {
                try {
                    var response = APIUtilsHelper.mapListVolleySuccess(success);
                    this.scheduleList.addAll(response.getData().stream()
                        .map(Schedule::mapping)
                        .collect(Collectors.toList()));
                    this.scheduleListAdapter.notifyDataSetChanged();
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
                .requestEnum(RequestEnums.HOME_GET_ALL_SCHEDULE)
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
        if (reqEnum.equals(RequestEnums.HOME_GET_ALL_SCHEDULE))
            this.requestSchedules(reqData);
    }

}