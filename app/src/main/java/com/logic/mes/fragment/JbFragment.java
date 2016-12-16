package com.logic.mes.fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.ProcessUtil;
import com.logic.mes.R;
import com.logic.mes.adapter.JbListAdapter;
import com.logic.mes.entity.base.GroupCancelInfo;
import com.logic.mes.entity.process.JbDetail;
import com.logic.mes.entity.process.JbProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class JbFragment extends BaseTagFragment implements IScanReceiver, ProcessUtil.SubmitResultReceiver, ServerObserver.ServerDataReceiver {

    @InjectView(R.id.jb_v_brick)
    TextView brick;
    @InjectView(R.id.jb_product_list)
    RecyclerView listView;
    @InjectView(R.id.jb_b_submit)
    Button submit;
    @InjectView(R.id.jb_v_reason)
    Spinner reason;
    @InjectView(R.id.jb_ratio_group)
    RadioGroup lx;
    @InjectView(R.id.jb_kz)
    RadioButton kzRb;
    @InjectView(R.id.jb_bf)
    RadioButton bfRb;


    JbProduct jb;
    List<JbDetail> jbDetailList;
    JbListAdapter dataAdapter;
    FragmentActivity activity;
    IScanReceiver receiver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    ServerObserver serverObserver;

    public JbFragment() {
        this.tagNameId = R.string.jb_tab_name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.jb, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this, "jb", activity);
        submitResultReceiver = this;

        if (jb == null) {
            jb = new JbProduct();
        }

        if (jbDetailList == null) {
            jbDetailList = new ArrayList<>();
        }

        final List<String> reasons = new ArrayList<>();
        
        List<GroupCancelInfo> infos = userInfo.getGroupCancelInfo();

        for (GroupCancelInfo info : infos) {
            reasons.add(info.getItemKey());
        }

        reasons.add(MyApplication.getResString(R.string.wait_choose));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, reasons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reason.setAdapter(adapter);

        reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                jb.setReason(reasons.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lx.clearCheck();

        lx.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                String lx = "";

                if (i == kzRb.getId()) {
                    lx = kzRb.getText().toString();
                } else {
                    lx = bfRb.getText().toString();
                }

                jb.setLx(lx);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jb.getReason().equals(MyApplication.getResString(R.string.wait_choose))) {
                    MyApplication.toast(R.string.form_required, false);
                } else if (jb.getLx().equals("")) {
                    MyApplication.toast(R.string.form_required, false);
                } else {
                    new ProcessUtil(activity).submit(submitResultReceiver, jb, userInfo.getUser());
                }
            }
        });

        jbDetailList.clear();

        dataAdapter = new JbListAdapter(getActivity(), jbDetailList);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);

        return view;
    }

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        brick.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickGroup(res, "jb"))
                .subscribe(serverObserver);
    }

    @Override
    public void scanError() {

    }

    @Override
    public void submitOk() {

    }

    @Override
    public void submitError() {

    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void serverData() {

        jbDetailList.clear();

        if (data.getDatas() != null && data.getDatas().getBagDatas() != null) {
            for (Map<String, String> bag : data.getDatas().getBagDatas()) {
                JbDetail jbDetail = new JbDetail();
                jbDetail.setStation(bag.get("pb_gw"));
                jbDetail.setBrickId(bag.get("ej_BrickID"));
                jbDetail.setLength(bag.get("pbj_yxbc"));
                jbDetail.setLevel(bag.get("ej_jzdj"));
                jbDetailList.add(jbDetail);
            }
        }

        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void serverError(Throwable e) {

    }

    @Override
    public void preventSubmit() {
        submit.setVisibility(View.INVISIBLE);
    }

    @Override
    public void ableSubmit() {
        submit.setVisibility(View.VISIBLE);
    }

    @Override
    public void clear() {

        brick.setText(R.string.wait_scan);
        reason.setSelection(0);
        lx.clearCheck();
        jb.setLx("");
        jb.setBrickId("");
        jb.setReason("");
        jbDetailList.clear();
        dataAdapter.notifyDataSetChanged();
    }
}
