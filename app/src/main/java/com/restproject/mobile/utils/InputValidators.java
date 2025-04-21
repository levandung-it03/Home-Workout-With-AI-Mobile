package com.restproject.mobile.utils;

import java.util.Objects;

public class InputValidators {

    public static boolean isValidStr(String str) {
        return (!Objects.isNull(str) && !str.isEmpty())
            || str.equals("false")
            || str.equals("0");
    }
}
