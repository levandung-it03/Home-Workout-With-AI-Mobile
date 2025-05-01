package com.restproject.mobile.utils;

import android.widget.EditText;

import java.util.Objects;

public class InputValidators {

    public static String getEdtStr(EditText view) {
        return view.getText().toString().trim();
    }

    public static boolean isValidStr(String str) {
        return (!Objects.isNull(str) && !str.isEmpty())
            || str.equals("false")
            || str.equals("0");
    }
}
