package com.restproject.mobile.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.restproject.mobile.fragments.AvailableSchedulesFragment;
import com.restproject.mobile.fragments.GenerateSchedulesFragment;
import com.restproject.mobile.fragments.HomeFragment;
import com.restproject.mobile.fragments.ProfileFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewPaperAdapter extends FragmentStateAdapter {
    private HashMap<Integer, Fragment> fragments = new HashMap<>();

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
            case 5:
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
                    if (this.checkAndWaitUntilViewIsCreated(home))
                        home.requestSchedules(home.getDataToRequestSchedules());
                    break;
                case 1:
                    var scheduleFrag = (AvailableSchedulesFragment) this.fragments.get(position);
                    if (this.checkAndWaitUntilViewIsCreated(scheduleFrag))
                        scheduleFrag.requestMainUIListData(new JSONObject(Map.of("page", 1)));
                    break;
                case 5:
                    var profileFrag = (ProfileFragment) this.fragments.get(position);
                    if (this.checkAndWaitUntilViewIsCreated(profileFrag))
                        profileFrag.requestUserInfo();
                    break;
            }
        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }
    }

    private boolean checkAndWaitUntilViewIsCreated(Fragment frag) {
        int[] maxTime = new int[] {0, 3_000, 100};
        boolean[] result = new boolean[] {false};
        new Thread(() -> {
            try {
                while (frag == null || !frag.isAdded() || frag.getView() == null) {
                    if (maxTime[0] == maxTime[1])
                        break;
                    maxTime[0] += maxTime[2];
                    wait(maxTime[2]);
                }
                result[0] = maxTime[0] < 4_000;
            } catch (InterruptedException e) {
                e.fillInStackTrace();
            }
        });
        return result[0];
    }
}
