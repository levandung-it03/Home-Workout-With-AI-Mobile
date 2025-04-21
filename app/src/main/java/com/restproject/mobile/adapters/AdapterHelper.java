package com.restproject.mobile.adapters;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.restproject.mobile.R;

public class AdapterHelper {

    @SuppressLint("ResourceAsColor")
    public static void checkAndChangeLevelTag(TextView levelTag, String level) {
        level = level.toUpperCase();
        levelTag.setText(level);
        switch (level) {
            case "BEGINNER":
                levelTag.setTextColor(R.color.dark_blue_700);
                levelTag.setBackgroundResource(R.drawable.bg_level_begginner);
                break;
            case "INTERMEDIATE":
                levelTag.setTextColor(R.color.normal_green_tree);
                levelTag.setBackgroundResource(R.drawable.bg_level_intermediate);
                break;
            case "ADVANCED":
                levelTag.setTextColor(R.color.normal_gold);
                levelTag.setBackgroundResource(R.drawable.bg_level_advance);
                break;
        }
    }
}
