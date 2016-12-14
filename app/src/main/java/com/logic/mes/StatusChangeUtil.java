package com.logic.mes;

import android.content.Context;
import android.widget.TextView;

/**
 * 文字状态变化工具
 */
public class StatusChangeUtil {
    private static StatusChangeUtil instance;

    public static StatusChangeUtil getInstance() {
        if (instance == null) {
            instance = new StatusChangeUtil();
        }
        return instance;
    }

    public void statusOk(Context context, TextView view) {
        view.setTextColor(context.getResources().getColor(R.color.success));
    }

    public void statusNormal(Context context, TextView view) {
        view.setTextColor(context.getResources().getColor(R.color.text));
    }
}
