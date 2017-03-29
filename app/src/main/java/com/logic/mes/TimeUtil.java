package com.logic.mes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    private static SimpleDateFormat sdf;

    public static String getNow() {
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        return sdf.format(new Date());
    }

    public static boolean isValidDateTime(String dateTime) {

        if (dateTime != null && !dateTime.equals("")) {
            try {

                long localTime = new Date().getTime();
                long serverTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime).getTime();

                if (Math.abs(localTime - serverTime) > 10 * 60 * 1000) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
