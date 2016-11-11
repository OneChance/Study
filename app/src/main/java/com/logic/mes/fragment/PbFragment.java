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
import com.logic.mes.entity.server.BrickInfo;
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

    public final int SCAN_CODE_STATION = 0;
    public final int SCAN_CODE_PRODUCT = 1;

    @InjectView(R.id.pb_b_mtype)
    Button scanMType;
    @InjectView(R.id.pb_scan_product)
    Button scanProduct;
    @InjectView(R.id.pb_v_mtype)
    TextView mType;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pb, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this);
        submitResultReceiver = this;

        scanMType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getScanUtil().send(receiver, SCAN_CODE_STATION);
            }
        });

        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.mtype_scan_first);
                } else {
                    MyApplication.getScanUtil().send(receiver, SCAN_CODE_PRODUCT);
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
                mType.setText(MyApplication.getResString(R.string.wait_scan));
                pb.setJx("");
                pb.getDetailList().clear();
                dataAdapter.notifyDataSetChanged();
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
    public void receive(String res, int scanCode) {
        if (scanCode == SCAN_CODE_STATION) {
            pb.setJx(res);
            mType.setText(res);
        } else if (scanCode == SCAN_CODE_PRODUCT) {
            //取产品信息
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res))
                    .subscribe(serverObserver);
        }
    }

    @Override
    public void getData(ServerResult res) {

        List<BrickInfo> brickInfos = res.getDatas();
        if (brickInfos.size() > 0) {

            BrickInfo brickInfo = brickInfos.get(0);


            PbDetail p = new PbDetail();
            p.setBrickId(brickInfo.getBrickId().toString());
            p.setLength(brickInfo.getYxbc().toString());
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
            MyApplication.toast("无数据!");
        }
    }

    @Override
    public void error() {

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
        pb.getDetailList().clear();
        dataAdapter.notifyDataSetChanged();
    }
}
