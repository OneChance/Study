package com.logic.mes.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.activity.MainActivity;
import com.logic.mes.adapter.ZtListAdapter;
import com.logic.mes.entity.process.ZtHead;
import com.logic.mes.entity.process.ZtProduct;
import com.logic.mes.entity.process.ZxProduct;
import com.logic.mes.entity.server.ProcessItem;
import com.logic.mes.ProcessUtil;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.util.List;
import java.util.Map;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ZtFragment extends BaseTagFragment implements ZtListAdapter.ButtonCallbacks, IScanReceiver, ProcessUtil.SubmitResultReceiver, ServerObserver.ServerDataReceiver {

    public ZtFragment() {
        this.tagNameId = R.string.zt_tab_name;
    }

    public final int SCAN_CODE_TH = 0;
    public final int SCAN_CODE_XZ = 1;

    int currentReceiverCode = SCAN_CODE_TH;
    String currentCode;


    @InjectView(R.id.zt_th_v)
    TextView thHead;
    @InjectView(R.id.zt_xh_v)
    TextView xhHead;
    @InjectView(R.id.zt_x_hj)
    TextView sumXz;

    @InjectView(R.id.zt_b_scan_th)
    Button scanTh;
    @InjectView(R.id.zt_b_scan_xz)
    Button scanXh;

    @InjectView(R.id.zt_product_list)
    RecyclerView listView;

    @InjectView(R.id.zt_b_submit)
    Button submit;
    @InjectView(R.id.zt_b_clear)
    Button clear;

    ZtHead zt;
    ZtListAdapter dataAdapter;
    MainActivity activity;
    IScanReceiver receiver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    ServerObserver serverObserver;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_TH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.zt, container, false);

        ButterKnife.inject(this, view);

        activity = (MainActivity) getActivity();
        receiver = this;
        submitResultReceiver = this;
        serverObserver = new ServerObserver(this, "zt", activity);

        scanTh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thHead.setText(R.string.wait_scan);
                MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_TH);
            }
        });

        scanXh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.th_scan_first, false);
                } else {
                    xhHead.setText(R.string.wait_scan);
                    MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_XZ);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSubmit("zt");
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        if (zt == null) {
            zt = new ZtHead();
        }

        dataAdapter = new ZtListAdapter(getActivity(), zt.getDetailList(), this);
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);
        helper.attachToRecyclerView(listView);
        helper.setCloseOnAction(false);

        return view;
    }

    public void toSubmit(String code) {
        if (thHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
            MyApplication.toast(R.string.th_scan_first, false);
        } else if (zt.getDetailList().size() == 0) {
            MyApplication.toast(R.string.xz_scan_need, false);
        } else {
            zt.setCode(code);
            zt.setBagCode(zt.getDetailList().get(0).getTh());
            new ProcessUtil(activity).submit(submitResultReceiver, zt, userInfo.getUser());
        }
    }

    @Override
    public void removePosition(int position) {
        dataAdapter.removePosition(position);
        changeSumXz(-1);
    }

    @Override
    public void scanReceive(String res, int scanCode) {

        ProcessItem item = new ProcessItem();

        if (scanCode == SCAN_CODE_TH) {
            thHead.setText(res);
            item.setItemKey("TorrInfo");
            item.setItemValue(res);
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).checkData(item))
                    .subscribe(serverObserver);
            currentReceiverCode = SCAN_CODE_TH;
        } else if (scanCode == SCAN_CODE_XZ) {
            currentCode = res;
            item.setItemKey("CaseCode");
            item.setItemValue(currentCode);
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).checkData(item))
                    .subscribe(serverObserver);
            currentReceiverCode = SCAN_CODE_XZ;
        }
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void serverData() {
        if (currentReceiverCode == SCAN_CODE_TH) {
            //如果有之前提交的数据，带出
            if (data != null && data.getDatas() != null && data.getDatas().getBagDatas() != null) {
                List<Map<String, String>> mapList = data.getDatas().getBagDatas();
                if (mapList.size() > 0) {
                    for (Map<String, String> map : mapList) {
                        ZtProduct p = new ZtProduct();
                        p.setTh(map.get("torrCode"));
                        p.setXh(map.get("caseCode"));
                        p.setDb(map.get("db"));
                        zt.getDetailList().add(p);
                    }
                    changeSumXz(zt.getDetailList().size());
                    dataAdapter.notifyDataSetChanged();
                }
            }
            MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_XZ);
        } else {
            fillData();
        }
    }

    public void fillData() {

        if (zt.getDetailList().size() < 30) {

            if (!checkExist(currentCode)) {

                if (!dbDiff(data.getVal("db"))) {
                    xhHead.setText(currentCode);
                    ZtProduct p = new ZtProduct();
                    p.setTh(thHead.getText().toString());
                    p.setXh(currentCode);

                    if (data != null && data.getVal("db") != null && !data.getVal("db").equals("")) {
                        p.setDb(data.getVal("db"));
                        activity.setStatus(MyApplication.getResString(R.string.db_type) + data.getVal("db"), true);
                    }

                    zt.getDetailList().add(p);
                    changeSumXz(1);
                    dataAdapter.notifyDataSetChanged();
                } else {
                    MyApplication.toast(R.string.hz_dj_diff, false);
                }

            } else {
                MyApplication.toast(R.string.xz_duplicate, false);
            }
        } else {
            MyApplication.toast(R.string.zt_size_full, false);
        }
    }

    public void clear() {
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_TH);
        thHead.setText(R.string.wait_scan);
        xhHead.setText(R.string.wait_scan);
        zt.getDetailList().clear();
        sumXz.setText("0");
        dataAdapter.notifyDataSetChanged();
    }

    private boolean checkExist(String code) {
        for (ZtProduct product : zt.getDetailList()) {
            if (product.getXh().equals(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void serverError(Throwable e) {
        if (currentReceiverCode == SCAN_CODE_XZ) {
            fillData();
        } else {
            MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_XZ);
        }
    }

    @Override
    public void submitOk() {
        doAfterSumbit(thHead.getText().toString(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(thHead.getText().toString(), false);
    }

    @Override
    public void preventSubmit() {

    }

    @Override
    public void ableSubmit() {

    }

    public void changeSumXz(int changeNum) {
        int sumXzNumber = Integer.parseInt(sumXz.getText().toString());
        sumXzNumber += changeNum;
        sumXz.setText((sumXzNumber + ""));
    }

    public boolean dbDiff(String db) {
        if (db != null && !db.equals("")) {
            for (ZtProduct product : zt.getDetailList()) {
                if (product.getDb() != null && !product.getDb().equals("") && !product.getDb().equals(db)) {
                    return true;
                }
            }
        }

        return false;
    }
}
