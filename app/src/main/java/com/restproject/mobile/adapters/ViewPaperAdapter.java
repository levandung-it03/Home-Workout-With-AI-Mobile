package com.restproject.mobile.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.restproject.mobile.fragments.AvailableSchedulesFragment;
import com.restproject.mobile.fragments.GenerateSchedulesFragment;
import com.restproject.mobile.fragments.HomeFragment;

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
            default:
                return new Fragment();
        }
    }


    @Override
    public int getItemCount() {
        return 7;
    }

    private Fragment pushToMap(int position, Fragment fragment) {
        fragments.put(position, fragment);
        return fragment;
    }

    public void refreshData(int position) {
        try {
            switch (position) {
//            case 1:
//                return new PersonFragment();
//            case 2:
//                return new SettingsFragment();
                case 0:
                    var home = (HomeFragment) this.fragments.get(position);
                    home.requestSchedules(home.getDataToRequestSchedules());
                    break;
                case 1:
                    var scheduleFrag = (AvailableSchedulesFragment) this.fragments.get(position);
                    scheduleFrag.requestMainUIListData(new JSONObject(Map.of("page", 1)));
                    break;
            }
        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }
    }
}
