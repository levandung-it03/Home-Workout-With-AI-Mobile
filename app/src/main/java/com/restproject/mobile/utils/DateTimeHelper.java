package com.restproject.mobile.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DateTimeHelper {

    public static String formatDateTimeToStr(Date time) {
        @SuppressLint("SimpleDateFormat") var sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return sdf.format(time);
    }

    //--Format to: yyyy-MM-dd
    public static String formatDateTimeFromEdt(String dateEdt) {
        List<String> parts = Arrays
            .stream(dateEdt.contains("-") ? dateEdt.split("-") : dateEdt.split("/"))
            .collect(Collectors.toList());
        if (parts.size() != 3)  return null;

        StringBuilder result = new StringBuilder();
        //--Format: [dd, MM, yyyy]
        if (parts.get(2).length() == 4 || parts.get(2).length() == 3) {
            result.append(parts.get(2))
                .append("-").append(parts.get(1))
                .append("-").append(parts.get(0));
        } else {
            result.append(parts.get(0))
                .append("-").append(parts.get(1))
                .append("-").append(parts.get(2));
        }
        return result.toString();
    }

    public static String formatDateTimeFromGson(String dateAsArr) {
        StringBuilder temp = new StringBuilder(dateAsArr.replaceAll(" ", ""));
        StringBuilder result = new StringBuilder();
        temp.deleteCharAt(0);
        temp.deleteCharAt(temp.length() - 1);
        String[] parts = temp.toString().split(",");
        result.append(dblStrToInt(parts[3])).append(":")
            .append(dblStrToInt(parts[4])).append(":")
            .append(dblStrToInt(parts[5])).append(" ")
            .append(dblStrToInt(parts[2])).append("/")
            .append(dblStrToInt(parts[1])).append("/")
            .append(dblStrToInt(parts[0]));
        return result.toString();
    }

    private static int dblStrToInt(String str) {
        return (int) Double.parseDouble(str);
    }
}
