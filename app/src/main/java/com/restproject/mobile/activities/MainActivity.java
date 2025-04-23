package com.restproject.mobile.activities;

import static com.restproject.mobile.BuildConfig.BACKEND_ENDPOINT;
import static com.restproject.mobile.BuildConfig.PRIVATE_AUTH_DIR;
import static com.restproject.mobile.BuildConfig.PRIVATE_USER_DIR;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.restproject.mobile.R;
import com.restproject.mobile.adapters.ViewPaperAdapter;
import com.restproject.mobile.api_helpers.RequestInterceptor;
import com.restproject.mobile.exception.ApplicationException;
import com.restproject.mobile.fragments.LoginFragment;
import com.restproject.mobile.fragments.PreviewAvailableScheduleFragment;
import com.restproject.mobile.models.PreviewScheduleResponse;
import com.restproject.mobile.storage_helpers.InternalStorageHelper;
import com.restproject.mobile.utils.APIBuilderForGET;
import com.restproject.mobile.utils.APIUtilsHelper;
import com.restproject.mobile.utils.VolleyErrorHandler;

import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public ViewPager viewPager;
    public NavigationView navigationView;
    public ImageButton toggleBtn;
    public View navOverlay;
    public RelativeLayout dialog;
    public FrameLayout dialogFragment;
    public ImageButton closeDialogBtn;
    public Boolean isNavVisible = false;
    public Boolean isDialogVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        this.setContentView(R.layout.activity_main);
        this.viewPager = this.findViewById(R.id.mainLayout_viewPaper);
        this.viewPager.setOffscreenPageLimit(1);
        this.navigationView = this.findViewById(R.id.mainLayout_navBar);
        this.toggleBtn = this.findViewById(R.id.mainLayout_tglBtnNav);
        this.navOverlay = this.findViewById(R.id.mainLayout_overlay);
        this.dialog = this.findViewById(R.id.mainLayout_dialog);
        this.closeDialogBtn = this.findViewById(R.id.mainLayout_closeDialog);
        this.dialogFragment = this.findViewById(R.id.mainLayout_dialogContainer);

        if (this.missingTokensInDevice()) {
            this.showLoginFragment();
        } else {
            this.setUpNavigation();
            this.setUpDialog();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void openDialog() {
        this.dialog.setVisibility(View.VISIBLE);
        this.isDialogVisible = true;
    }

    private void setUpDialog() {
        this.dialog.setVisibility(View.GONE);
        this.closeDialogBtn.setOnClickListener(v -> {
            this.isDialogVisible = !this.isDialogVisible;
            this.dialog.setVisibility(this.isDialogVisible ? View.VISIBLE : View.GONE);
            if (!this.isDialogVisible) {
                this.dialogFragment.removeAllViews();
            }
        });
    }

    private boolean missingTokensInDevice() {
        String[] tokens = null;
        try {
            tokens = InternalStorageHelper.readIS(this, "tokens.txt").split(";");
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return tokens == null || tokens.length != 2;
    }

    public void setUpNavigation() {
        var context = this;
        this.navigationView.setNavigationItemSelectedListener(item -> {
            Menu menu = context.navigationView.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).isChecked()) {
                    if (menu.getItem(i).equals(item)) return false;
                    menu.getItem(i).setChecked(false);
                }
            }
            if (item.getItemId() == R.id.navBar_subscribeSchedule) {
                context.viewPager.setCurrentItem(1);
            } else if (item.getItemId() == R.id.navBar_generateSchedule) {
                context.viewPager.setCurrentItem(2);
            } else if (item.getItemId() == R.id.navBar_depositCoins) {
                context.viewPager.setCurrentItem(3);
            } else if (item.getItemId() == R.id.navBar_coinsHistories) {
                context.viewPager.setCurrentItem(4);
            } else if (item.getItemId() == R.id.navBar_profile) {
                context.viewPager.setCurrentItem(5);
            } else if (item.getItemId() == R.id.navBar_logout) {
                this.requestLogout();
            } else {
                context.viewPager.setCurrentItem(0);
            }
            return true;
        });
        this.viewPager.setAdapter(new ViewPaperAdapter(this.getSupportFragmentManager(),
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //--Update On Navigation Bar (which is chosen, then highlight it)
                if (position == 0)
                    context.navigationView.getMenu().findItem(R.id.navBar_home).setChecked(true);
                else if (position == 1)
                    context.navigationView.getMenu().findItem(R.id.navBar_subscribeSchedule).setChecked(true);
                else if (position == 2)
                    context.navigationView.getMenu().findItem(R.id.navBar_generateSchedule).setChecked(true);
                else if (position == 3)
                    context.navigationView.getMenu().findItem(R.id.navBar_depositCoins).setChecked(true);
                else if (position == 4)
                    context.navigationView.getMenu().findItem(R.id.navBar_coinsHistories).setChecked(true);
                else if (position == 5)
                    context.navigationView.getMenu().findItem(R.id.navBar_profile).setChecked(true);
            }

            @Override
            public void onPageScrolled(int p, float pOffset, int pOffsetPix) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        this.navigationView.getMenu().findItem(R.id.navBar_home).setChecked(true);
        this.toggleBtn.setVisibility(View.VISIBLE);
        this.toggleBtn.setScaleX(-1);
        this.toggleBtn.setOnClickListener(v -> {
            context.isNavVisible = !context.isNavVisible;   //--Toggle status
            context.navigationView.setVisibility(context.isNavVisible ? View.VISIBLE : View.GONE);
            context.navOverlay.setVisibility(context.isNavVisible ? View.VISIBLE : View.GONE);
            context.toggleBtn.setScaleX(context.isNavVisible ? 1 : -1);
        });
    }

    public void requestLogout() {
        try {
            var context = this;
            var reqData = new JSONObject(Map.of("token",
                InternalStorageHelper.readIS(context, "tokens.txt").split(";")[1]));
            var jsonReq = new JsonObjectRequest(
                Request.Method.POST,
                BACKEND_ENDPOINT + PRIVATE_AUTH_DIR + "/v1/logout",
                reqData,
                success -> {
                    var response = APIUtilsHelper.mapVolleySuccess(success);
                    Toast.makeText(this.getBaseContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                }, error ->
                Toast.makeText(context, APIUtilsHelper.readErrorFromVolley(error).getMessage(),
                    Toast.LENGTH_SHORT).show()
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    return RequestInterceptor.getPrivateAuthHeaders(context.getBaseContext());
                }
            };
            Volley.newRequestQueue(context.getBaseContext())
                .add(APIUtilsHelper.setVolleyRequestTimeOut(jsonReq, 30_000));
        } catch (ApplicationException e) {
            e.fillInStackTrace();
            Toast.makeText(this.getBaseContext(), "An Error occurred. Please restart app.",
                Toast.LENGTH_SHORT).show();
        }
        try {
            InternalStorageHelper.writeIS(this, "tokens.txt", "");
        } catch (ApplicationException e) {
            e.fillInStackTrace();
            Toast.makeText(this.getBaseContext(), "An Error occurred. Please restart app.",
                Toast.LENGTH_SHORT).show();
        }
        this.showLoginFragment();
    }

    public void showLoginFragment() {
        this.navigationView.setVisibility(View.GONE);
        this.navOverlay.setVisibility(View.GONE);
        this.toggleBtn.setVisibility(View.GONE);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.main, new LoginFragment())
            .commit();
    }

}