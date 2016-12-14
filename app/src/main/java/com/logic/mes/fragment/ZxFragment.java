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
import com.logic.mes.adapter.ZxListAdapter;
import com.logic.mes.entity.process.ZxHead;
import com.logic.mes.entity.process.ZxProduct;
import com.logic.mes.entity.server.ProcessItem;
import com.logic.mes.ProcessUtil;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ZxFragment extends BaseTagFragment implements ZxListAdapter.ButtonCallbacks, IScanReceiver, ProcessUtil.SubmitResultReceiver, ServerObserver.ServerDataReceiver {

    public ZxFragment() {
        this.tagNameId = R.string.zx_tab_name;
    }

    public final int SCAN_CODE_XZ = 0;
    public final int SCAN_CODE_HZ = 1;

    int currentReceiverCode = SCAN_CODE_XZ;
    boolean waitReceive = false;
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
    FragmentActivity activity;
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

        activity = getActivity();
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
                if (xhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.xz_scan_first, false);
                } else if (zx.getDetailList().size() == 0) {
                    MyApplication.toast(R.string.hz_scan_need, false);
                } else {
                    zx.setCode("zx");
                    new ProcessUtil(activity).submit(submitResultReceiver, zx, userInfo.getUser());
                }
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

    @Override
    public void removePosition(int position) {
        dataAdapter.removePosition(position);
    }

    @Override
    public void scanReceive(String res, int scanCode) {

        ProcessItem item = new ProcessItem();

        if (scanCode == SCAN_CODE_XZ) {
            if (!waitReceive) {
                currentCode = res;
                item.setItemKey("CaseCode");
                item.setItemValue(currentCode);
                NetUtil.SetObserverCommonAction(NetUtil.getServices(false).checkData(item))
                        .subscribe(serverObserver);
                currentReceiverCode = SCAN_CODE_XZ;
                waitReceive = true;
            }
        } else if (scanCode == SCAN_CODE_HZ) {
            if (!waitReceive) {
                currentCode = res;
                item.setItemKey("BoxCode");
                item.setItemValue(currentCode);
                NetUtil.SetObserverCommonAction(NetUtil.getServices(false).checkData(item))
                        .subscribe(serverObserver);
                currentReceiverCode = SCAN_CODE_HZ;
                waitReceive = true;
            }
        }
    }

    @Override
    public void scanError() {

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

        if (!data.getCode().equals("1")) {
            fillData();
        }
        waitReceive = false;
    }

    public void fillData() {
        if (currentReceiverCode == SCAN_CODE_XZ) {
            xhHead.setText(currentCode);
            MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_HZ);
        } else if (currentReceiverCode == SCAN_CODE_HZ) {
            hhHead.setText(currentCode);
            ZxProduct p = new ZxProduct();
            p.setXh(xhHead.getText().toString());
            p.setHh(currentCode);
            if (zx.getDetailList().size() < 4) {
                zx.getDetailList().add(p);
            } else {
                MyApplication.toast(R.string.zx_size_full_4, false);
            }
            dataAdapter.notifyDataSetChanged();
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
        fillData();
        waitReceive = false;
    }
}
