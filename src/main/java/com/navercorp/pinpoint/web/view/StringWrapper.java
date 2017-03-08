package com.navercorp.pinpoint.web.view;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 5/26/16.
 */
public class StringWrapper {
    private static final int DEFAULT_STR_LENGTH = 38;

    public static String wrap(Object input) {
        return String.valueOf(input);
    }

    public static String wrapStr(String input) {
        return input == null ? "N/A" : input;
    }

    public static String wrapDouble(Double input) {
        DecimalFormat df = new DecimalFormat("0.00");

        return df.format(input);
    }

    public static String wrapPercent(Double input) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(input * 100) + "%";
    }


    public static Double WrapPercentDouble(Double input) {
        BigDecimal bg = new BigDecimal(input * 100);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    public static String DefaultDateStr(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YY-MM-dd HH:mm");
        return simpleDateFormat.format(new Date(date));
    }


    public static String FullDateStr(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(date));
    }

    public static String FullDataStrMs(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss SSS");
        return simpleDateFormat.format(new Date(date));
    }

    public static Double wrapNumberDouble(Double input) {
        BigDecimal bg = new BigDecimal(input);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String simplifyStr(String str) {
        if (str.length() <= DEFAULT_STR_LENGTH) {
            return str;
        }

        return str.substring(0, DEFAULT_STR_LENGTH) + "...";
    }
}
