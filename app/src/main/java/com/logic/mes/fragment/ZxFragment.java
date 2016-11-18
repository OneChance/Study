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
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.process.ZxHead;
import com.logic.mes.entity.process.ZxProduct;
import com.logic.mes.entity.server.ProcessUtil;

import java.util.List;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ZxFragment extends BaseTagFragment implements ZxListAdapter.ButtonCallbacks, IScanReceiver, ProcessUtil.SubmitResultReceiver {

    public ZxFragment() {
        this.tagNameId = R.string.zx_tab_name;
    }

    public final int SCAN_CODE_XZ = 0;
    public final int SCAN_CODE_HZ = 1;

    @InjectView(R.id.zx_xh_v)
    TextView xhHead;
    @InjectView(R.id.zx_hh_v)
    TextView hhHead;

    @InjectView(R.id.zx_product_list)
    RecyclerView listView;
    @InjectView(R.id.zx_save)
    Button save;

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
    ProcessUtil.SubmitResultReceiver submitResultReceiver;

    @Override
    public void setReceiver() {
        receiver = this;
        submitResultReceiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_XZ);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.zx, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;

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
                    MyApplication.toast(R.string.xz_scan_first);
                } else {
                    hhHead.setText(R.string.wait_scan);
                    MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_HZ);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zx.getDetailList().size() > 0) {
                    DBHelper.getInstance(activity).delete(ZxHead.class);
                    DBHelper.getInstance(activity).save(zx);
                    MyApplication.toast(R.string.product_save_success);
                } else {
                    MyApplication.toast(R.string.hz_scan_need);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (xhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.xz_scan_first);
                } else if (zx.getDetailList().size() == 0) {
                    MyApplication.toast(R.string.hz_scan_need);
                } else {
                    zx.setCode("zx");
                    new ProcessUtil(activity).submit(submitResultReceiver, zx);
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

        List<ZxHead> plist = DBHelper.getInstance(activity).query(ZxHead.class);
        if (plist.size() > 0) {
            zx = plist.get(0);
            if (zx.getDetailList() != null && zx.getDetailList().size() > 0) {
                xhHead.setText(zx.getDetailList().get(0).getXh());
            }
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
        ZxProduct p = zx.getDetailList().get(position);
        if (p.getId() != 0) {
            DBHelper.getInstance(activity).delete(p);
        }
        dataAdapter.removePosition(position);
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        if (scanCode == SCAN_CODE_XZ) {
            xhHead.setText(res);
        } else if (scanCode == SCAN_CODE_HZ) {
            hhHead.setText(res);
            ZxProduct p = new ZxProduct();
            p.setXh(xhHead.getText().toString());
            p.setHh(res);
            if (zx.getDetailList().size() < 4) {
                zx.getDetailList().add(p);
            } else {
                MyApplication.toast(R.string.zx_size_full_4);
            }

            dataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void scanError() {

    }

    @Override
    public void submitOk() {
        clear();
    }

    public void clear() {
        xhHead.setText(R.string.wait_scan);
        hhHead.setText(R.string.wait_scan);
        zx.getDetailList().clear();
        dataAdapter.notifyDataSetChanged();
        DBHelper.getInstance(activity).delete(ZxHead.class);
    }
}