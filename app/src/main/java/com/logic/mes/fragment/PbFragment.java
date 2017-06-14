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
import com.logic.mes.adapter.PbListAdapter;
import com.logic.mes.entity.base.TableSet;
import com.logic.mes.entity.base.TableType;
import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.entity.process.MType;
import com.logic.mes.entity.process.PbDetail;
import com.logic.mes.entity.process.PbProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;
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
    Spinner mType;
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
    MainActivity activity;
    IScanReceiver receiver;
    ServerObserver serverObserver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    List<TableSet> tableSet;
    List<TableType> tableType;
    List<MType> mTypes = new ArrayList<>();

    boolean visible = false;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_MTYPE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.pb, container, false);

        ButterKnife.inject(this, view);

        activity = (MainActivity) getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this, "pb", activity);
        submitResultReceiver = this;

        scanMType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_MTYPE);
            }
        });

        scanBrick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pb.getJx().equals("")) {
                    MyApplication.toast(R.string.wait_choose_mtype, false);
                } else {
                    brick.setText(R.string.wait_scan);
                    MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_PRODUCT);
                }
            }
        });

        //初始化机型下拉列表
        final UserInfo userInfo = ((MainActivity) activity).getUserInfo();
        List<TableType> types = userInfo.getTableType();
        mTypes.clear();
        MType toSelect = new MType("", MyApplication.getResString(R.string.wait_choose));
        mTypes.add(toSelect);
        for (TableType type : types) {
            mTypes.add(new MType(type.getTypeCode(), type.getTypeName()));
        }
        ArrayAdapter<MType> adapter = new ArrayAdapter<MType>(activity, android.R.layout.simple_spinner_item, mTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mType.setAdapter(adapter);

        mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pb.setJx(mTypes.get(i).getCode());
                if (!mTypes.get(i).getCode().equals("") && visible) {
                    MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_PRODUCT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pb.getJx().equals("")) {
                    MyApplication.toast(R.string.wait_choose_mtype, false);
                } else {
                    double groupLength = getGroupLength(pb.getJx());
                    double groupMinLength = getGroupMinLength(pb.getJx());
                    double listLength = 0;
                    for (PbDetail pbDetail : pb.getDetailList()) {
                        String length = pbDetail.getLength();
                        if (length == null || length.equals("")) {
                            length = "0";
                        }
                        listLength = new BigDecimal(listLength).add(new BigDecimal(length)).doubleValue();
                    }

                    //校验长度信息
                    if (listLength > groupLength || listLength < groupMinLength) {
                        MyApplication.toast(String.format(MyApplication.getResString(R.string.length_error), listLength, groupMinLength, groupLength), false);
                    } else {
                        //校验工位信息
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
                            MyApplication.toast(R.string.mtype_not_match, false);
                        }
                    }
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
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
        //取产品信息
        if (scanCode == SCAN_CODE_MTYPE) {
            int selectIndex = getSelectIndex(res);
            if (selectIndex > 0) {
                mType.setSelection(selectIndex);
                MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_PRODUCT);
            } else {
                mType.setSelection(0);
            }
        } else if (scanCode == SCAN_CODE_PRODUCT) {
            //取产品信息
            brick.setText(res);
            NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res, "pb"))
                    .subscribe(serverObserver);
        }
    }

    public void addRow(String brickId, String level, String length) {
        if (checkExist(brickId)) {
            MyApplication.toast(R.string.duplicate_data, false);
        } else if (dbDiff(data.getVal("db"))) {
            MyApplication.toast(R.string.db_diff, false);
        } else if (differentLevel(level)) {
            MyApplication.toast(R.string.different_level, false);
        } else {
            PbDetail p = new PbDetail();
            p.setBrickId(brickId);
            p.setLength(length);
            p.setLevel(level);
            p.setStation("");

            if (data != null && data.getVal("db") != null && !data.getVal("db").equals("")) {
                p.setDb(data.getVal("db"));
                activity.setStatus(MyApplication.getResString(R.string.db_type) + data.getVal("db"), true);
            }

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
    public void serverData() {
        addRow(data.getVal("ej_BrickID"), data.getVal("ej_jzdj"), data.getVal("pbj_yxbc"));
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void clear() {
        successClear();
    }

    public void clearAll() {
        mType.setSelection(0);
        successClear();
    }

    public void successClear() {
        brick.setText(R.string.wait_scan);
        pb.getDetailList().clear();
        dataAdapter.notifyDataSetChanged();
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_MTYPE);
    }

    @Override
    public void serverError(Throwable e) {
        addRow(brick.getText().toString(), "", "");
    }

    @Override
    public void preventSubmit() {

    }

    @Override
    public void ableSubmit() {

    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    /**
     * @return 返回一组工位信息
     */
    public String getStations() {
        String stations = "";
        for (TableSet ts : tableSet) {
            if (ts.getTypeCode().equals(pb.getJx())) {
                if (ts.getDataSet().split(",").length == pb.getDetailList().size()) {
                    stations = ts.getDataSet();
                }
            }
        }
        return stations;
    }

    @Override
    public void submitOk() {
        doAfterSumbit(pb.getDetailList().get(0).getBrickId(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(pb.getDetailList().get(0).getBrickId(), false);
    }

    /**
     * 判断列表中是否存在此brick
     *
     * @param code brickCode
     * @return 判断结果
     */
    public boolean checkExist(String code) {

        for (PbDetail d : pb.getDetailList()) {
            if (d.getBrickId().equals(code)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否同等级
     *
     * @param level 等级
     * @return 判断结果
     */
    public boolean differentLevel(String level) {
        return pb.getDetailList().size() > 0 && !pb.getDetailList().get(0).getLevel().equals(level);
    }

    /**
     * 获得机型在下拉列表中的位置
     *
     * @param mType 机型
     * @return 位置索引
     */
    public int getSelectIndex(String mType) {
        for (int i = 0; i < mTypes.size(); i++) {
            if (mTypes.get(i).getCode().equals(mType)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 获得组长度
     *
     * @param mType 机型
     * @return 组长度
     */
    public double getGroupLength(String mType) {
        for (int i = 0; i < tableType.size(); i++) {
            if (tableType.get(i).getTypeCode().equals(mType)) {
                try {
                    return Double.parseDouble(tableType.get(i).getGroupLength());
                } catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    /**
     * 获得最小组长度
     *
     * @param mType 机型
     * @return 最小组长度
     */
    public double getGroupMinLength(String mType) {
        for (int i = 0; i < tableType.size(); i++) {
            if (tableType.get(i).getTypeCode().equals(mType)) {
                try {
                    return Double.parseDouble(tableType.get(i).getGroupMinLength());
                } catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    public boolean dbDiff(String db) {
        if (db != null && !db.equals("")) {
            for (PbDetail product : pb.getDetailList()) {
                if (product.getDb() != null && !product.getDb().equals("") && !product.getDb().equals(db)) {
                    return true;
                }
            }
        }

        return false;
    }
}
