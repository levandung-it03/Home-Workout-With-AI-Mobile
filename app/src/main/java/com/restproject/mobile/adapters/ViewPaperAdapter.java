package com.restproject.mobile.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.restproject.mobile.fragments.AvailableSchedulesFragment;
import com.restproject.mobile.fragments.DepositCoinsFragment;
import com.restproject.mobile.fragments.GenerateSchedulesFragment;
import com.restproject.mobile.fragments.HomeFragment;
import com.restproject.mobile.fragments.ProfileFragment;
import com.restproject.mobile.fragments.RefreshableFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewPaperAdapter extends FragmentStateAdapter {
    private final HashMap<Integer, Fragment> fragments = new HashMap<>();

    public ViewPaperAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return this.pushToMap(position, new HomeFragment());
            case 1:
                return this.pushToMap(position, new AvailableSchedulesFragment());
            case 2:
                return new GenerateSchedulesFragment();
            case 3:
                return this.pushToMap(position, new DepositCoinsFragment());
            case 4:
                return this.pushToMap(position, new ProfileFragment());
            default:
                return new Fragment();
        }
    }


    @Override
    public int getItemCount() {
        return 6;
    }

    private Fragment pushToMap(int position, Fragment fragment) {
        fragments.put(position, fragment);
        return fragment;
    }

    public void refreshData(int position) {
        try {
            switch (position) {
                case 0:
                    var home = (HomeFragment) this.fragments.get(position);
                    this.checkAndWaitUntilViewIsCreated(home);
                    break;
                case 1:
                    var scheduleFrag = (AvailableSchedulesFragment) this.fragments.get(position);
                    this.checkAndWaitUntilViewIsCreated(scheduleFrag);
                    break;
                case 3:
                    var depFrag = (DepositCoinsFragment) this.fragments.get(position);
                    this.checkAndWaitUntilViewIsCreated(depFrag);
                    break;
                case 4:
                    var profileFrag = (ProfileFragment) this.fragments.get(position);
                    this.checkAndWaitUntilViewIsCreated(profileFrag);
                    break;
            }
        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }
    }

    private void checkAndWaitUntilViewIsCreated(Fragment frag) {
        int[] maxTime = new int[] {0, 3_000, 100};
        new Thread(() -> {
            try {
                while (frag == null || !frag.isAdded() || frag.getView() == null) {
                    if (maxTime[0] == maxTime[1])
                        break;
                    maxTime[0] += maxTime[2];
                    Thread.sleep(maxTime[2]);
                }
                if (frag != null)
                    ((RefreshableFragment) frag).refresh();
            } catch (InterruptedException e) {
                e.fillInStackTrace();
            }
        }).start();
    }
}
