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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.activities.RequestEnums;
import com.restproject.mobile.adapters.CoinsHistoriesListAdapter;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.models.ChangingCoinsHistories;
import com.restproject.mobile.models.Schedule;
import com.restproject.mobile.utils.APIBuilderForGET;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class CoinsHistoriesFragment extends Fragment implements PrivateUIObject {
    private TextView explainTxtForEmptyList;
    private ListView historiesListView;
    private CoinsHistoriesListAdapter listAdapter;
    private final ArrayList<ChangingCoinsHistories> histories = new ArrayList<>();

    public CoinsHistoriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coins_histories, container, false);
        this.explainTxtForEmptyList = view.findViewById(R.id.page_coinsHistories_explainTxtForEmptyList);
        this.historiesListView = view.findViewById(R.id.page_coinsHistories_historiesListView);
        this.listAdapter = new CoinsHistoriesListAdapter(this.requireContext(),
            R.layout.layout_coins_histories_item, this.histories);
        this.historiesListView.setAdapter(this.listAdapter);
        this.requestHistories();
        return view;
    }

    private void requestHistories() {
        var context = this;
        this.histories.clear();
        var jsonReq = new JsonObjectRequest(
            Request.Method.GET,
            BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/get-changing-coins-histories-of-user",
            null,
            success -> {
                try {
                    var response = APIUtilsHelper.mapListVolleySuccess(success);
                    this.histories.addAll(response.getData().stream()
                        .map(ChangingCoinsHistories::mapping)
                        .collect(Collectors.toList()));
                    this.listAdapter.notifyDataSetChanged();
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
                .requestData(null)
                .requestEnum(RequestEnums.COINS_HISTORIES_GET_HISTORIES)
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
    public void checkAndShowExplainTagIfEmptyList() {
        if (this.histories.isEmpty()) {
            this.historiesListView.setVisibility(View.GONE);
            this.explainTxtForEmptyList.setVisibility(View.VISIBLE);
            this.explainTxtForEmptyList.setText("There are no changes of your Coins.");
        } else {
            this.historiesListView.setVisibility(View.VISIBLE);
            this.explainTxtForEmptyList.setVisibility(View.GONE);
        }
    }

    @Override
    public void recall(JSONObject reqData, RequestEnums reqEnum) {
        if (reqEnum.equals(RequestEnums.COINS_HISTORIES_GET_HISTORIES))
            this.requestHistories();
    }
}