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
import com.logic.mes.adapter.PandianListAdapter;
import com.logic.mes.entity.base.TableSet;
import com.logic.mes.entity.base.TableType;
import com.logic.mes.entity.process.MType;
import com.logic.mes.entity.process.PandianDetail;
import com.logic.mes.entity.process.PandianProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.util.ArrayList;
import java.util.List;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PandianFragment extends BaseTagFragment implements PandianListAdapter.ButtonCallbacks, IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public PandianFragment() {
        this.tagNameId = R.string.pandian_tab_name;
    }

    @BindView(R.id.pandian_workshop)
    TextView workshop;
    @BindView(R.id.pandian_product_list)
    RecyclerView listView;

    @BindView(R.id.pandian_submit)
    Button submit;
    @BindView(R.id.pandian_clear)
    Button clear;

    PandianProduct pandian;
    PandianListAdapter dataAdapter;
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
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.pandian, container, false);

        ButterKnife.bind(this, view);

        activity = (MainActivity) getActivity();
        receiver = this;
        serverObserver = new ServerObserver(this, "pb", activity);
        submitResultReceiver = this;


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });

        if (pandian == null) {
            pandian = new PandianProduct();
            pandian.setDetailList(new ArrayList<PandianDetail>());
        }

        dataAdapter = new PandianListAdapter(getActivity(), pandian.getDetailList(), this);
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
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res, "pandian"))
                .subscribe(serverObserver);
    }

    public void addRow(String brickId, int sequenceNum, double weight) {

        if (checkExist(brickId)) {
            MyApplication.toast(R.string.duplicate_data, false);
        } else {
            PandianDetail p = new PandianDetail();
            p.setBrickId(brickId);
            p.setSequenceNum(sequenceNum);
            p.setWeight(weight);
            pandian.getDetailList().add(p);
            dataAdapter.notifyItemChanged(1);
            dataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void serverData() {
        String brickId = data.getVal("ej_BrickID");
        double weight;
        try {
            weight = Double.parseDouble(data.getVal("weight"));
            addRow(brickId,pandian.getDetailList().size()+1,weight);
        } catch (Exception e) {
            MyApplication.toast(R.string.pandian_weight_error,false);
        }
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
        successClear();
    }

    public void successClear() {
        workshop.setText(R.string.wait_scan);
        pandian.getDetailList().clear();
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void serverError(Throwable e) {
        //addRow(brick.getText().toString(), "", "", 0.0, "", "", "");
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

    @Override
    public void submitOk() {
        doAfterSumbit("", true);
    }

    @Override
    public void submitError() {
        doAfterSumbit("", false);
    }

    /**
     * 判断列表中是否存在此brick
     *
     * @param code brickCode
     * @return 判断结果
     */
    public boolean checkExist(String code) {
        for (PandianDetail d : pandian.getDetailList()) {
            if (d.getBrickId().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
