package com.logic.mes.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.logic.mes.activity.MainActivity;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.entity.server.ServerResult;

public class BaseTagFragment extends Fragment {
    public int tagNameId = -1;

    public ServerResult data;

    public UserInfo userInfo;

    public void setReceiver() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        userInfo = ((MainActivity) getActivity()).getUserInfo();

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
