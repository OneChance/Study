package com.logic.mes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.logic.mes.R;

public class ZbFragment extends BaseTagFragment {

    public ZbFragment() {
        this.tagNameId = R.string.zb_tab_name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.zb, container, false);

        return view;
    }
}
