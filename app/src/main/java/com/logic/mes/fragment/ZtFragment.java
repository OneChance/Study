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
import com.logic.mes.adapter.ZtListAdapter;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.process.ZtHead;
import com.logic.mes.entity.process.ZtProduct;
import com.logic.mes.entity.server.ProcessUtil;

import java.util.List;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ZtFragment extends BaseTagFragment implements ZtListAdapter.ButtonCallbacks, IScanReceiver, ProcessUtil.SubmitResultReceiver {

    public ZtFragment() {
        this.tagNameId = R.string.zt_tab_name;
    }

    public final int SCAN_CODE_TH = 0;
    public final int SCAN_CODE_XZ = 1;


    @InjectView(R.id.zt_th_v)
    TextView thHead;
    @InjectView(R.id.zt_xh_v)
    TextView xhHead;

    @InjectView(R.id.zt_b_scan_th)
    Button scanTh;
    @InjectView(R.id.zt_b_scan_xz)
    Button scanXh;

    @InjectView(R.id.zt_product_list)
    RecyclerView listView;
    @InjectView(R.id.zt_save)
    Button save;

    @InjectView(R.id.zt_b_submit)
    Button submit;
    @InjectView(R.id.zt_b_clear)
    Button clear;

    ZtHead zt;
    ZtListAdapter dataAdapter;
    FragmentActivity activity;
    IScanReceiver receiver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_TH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.zt, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;
        submitResultReceiver = this;

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
                    MyApplication.toast(R.string.th_scan_first);
                } else {
                    xhHead.setText(R.string.wait_scan);
                    MyApplication.getScanUtil().setReceiver(receiver, SCAN_CODE_XZ);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zt.getDetailList().size() > 0) {
                    DBHelper.getInstance(activity).delete(ZtHead.class);
                    DBHelper.getInstance(activity).save(zt);
                    MyApplication.toast(R.string.product_save_success);
                } else {
                    MyApplication.toast(R.string.xz_scan_need);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.th_scan_first);
                } else if (zt.getDetailList().size() == 0) {
                    MyApplication.toast(R.string.xz_scan_need);
                } else {
                    zt.setCode("zt");
                    new ProcessUtil(activity).submit(submitResultReceiver, zt);
                }
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

        List<ZtHead> plist = DBHelper.getInstance(activity).query(ZtHead.class);
        if (plist.size() > 0) {
            zt = plist.get(0);
            if (zt.getDetailList() != null && zt.getDetailList().size() > 0) {
                thHead.setText(zt.getDetailList().get(0).getTh());
            }
        }

        dataAdapter = new ZtListAdapter(getActivity(), zt.getDetailList(), this);
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);
        helper.attachToRecyclerView(listView);
        helper.setCloseOnAction(false);

        return view;
    }

    @Override
    public void removePosition(int position) {
        ZtProduct p = zt.getDetailList().get(position);
        if (p.getId() != 0) {
            DBHelper.getInstance(activity).delete(p);
        }
        dataAdapter.removePosition(position);
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        if (scanCode == SCAN_CODE_TH) {
            thHead.setText(res);
        } else if (scanCode == SCAN_CODE_XZ) {
            xhHead.setText(res);
            ZtProduct p = new ZtProduct();
            p.setTh(thHead.getText().toString());
            p.setXh(res);
            zt.getDetailList().add(p);
            dataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error);
    }

    public void clear() {
        thHead.setText(R.string.wait_scan);
        xhHead.setText(R.string.wait_scan);
        zt.getDetailList().clear();
        dataAdapter.notifyDataSetChanged();
        DBHelper.getInstance(activity).delete(ZtHead.class);
    }

    @Override
    public void submitOk() {
        clear();
    }
}
