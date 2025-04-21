package com.restproject.mobile.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.restproject.mobile.fragments.AvailableSchedulesFragment;
import com.restproject.mobile.fragments.HomeFragment;

public class ViewPaperAdapter extends FragmentStatePagerAdapter {

    public ViewPaperAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
//            case 1:
//                return new PersonFragment();
//            case 2:
//                return new SettingsFragment();
            case 0:
                return new HomeFragment();
            case 1:
                return new AvailableSchedulesFragment();
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return 7;   //--Num of nNavBar items.
    }
}
