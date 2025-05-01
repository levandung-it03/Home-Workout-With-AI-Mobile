package com.restproject.mobile.fragments;

import static com.restproject.mobile.BuildConfig.BACKEND_ENDPOINT;
import static com.restproject.mobile.BuildConfig.PRIVATE_USER_DIR;
import static com.restproject.mobile.utils.InputValidators.getEdtStr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.restproject.mobile.BuildConfig;
import com.restproject.mobile.R;
import com.restproject.mobile.activities.PrivateUIObject;
import com.restproject.mobile.activities.RequestEnums;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.models.SepayQRResponse;
import com.restproject.mobile.utils.APIBuilderForGET;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Map;

public class DepositCoinsFragment extends Fragment implements PrivateUIObject, RefreshableFragment {
    private final int[] retryCount = new int[]{10};
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SepayQRResponse currentDataResponse = new SepayQRResponse();
    private TextView bankAccount, bankName, sentContent;
    private EditText coinsAmount;
    private LinearLayout getQrBlock, showQrToUserBlock;
    private ImageView qrFrame;

    public DepositCoinsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deposit_coins, container, false);
        this.bankAccount = view.findViewById(R.id.page_depositCoins_bankAccount);
        this.coinsAmount = view.findViewById(R.id.page_depositCoins_coinsAmount);
        this.getQrBlock = view.findViewById(R.id.page_depositCoins_getQrBlock);
        this.showQrToUserBlock = view.findViewById(R.id.page_depositCoins_showQrToUserBlock);
        this.bankName = view.findViewById(R.id.page_depositCoins_bankName);
        this.sentContent = view.findViewById(R.id.page_depositCoins_sentContent);
        this.qrFrame = view.findViewById(R.id.page_depositCoins_qrFrame);
        RelativeLayout turnBackVirtualBtn = view.findViewById(R.id.page_depositCoins_preBlockBtn);
        Button getQrBtn = view.findViewById(R.id.page_depositCoins_getQrBtn);

        turnBackVirtualBtn.setOnClickListener(v -> this.setUpTurnBackBtn());
        getQrBtn.setOnClickListener(v -> {
            int coinsAmount = Integer.parseInt(getEdtStr(this.coinsAmount));
            if (coinsAmount < 0) {
                Toast.makeText(this.getContext(), "Invalid Coins Amount", Toast.LENGTH_SHORT).show();
                return;
            }
            this.requestGetQrByCoins(new JSONObject(Map.of("coinsAmount", coinsAmount)));
        });
        return view;
    }

    private void requestGetQrByCoins(JSONObject requestData) {
        var context = this;
        var reqUrl = APIBuilderForGET.parseFromJsonObject(requestData,
            BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/get-sepay-qr-url");
        var jsonReq = new JsonObjectRequest(Request.Method.GET, reqUrl, null,
            success -> {
                var response = APIUtilsHelper.mapVolleySuccess(success);
                this.currentDataResponse = SepayQRResponse.mapping(response.getData());
                this.openAndInitializeQrBlock();
            }, error ->
            APIUtilsHelper.handlePrivateVolleyRequestError(VolleyErrorHandler.builder()
                .activity((AppCompatActivity) context.requireActivity())
                .context(context.getContext())
                .app(context)
                .requestData(requestData)
                .requestEnum(RequestEnums.DEPOSIT_COINS_GET_URL)
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

    private void openAndInitializeQrBlock() {
        this.getQrBlock.setVisibility(View.GONE);
        this.showQrToUserBlock.setVisibility(View.VISIBLE);
        this.bankAccount.setText(this.currentDataResponse.getAccountTarget());
        this.bankName.setText(this.currentDataResponse.getBankName());
        this.sentContent.setText(this.currentDataResponse.getDescription());
        Picasso.get().load(this.currentDataResponse.getUrl()).into(this.qrFrame);

        this.handler.postDelayed(() -> {
            try {
                System.out.println("10s is end, start to listen transaction status!");
                this.retryCount[0] = 0; //--Reset Retry Count.
                this.checkTransactionStatus();
            } catch (NullPointerException e) {
                System.out.println("Detroyed Handler from DepositCoins");
            }
        }, 10_000); //--Wait 10 seconds first (stimulating User's transaction progress).
    }

    /**
     * Request 40 times with 4s/req to check transaction's status.
     */
    private void checkTransactionStatus() {
        try {
            if (this.retryCount[0] >= 40) { //--After 40 tries, show a message
                Toast.makeText(this.getContext(),
                    "Transaction is lengthy so the result will not be noticed directly",
                    Toast.LENGTH_LONG).show();
                this.retryCount[0] = 0;    //--Reset Retry Count (after 40 times retrying unsuccessfully).
                return;
            }
            System.out.println("[checkTransactionStatus]: retry-" + this.retryCount[0]);
            var requestData = new JSONObject(Map.of("description",
                this.currentDataResponse.getDescription()));
            this.requestGetTransactionStatus(requestData);
        } catch (NullPointerException e) {
            System.out.println("Detroyed Handler from DepositCoins");
        }
    }

    private void requestGetTransactionStatus(JSONObject requestData) {
        var context = this;
        Volley.newRequestQueue(context.requireContext())
            .add(APIUtilsHelper.setVolleyRequestTimeOut(new JsonObjectRequest(
                Request.Method.GET,
                APIBuilderForGET.parseFromJsonObject(requestData,
                    BACKEND_ENDPOINT + PRIVATE_USER_DIR + "/v1/get-deposit-status"),
                null,
                success -> {
                    try {
                        var response = APIUtilsHelper.mapVoidVolleySuccess(success);
                        System.out.println("--Result: " + response.getMessage());
                        if (response.getApplicationCode().equals(BuildConfig.DEPOSIT_COINS_PROCESSING_SUC_CODE)) {
                            //--Retry after 4 seconds
                            new android.os.Handler().postDelayed(() -> {
                                this.retryCount[0]++;
                                context.checkTransactionStatus();
                            }, 4_000);
                        } else {
                            Toast.makeText(context.requireContext(), response.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        System.out.println("Detroyed Handler from DepositCoins");
                    }
                }, error ->
                APIUtilsHelper.handlePrivateVolleyRequestError(VolleyErrorHandler.builder()
                    .activity((AppCompatActivity) context.requireActivity())
                    .context(context.getContext())
                    .app(context)
                    .requestData(requestData)
                    .requestEnum(RequestEnums.DEPOSIT_COINS_GET_TRANS_STATUS)
                    .error(error))
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    return RequestInterceptor.getPrivateHeaders(context.getContext());
                }
            }, 30_000));
    }

    private void setUpTurnBackBtn() {
        this.bankAccount.setText("");
        this.bankName.setText("");
        this.sentContent.setText("");
        this.retryCount[0] = 0;
        this.currentDataResponse = new SepayQRResponse();
        this.getQrBlock.setVisibility(View.VISIBLE);
        this.showQrToUserBlock.setVisibility(View.GONE);
    }

    public void refreshData() {
        this.setUpTurnBackBtn();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void recall(JSONObject reqData, RequestEnums reqEnum) {
        if (reqEnum.equals(RequestEnums.DEPOSIT_COINS_GET_URL))
            this.requestGetQrByCoins(reqData);
        if (reqEnum.equals(RequestEnums.DEPOSIT_COINS_GET_TRANS_STATUS))
            this.requestGetQrByCoins(reqData);

    }

    @Override
    public void refresh() {
        requireActivity().runOnUiThread(() -> {
            this.handler.removeCallbacksAndMessages(null);
            this.setUpTurnBackBtn();
        });
    }
}