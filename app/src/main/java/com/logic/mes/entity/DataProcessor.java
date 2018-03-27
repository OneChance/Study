package com.logic.mes.entity;

public class DataProcessor {
    public static String NumberNotNull(String number) {
        return (number == null || number.equals("")) ? "0" : number;
    }
}
