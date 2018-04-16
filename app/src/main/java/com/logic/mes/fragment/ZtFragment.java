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
import com.logic.mes.adapter.ZtListAdapter;
import com.logic.mes.entity.process.ZtHead;
import com.logic.mes.entity.process.ZtProduct;
import com.logic.mes.entity.server.ProcessItem;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.util.List;
import java.util.Map;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ZtFragment extends BaseTagFragment implements ZtListAdapter.ButtonCallbacks, IScanReceiver, ProcessUtil.SubmitResultReceiver, ServerObserver.ServerDataReceiver {

    public ZtFragment() {
        this.tagNameId = R.string.zt_tab_name;
    }

    public final int SCAN_CODE_TH = 0;
    public final int SCAN_CODE_XZ = 1;

    int currentReceiverCode = SCAN_CODE_TH;
    String currentCode;


    @BindView(R.id.zt_th_v)
    TextView thHead;
    @BindView(R.id.zt_xh_v)
    TextView xhHead;

    @BindView(R.id.zt_b_scan_th)
    Button scanTh;
    @BindView(R.id.zt_b_scan_xz)
    Button scanXh;

    @BindView(R.id.zt_product_list)
    RecyclerView listView;

    @BindView(R.id.zt_b_submit)
    Button submit;
    @BindView(R.id.zt_b_clear)
    Button clear;

    @BindView(R.id.zt_hs)
    TextView hs;
    @BindView(R.id.zt_ps)
    TextView ps;


    @BindView(R.id.t_infos)
    TextView tInfos;

    String jzdjFromTorr = "";
    String jzccFromTorr = "";
    String dbFromTorr = "";

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

        ButterKnife.bind(this, view);

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
        updateSl();
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
            xhHead.setText(res);
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
            //Gson gson = new Gson();
            //Log.d("mes", gson.toJson(data));

            zt.getDetailList().clear();

            //如果有之前提交的数据，带出
            if (data != null && data.getDatas() != null && data.getDatas().getBagDatas() != null) {
                List<Map<String, String>> mapList = data.getDatas().getBagDatas();
                if (mapList.size() > 0) {
                    for (Map<String, String> map : mapList) {
                        ZtProduct p = new ZtProduct();

                        p.setTh(thHead.getText().toString());
                        p.setXh(map.get("caseCode"));
                        p.setDb(map.get("zh_db"));

                        if (map.get("casedjmc") != null && !map.get("casedjmc").equals("")) {
                            p.setDj(map.get("casedjmc"));
                        }
                        if (map.get("casedj") != null && !map.get("casedj").equals("")) {
                            p.setDjCode(map.get("casedj"));
                        }

                        if (map.get("caseps") != null && !map.get("caseps").equals("")) {
                            p.setSl(data.getVal("caseps"));
                        }

                        zt.getDetailList().add(p);

                        jzdjFromTorr = map.get("jzdj");
                        jzccFromTorr = map.get("jzcc") == null || map.get("jzcc").equals("") ? "0" : map.get("jzcc");
                        dbFromTorr = map.get("zh_db");

                        tInfos.setText(String.format(activity.getResources().getString(R.string.case_infos), jzdjFromTorr, jzccFromTorr, dbFromTorr));

                    }
                    updateSl();
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
                if (!levelDiff(data.getVal("casedj"))) {

                    String jzdj = data.getVal("jzdj");
                    String jzcc = data.getVal("jzcc") == null || data.getVal("jzcc").equals("") ? "0" : data.getVal("jzcc");
                    String db = data.getVal("zh_db");

                    if (zt.getDetailList().size() == 0 || (jzdjFromTorr.equals(jzdj) && Double.parseDouble(jzccFromTorr) == Double.parseDouble(jzcc))) {

                        jzdjFromTorr = jzdj;
                        jzccFromTorr = jzcc;
                        dbFromTorr = db;

                        tInfos.setText(String.format(activity.getResources().getString(R.string.t_infos), jzdjFromTorr, jzccFromTorr, dbFromTorr));

                        xhHead.setText(currentCode);
                        ZtProduct p = new ZtProduct();

                        p.setXh(currentCode);
                        p.setTh(thHead.getText().toString());

                        if (data != null && data.getVal("casedjmc") != null && !data.getVal("casedjmc").equals("")) {
                            p.setDj(data.getVal("casedjmc"));
                        }

                        if (data != null && data.getVal("casedj") != null && !data.getVal("casedj").equals("")) {
                            p.setDjCode(data.getVal("casedj"));
                        }

                        if (data != null && data.getVal("caseps") != null && !data.getVal("caseps").equals("")) {
                            p.setSl(data.getVal("caseps"));
                        }

                        if (data != null && data.getVal("zh_db") != null && !data.getVal("zh_db").equals("")) {
                            p.setDb(data.getVal("zh_db"));
                            activity.setStatus(MyApplication.getResString(R.string.db_type) + data.getVal("zh_db"), true);
                        }

                        zt.getDetailList().add(p);
                        updateSl();
                        dataAdapter.notifyDataSetChanged();
                    } else {
                        MyApplication.toast(String.format(activity.getResources().getString(R.string.t_info_diff), jzdj, jzcc), false);
                    }
                } else {
                    MyApplication.toast(R.string.zt_dj_diff, false);
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
        hs.setText("0");
        ps.setText("0");
        tInfos.setText("");
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

    /**
     * 比较箱子等级
     * 即将装托的箱子列表中如果有A级,则所有箱子的级别必须一致
     *
     * @param levelCode 扫描待加入的箱子等级
     * @return 等级是否不一致
     */
    public boolean levelDiff(String levelCode) {
        if (existALevel()) {
            if (levelCode == null || !levelCode.equals(zt.getDetailList().get(0).getDjCode())) {
                return true;
            }
        } else {
            if (levelCode != null && !levelCode.equals("")) {
                if (levelCode.startsWith("A")) {
                    if (zt.getDetailList().size() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean existALevel() {
        for (ZtProduct product : zt.getDetailList()) {
            if (product.getDjCode() != null && product.getDjCode().startsWith("A")) {
                return true;
            }
        }
        return false;
    }

    public boolean dbDiff(String db) {
        if (db != null && !db.equals("")) {
            for (ZtProduct product : zt.getDetailList()) {
                if (product.getDb() != null && !product.getDb().equals("") && !product.getDb().equals(db)) {
                    //Log.d("mes", "new db:" + db + "     exist db:" + product.getDb());
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 更新数量
     */
    public void updateSl() {
        String[] sl = calCurrentHsPs();
        hs.setText(sl[0]);
        ps.setText(sl[1]);
    }

    public String[] calCurrentHsPs() {

        int psInt = 0;

        for (ZtProduct t : zt.getDetailList()) {
            try {
                psInt = psInt + Integer.parseInt(t.getSl());
            } catch (Exception e) {
                //do not cal
            }
        }

        return new String[]{zt.getDetailList().size() + "", psInt + ""};
    }
}
