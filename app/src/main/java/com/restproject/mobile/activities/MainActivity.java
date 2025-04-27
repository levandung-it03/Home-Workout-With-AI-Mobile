package com.restproject.mobile.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.restproject.mobile.R;
import com.restproject.mobile.adapters.ViewPaperAdapter;
import com.restproject.mobile.fragments.LoginFragment;
import com.restproject.mobile.storage_helpers.InternalStorageHelper;

public class MainActivity extends AppCompatActivity {
    public ViewPager2 viewPager;
    public NavigationView navigationView;
    public ImageButton toggleBtn;
    public View navOverlay;
    public RelativeLayout dialog;
    public FrameLayout dialogFragment;
    public ImageButton closeDialogBtn;
    public ViewPaperAdapter viewPaperAdapter;
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
            this.isDialogVisible = false;
            this.dialog.setVisibility(View.GONE);
            this.dialogFragment.removeAllViews();
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

    @SuppressLint("NotifyDataSetChanged")
    public void setUpNavigation() {
        var context = this;
        this.viewPaperAdapter = new ViewPaperAdapter(this);
        this.viewPager.setAdapter(this.viewPaperAdapter);
        this.navigationView.setNavigationItemSelectedListener(item -> {
            Menu menu = context.navigationView.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).isChecked()) {
                    if (menu.getItem(i).equals(item)) return false;
                    menu.getItem(i).setChecked(false);
                }
            }
            closeDialogBtn.callOnClick();
            if (item.getItemId() == R.id.navBar_subscribeSchedule) {
                context.viewPager.setCurrentItem(1);
                context.viewPaperAdapter.refreshData(1);
            } else if (item.getItemId() == R.id.navBar_generateSchedule) {
                context.viewPager.setCurrentItem(2);
            } else if (item.getItemId() == R.id.navBar_depositCoins) {
                context.viewPager.setCurrentItem(3);
                context.viewPaperAdapter.refreshData(3);
            } else if (item.getItemId() == R.id.navBar_coinsHistories) {
                context.viewPager.setCurrentItem(4);
                context.viewPaperAdapter.refreshData(4);
            } else if (item.getItemId() == R.id.navBar_profile) {
                context.viewPager.setCurrentItem(5);
                context.viewPaperAdapter.refreshData(5);
            } else {
                context.viewPager.setCurrentItem(0);
                context.viewPaperAdapter.refreshData(0);
            }
            this.toggleBtn.callOnClick();
            return true;
        });
        this.viewPager.setUserInputEnabled(false);
        this.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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

        });

        Menu menu = this.navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++)
            if (menu.getItem(i).isChecked())
                menu.getItem(i).setChecked(false);
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