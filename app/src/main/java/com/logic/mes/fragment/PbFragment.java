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
import com.logic.mes.entity.base.TableSet;
import com.logic.mes.entity.base.TableType;
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
    List<TableType> tableType;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_MTYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

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
                        //添加线网数据
                        for (TableType tt : tableType) {
                            if (tt.getTypeCode().equals(pb.getJx())) {
                                pb.setXwcd(tt.getLineLength());
                                break;
                            }
                        }

                        new ProcessUtil(activity).submit(submitResultReceiver, pb, userInfo.getUser());
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

        dataAdapter = new PbListAdapter(getActivity(), pb.getDetailList(), this);
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);
        helper.attachToRecyclerView(listView);
        helper.setCloseOnAction(false);

        tableType = userInfo.getTableType();
        tableSet = userInfo.getTableSet();

        return view;
    }

    @Override
    public void removePosition(int position) {
        dataAdapter.removePosition(position);
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        if (scanCode == SCAN_CODE_MTYPE) {
            pb.setJx(res);
            mType.setText(res);
            MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_PRODUCT);
        } else if (scanCode == SCAN_CODE_PRODUCT) {
            //取产品信息
            brick.setText(res);
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res))
                    .subscribe(serverObserver);
        }
    }

    @Override
    public void serverData() {

        if (checkExist(data.getVal("ej_BrickID"))) {
            MyApplication.toast(R.string.duplicate_data);
        } else if (differentLevel(data.getVal("ej_jzdj"))) {
            MyApplication.toast(R.string.different_level);
        } else {
            PbDetail p = new PbDetail();
            p.setBrickId(data.getVal("ej_BrickID"));
            p.setLength(data.getVal("pbj_yxbc"));
            p.setLevel(data.getVal("ej_jzdj"));
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
        }
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void clear() {
        mType.setText(R.string.wait_scan);
        brick.setText(R.string.wait_scan);
        pb.getDetailList().clear();
        dataAdapter.notifyDataSetChanged();
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_MTYPE);
    }

    @Override
    public void serverError() {

    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error);
    }

    public String getStations() {
        String stations = "";
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

    public boolean differentLevel(String level) {

        if (pb.getDetailList().size() > 0 && !pb.getDetailList().get(0).getLevel().equals(level)) {
            return true;
        }

        return false;
    }
}
