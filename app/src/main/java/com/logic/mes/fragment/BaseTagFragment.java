package com.logic.mes.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.activity.MainActivity;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.entity.server.ServerResult;

import java.util.ArrayList;
import java.util.List;

public class BaseTagFragment extends Fragment {
    public int tagNameId = -1;

    public ServerResult data;

    public UserInfo userInfo;

    MainActivity activity;

    public void setReceiver() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();
        userInfo = activity.getUserInfo();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void clear() {

    }

    /**
     * 在提交之后,在提交状态栏显示操作结果
     *
     * @param no      操作的单据号
     * @param success 是否成功
     */
    public void doAfterSumbit(String no, boolean success) {

        String status = MyApplication.getResString(R.string.submit_ok);

        if (!success) {
            status = MyApplication.getResString(R.string.data_save);
        }

        activity.setStatus(no + status, success);

        clear();
    }
}
