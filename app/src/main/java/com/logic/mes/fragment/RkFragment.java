package com.logic.mes.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.adapter.RkListAdapter;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.process.RkDetail;
import com.logic.mes.entity.process.RkProduct;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class RkFragment extends BaseTagFragment implements RkListAdapter.ButtonCallbacks, IScanReceiver {

    public RkFragment() {
        this.tagNameId = R.string.rk_tab_name;
    }

    @InjectView(R.id.rk_b_scan_tm)
    Button scanTm;
    @InjectView(R.id.rk_v_tm_head)
    TextView tmHead;
    @InjectView(R.id.rk_product_list)
    RecyclerView listView;
    @InjectView(R.id.rk_save)
    Button save;
    @InjectView(R.id.rk_v_jzrq)
    Button jzrq;
    @InjectView(R.id.rk_v_hj)
    TextView hj;

    @InjectView(R.id.rk_b_submit)
    Button submit;
    @InjectView(R.id.rk_b_clear)
    Button clear;

    RkProduct product;
    List<RkDetail> list;
    RkListAdapter dataAdapter;
    FragmentActivity activity;
    IScanReceiver receiver;
    Calendar c;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.rk, container, false);
        ButterKnife.inject(this, view);
        activity = getActivity();
        receiver = this;

        if (c == null) {
            c = Calendar.getInstance();
        }

        jzrq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        jzrq.setText(year+"-"+(month+1)+"-"+day);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        scanTm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getScanUtil().send(receiver, 0);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() > 0) {
                    DBHelper.getInstance(activity).delete(RkProduct.class);
                    product.setJzrq(jzrq.getText().toString());
                    product.setHj(hj.getText().toString());
                    product.setdList(list);
                    DBHelper.getInstance(activity).save(product);
                    MyApplication.toast(R.string.product_save_success);
                } else {
                    MyApplication.toast(R.string.tm_scan_first);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() == 0) {
                    MyApplication.toast(R.string.tm_scan_first);
                } else {

                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmHead.setText(MyApplication.getResString(R.string.wait_scan));
                jzrq.setText("");
                hj.setText("");
                list.clear();
                dataAdapter.notifyDataSetChanged();
            }
        });

        if (product == null) {
            product = new RkProduct();
        }

        if (list == null) {
            list = new ArrayList<>();
        }

        List<RkProduct> plist = DBHelper.getInstance(activity).query(RkProduct.class);
        if (plist.size() > 0) {
            jzrq.setText(plist.get(0).getJzrq());
            hj.setText(plist.get(0).getHj());
            list = plist.get(0).getdList();
        }

        dataAdapter = new RkListAdapter(getActivity(), list, this);
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);
        helper.attachToRecyclerView(listView);
        helper.setCloseOnAction(false);

        return view;
    }

    @Override
    public void removePosition(int position) {
        RkDetail p = list.get(position);
        if (p.getId() != 0) {
            DBHelper.getInstance(activity).delete(p);
        }
        dataAdapter.removePosition(position);
    }

    @Override
    public void receive(String res, int scanCode) {
        RkDetail p = new RkDetail();
        p.setLb("盒");
        p.setTm("241-234234-25435-123");
        p.setSl("10");
        list.add(p);
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void error() {

    }
}
