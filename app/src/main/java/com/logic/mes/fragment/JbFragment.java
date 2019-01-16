package com.logic.mes.fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.ProcessUtil;
import com.logic.mes.R;
import com.logic.mes.activity.MainActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class JbFragment extends BaseTagFragment implements IScanReceiver, ProcessUtil.SubmitResultReceiver, ServerObserver.ServerDataReceiver, JbListAdapter.JbDetailCallback {

    @BindView(R.id.jb_v_brick)
    TextView brick;
    @BindView(R.id.jb_product_list)
    RecyclerView listView;
    @BindView(R.id.jb_b_submit)
    Button submit;
    @BindView(R.id.jb_v_reason)
    Spinner reason;


    JbProduct jb;
    JbListAdapter dataAdapter;
    MainActivity activity;
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

        ButterKnife.bind(this, view);

        activity = (MainActivity) getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this, "jb", activity);
        submitResultReceiver = this;

        if (jb == null) {
            jb = new JbProduct();
            jb.setJbDetailList(new ArrayList<JbDetail>());
        }

        final List<String> reasons = new ArrayList<>();

        List<GroupCancelInfo> infos = userInfo.getGroupCancelInfo();

        reasons.add(MyApplication.getResString(R.string.wait_choose));

        for (GroupCancelInfo info : infos) {
            reasons.add(info.getItemKey());
        }

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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jb.getReason().equals(MyApplication.getResString(R.string.wait_choose))) {
                    MyApplication.toast(R.string.form_required, false);
                } else {
                    new ProcessUtil(activity).submit(submitResultReceiver, jb, userInfo.getUser());
                }
            }
        });

        dataAdapter = new JbListAdapter(getActivity(), jb.getJbDetailList(), this);
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
        jb.setBrickId(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickGroup(res, "jb"))
                .subscribe(serverObserver);
    }

    @Override
    public void scanError() {

    }

    @Override
    public void submitOk() {
        doAfterSumbit(jb.getBrickId(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(jb.getBrickId(), false);
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void serverData() {

        jb.getJbDetailList().clear();

        if (data.getDatas() != null && data.getDatas().getBagDatas() != null) {
            for (Map<String, String> bag : data.getDatas().getBagDatas()) {
                JbDetail jbDetail = new JbDetail();
                jbDetail.setStation(bag.get("pb_gw"));
                jbDetail.setBrickId(bag.get("ej_BrickID"));
                jbDetail.setLength(bag.get("pbj_yxbc"));
                jbDetail.setSfbf(MyApplication.getResString(R.string.no));
                jb.getJbDetailList().add(jbDetail);
                if(jb.getGroupId()==null){
                    jb.setGroupId(bag.get("ej_groupId"));
                }
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
        jb.setBrickId("");
        jb.setReason("");
        jb.getJbDetailList().clear();
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void checkedChange(int position, boolean checked) {
        if (checked) {
            jb.getJbDetailList().get(position).setSfbf(MyApplication.getResString(R.string.yes));
        } else {
            jb.getJbDetailList().get(position).setSfbf(MyApplication.getResString(R.string.no));
        }
    }
}
