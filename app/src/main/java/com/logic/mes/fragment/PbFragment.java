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
import com.logic.mes.adapter.PbListAdapter;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.base.TableSet;
import com.logic.mes.entity.process.PbDetail;
import com.logic.mes.entity.process.PbProduct;
import com.logic.mes.entity.server.ProcessUtil;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.util.ArrayList;
import java.util.List;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.R.id.list;

public class PbFragment extends BaseTagFragment implements PbListAdapter.ButtonCallbacks, IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public PbFragment() {
        this.tagNameId = R.string.pb_tab_name;
    }

    public final int SCAN_CODE_MTYPE = 0;
    public final int SCAN_CODE_PRODUCT = 1;

    @InjectView(R.id.pb_b_scan_mtype)
    Button scanMType;
    @InjectView(R.id.pb_b_scan_brick)
    Button scanBrick;
    @InjectView(R.id.pb_v_mtype)
    TextView mType;
    @InjectView(R.id.pb_v_brick)
    TextView brick;
    @InjectView(R.id.pb_product_list)
    RecyclerView listView;
    @InjectView(R.id.pb_save)
    Button save;

    @InjectView(R.id.pb_b_submit)
    Button submit;
    @InjectView(R.id.pb_b_clear)
    Button clear;

    PbProduct pb;
    PbListAdapter dataAdapter;
    FragmentActivity activity;
    IScanReceiver receiver;
    ServerObserver serverObserver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    List<TableSet> tableSet;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_MTYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pb, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this, "pb", activity);
        submitResultReceiver = this;

        scanMType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType.setText(R.string.wait_scan);
                MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_MTYPE);
            }
        });

        scanBrick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.mtype_scan_first);
                } else {
                    brick.setText(R.string.wait_scan);
                    MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_PRODUCT);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.getInstance(activity).delete(PbProduct.class);
                DBHelper.getInstance(activity).save(list);
                MyApplication.toast(R.string.product_save_success);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.mtype_scan_first);
                } else {
                    String stations = getStations();
                    if (!stations.equals("")) {
                        //提交数据
                        pb.setCode("pb");
                        new ProcessUtil(activity).submit(submitResultReceiver, pb);
                    } else {
                        MyApplication.toast(R.string.mtype_not_match);
                    }
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        if (pb == null) {
            pb = new PbProduct();
            pb.setDetailList(new ArrayList<PbDetail>());
        }

        List<PbProduct> plist = DBHelper.getInstance(activity).query(PbProduct.class);
        if (plist.size() > 0) {
            pb = plist.get(0);
            mType.setText(pb.getJx());
        } else {
            pb.setJx("");
            pb.getDetailList().clear();
        }

        dataAdapter = new PbListAdapter(getActivity(), pb.getDetailList(), this);
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);
        helper.attachToRecyclerView(listView);
        helper.setCloseOnAction(false);

        return view;
    }

    @Override
    public void removePosition(int position) {
        PbDetail p = pb.getDetailList().get(position);
        if (p.getId() != 0) {
            DBHelper.getInstance(activity).delete(p);
        }
        dataAdapter.removePosition(position);
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        if (scanCode == SCAN_CODE_MTYPE) {
            pb.setJx(res);
            mType.setText(res);
        } else if (scanCode == SCAN_CODE_PRODUCT) {
            //取产品信息
            brick.setText(res);
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res))
                    .subscribe(serverObserver);
        }
    }

    @Override
    public void serverData(ServerResult res) {

        if (!checkExist(res.getVal("ej_BrickID"))) {
            PbDetail p = new PbDetail();
            p.setBrickId(res.getVal("ej_BrickID"));
            p.setLength(res.getVal("pbj_yxbc"));
            p.setStation("");
            pb.getDetailList().add(p);

            String stations = getStations();
            if (!stations.equals("")) {
                String[] stationArray = stations.split(",");
                for (int i = 0; i < pb.getDetailList().size(); i++) {
                    pb.getDetailList().get(i).setStation(stationArray[i]);
                }
            }

            dataAdapter.notifyItemChanged(1);
            dataAdapter.notifyDataSetChanged();

        } else {
            MyApplication.toast(R.string.duplicate_data);
        }
    }

    @Override
    public void clear() {
        mType.setText(R.string.wait_scan);
        pb.getDetailList().clear();
        dataAdapter.notifyDataSetChanged();
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_MTYPE);
        DBHelper.getInstance(activity).delete(PbProduct.class);
    }

    @Override
    public void serverError() {

    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error);
    }

    public void getTableSet() {
        if (tableSet == null) {
            tableSet = DBHelper.getInstance(activity).query(TableSet.class);
        }
    }

    public String getStations() {
        String stations = "";
        getTableSet();
        for (TableSet ts : tableSet) {
            if (ts.getTypeCode().equals(mType.getText().toString())) {
                if (ts.getDataSet().split(",").length == pb.getDetailList().size()) {
                    stations = ts.getDataSet();
                }
            }
        }
        return stations;
    }

    @Override
    public void submitOk() {
        clear();
    }

    public boolean checkExist(String code) {

        for (PbDetail d : pb.getDetailList()) {
            if (d.getBrickId().equals(code)) {
                return true;
            }
        }

        return false;
    }
}
