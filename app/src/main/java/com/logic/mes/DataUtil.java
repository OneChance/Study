package com.logic.mes;


public class DataUtil {
    public static int getIntValue(String v) {
        return v == null || v.equals("") ? 0 : Integer.parseInt(v);
    }

    public static double getDoubleValue(String v) {
        return v == null || v.equals("") ? 0 : Double.parseDouble(v);
    }

    public static double getDoubleValueNotZero(String v) {
        return v == null || v.equals("") ? 1 : Double.parseDouble(v);
    }
}
