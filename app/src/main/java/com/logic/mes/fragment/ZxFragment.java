package com.logic.mes.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.ProcessUtil;
import com.logic.mes.R;
import com.logic.mes.activity.MainActivity;
import com.logic.mes.adapter.ZxListAdapter;
import com.logic.mes.entity.process.ZxHead;
import com.logic.mes.entity.process.ZxProduct;
import com.logic.mes.entity.server.ProcessItem;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.util.List;
import java.util.Map;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ZxFragment extends BaseTagFragment implements ZxListAdapter.ButtonCallbacks, IScanReceiver, ProcessUtil.SubmitResultReceiver, ServerObserver.ServerDataReceiver {

    public ZxFragment() {
        this.tagNameId = R.string.zx_tab_name;
    }

    public final int SCAN_CODE_XZ = 0;
    public final int SCAN_CODE_HZ = 1;

    int currentReceiver = SCAN_CODE_XZ;
    String currentCode;

    @InjectView(R.id.zx_xh_v)
    TextView xhHead;
    @InjectView(R.id.zx_hh_v)
    TextView hhHead;

    @InjectView(R.id.zx_product_list)
    RecyclerView listView;

    @InjectView(R.id.zx_b_scan_xz)
    Button scanXz;
    @InjectView(R.id.zx_b_scan_hz)
    Button scanHz;

    @InjectView(R.id.zx_b_submit)
    Button submit;
    @InjectView(R.id.zx_b_clear)
    Button clear;

    ZxHead zx;
    ZxListAdapter dataAdapter;
    MainActivity activity;
    IScanReceiver receiver;
    ServerObserver serverObserver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;


    @Override
    public void setReceiver() {
        receiver = this;
        submitResultReceiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_XZ);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.zx, container, false);

        ButterKnife.inject(this, view);

        activity = (MainActivity) getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this, "zx", activity);

        scanXz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xhHead.setText(R.string.wait_scan);
                MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_XZ);
            }
        });

        scanHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (xhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.xz_scan_first, false);
                } else {
                    hhHead.setText(R.string.wait_scan);
                    MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_HZ);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSubmit("zx");
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        if (zx == null) {
            zx = new ZxHead();
        }

        dataAdapter = new ZxListAdapter(getActivity(), zx.getDetailList(), this);
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);
        helper.attachToRecyclerView(listView);
        helper.setCloseOnAction(false);

        return view;
    }

    public void toSubmit(String code) {
        if (xhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
            MyApplication.toast(R.string.xz_scan_first, false);
        } else if (zx.getDetailList().size() == 0) {
            MyApplication.toast(R.string.hz_scan_need, false);
        } else {
            zx.setCode(code);
            zx.setBagCode(zx.getDetailList().get(0).getXh());
            new ProcessUtil(activity).submit(submitResultReceiver, zx, userInfo.getUser());
        }
    }

    @Override
    public void removePosition(int position) {
        dataAdapter.removePosition(position);
    }

    @Override
    public void scanReceive(String res, int scanCode) {

        ProcessItem item = new ProcessItem();

        if (scanCode == SCAN_CODE_XZ) {
            xhHead.setText(res);
            item.setItemKey("CaseInfo");
            item.setItemValue(res);
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).checkData(item))
                    .subscribe(serverObserver);
            currentReceiver = SCAN_CODE_XZ;
        } else if (scanCode == SCAN_CODE_HZ) {
            currentCode = res;
            item.setItemKey("BoxCode");
            item.setItemValue(currentCode);
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).checkData(item))
                    .subscribe(serverObserver);
            currentReceiver = SCAN_CODE_HZ;
        }
    }

    @Override
    public void scanError() {

    }

    private boolean checkExist(String code) {
        for (ZxProduct product : zx.getDetailList()) {
            if (product.getHh().equals(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void submitOk() {
        doAfterSumbit(xhHead.getText().toString(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(xhHead.getText().toString(), false);
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void serverData() {
        if (currentReceiver == SCAN_CODE_XZ) {
            //如果有之前提交的数据，带出
            if (data != null && data.getDatas() != null && data.getDatas().getBagDatas() != null) {
                List<Map<String, String>> mapList = data.getDatas().getBagDatas();
                if (mapList.size() > 0) {
                    for (Map<String, String> map : mapList) {
                        ZxProduct p = new ZxProduct();
                        p.setXh(map.get("caseCode"));
                        p.setHh(map.get("boxCode"));
                        p.setDb(map.get("zh_db"));

                        if (map.get("boxdj") != null && !map.get("boxdj").equals("")) {
                            p.setLevel(map.get("boxdj"));
                        }

                        zx.getDetailList().add(p);
                    }
                    dataAdapter.notifyDataSetChanged();
                }
            }
            MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_HZ);
        } else {
            fillData();
        }
    }

    public void fillData() {
        if (zx.getDetailList().size() < 4) {
            if (!checkExist(currentCode)) {
                if (!levelDiff(data.getVal("boxdj"))) {
                    if (!dbDiff(data.getVal("zh_db"))) {

                        hhHead.setText(currentCode);
                        ZxProduct p = new ZxProduct();
                        p.setXh(xhHead.getText().toString());
                        p.setHh(currentCode);

                        if (data != null && data.getVal("boxdj") != null && !data.getVal("boxdj").equals("")) {
                            p.setLevel(data.getVal("boxdj"));
                        }

                        if (data != null && data.getVal("zh_db") != null && !data.getVal("zh_db").equals("")) {
                            p.setDb(data.getVal("zh_db"));
                            activity.setStatus(MyApplication.getResString(R.string.db_type) + data.getVal("zh_db"), true);
                        }

                        zx.getDetailList().add(p);

                        dataAdapter.notifyDataSetChanged();
                    } else {
                        MyApplication.toast(R.string.db_diff, false);
                    }
                } else {
                    MyApplication.toast(R.string.hz_dj_diff, false);
                }
            } else {
                MyApplication.toast(R.string.hz_duplicate, false);
            }
        } else {
            MyApplication.toast(R.string.zx_size_full, false);
        }
    }

    public void clear() {
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_XZ);
        xhHead.setText(R.string.wait_scan);
        hhHead.setText(R.string.wait_scan);
        zx.getDetailList().clear();
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void serverError(Throwable e) {
        if (currentReceiver == SCAN_CODE_HZ) {
            fillData();
        } else {
            MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_HZ);
        }
    }

    @Override
    public void preventSubmit() {

    }

    @Override
    public void ableSubmit() {

    }

    public boolean levelDiff(String level) {

        if (level != null && !level.equals("")) {
            for (ZxProduct product : zx.getDetailList()) {
                if (product.getLevel() != null && !product.getLevel().equals("") && !product.getLevel().equals(level)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean dbDiff(String db) {
        if (db != null && !db.equals("")) {
            for (ZxProduct product : zx.getDetailList()) {
                if (product.getDb() != null && !product.getDb().equals("") && !product.getDb().equals(db)) {
                    return true;
                }
            }
        }

        return false;
    }
}
