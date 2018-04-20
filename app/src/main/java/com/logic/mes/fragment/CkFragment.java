package com.logic.mes.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.logic.mes.EditTextUtil;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.ProcessUtil;
import com.logic.mes.R;
import com.logic.mes.activity.MainActivity;
import com.logic.mes.adapter.CkListAdapter;
import com.logic.mes.dialog.MaterialDialog;
import com.logic.mes.entity.process.CkDetail;
import com.logic.mes.entity.process.CkProduct;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CkFragment extends BaseTagFragment implements CkListAdapter.ButtonCallbacks, IScanReceiver, ProcessUtil.SubmitResultReceiver, ServerObserver.ServerDataReceiver {

    public CkFragment() {
        this.tagNameId = R.string.ck_tab_name;
    }

    @BindView(R.id.ck_v_tm_head)
    TextView tmHead;
    @BindView(R.id.ck_product_list)
    RecyclerView listView;
    @BindView(R.id.ck_v_jzrq)
    Button jzrq;
    @BindView(R.id.ck_v_hj)
    TextView hj;
    @BindView(R.id.ck_v_bill)
    EditText bill;

    @BindView(R.id.ck_b_submit)
    Button submit;
    @BindView(R.id.ck_b_clear)
    Button clear;

    @BindView(R.id.ck_base_infos)
    TextView baseInfos;

    String jzdjGlobal = "";
    String jzccGlobal = "";
    String dbGlobal = "";

    MaterialDialog noticeDialog;
    View noteView;
    TextView noteMsgView;

    CkProduct product;
    CkListAdapter dataAdapter;
    MainActivity activity;
    IScanReceiver receiver;
    Calendar c;

    ProcessUtil.SubmitResultReceiver submitResultReceiver;
    ServerObserver serverObserver;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.ck, container, false);
        ButterKnife.bind(this, view);
        activity = (MainActivity) getActivity();
        receiver = this;
        submitResultReceiver = this;
        serverObserver = new ServerObserver(this, "ck", activity);

        if (c == null) {
            c = Calendar.getInstance();
        }

        jzrq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        jzrq.setText(year + "-" + (month + 1) + "-" + day);
                        product.setJzrq(jzrq.getText().toString());
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product.getDetailList().size() == 0) {
                    MyApplication.toast(R.string.tm_scan_first, false);
                } else if (jzrq.getText().equals("")) {
                    MyApplication.toast(R.string.jzrq_need, false);
                } else {
                    product.setCode("ck");
                    product.setBill(bill.getText().toString());
                    new ProcessUtil(activity).submit(submitResultReceiver, product, userInfo.getUser());
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        if (product == null) {
            product = new CkProduct();
        }

        dataAdapter = new CkListAdapter(getActivity(), product.getDetailList(), this);
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);
        helper.attachToRecyclerView(listView);
        helper.setCloseOnAction(false);

        jzrq.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        product.setJzrq(jzrq.getText().toString());

        MyApplication.getScanUtil().setReceiver(receiver, 0);

        EditTextUtil.setNoKeyboard(bill);

        noticeDialog = new MaterialDialog(activity);

        noteView = View.inflate(activity, R.layout.dialog_msg, null);
        noteMsgView = (TextView) noteView.findViewById(R.id.dialog_msg_content);
        noteMsgView.setSingleLine(false);
        noticeDialog.setTitle(R.string.notice);
        noticeDialog.setContentView(noteView);

        noticeDialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
                noticeDialog.dismiss(view);
            }
        });

        noticeDialog.setNegativeButton(R.string.no, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeDialog.dismiss(view);
            }
        });

        return view;
    }

    @Override
    public void removePosition(int position) {
        dataAdapter.removePosition(position);
        if (product.getDetailList().size() == 0) {
            baseInfos.setText("");
        }
        calSum();
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        tmHead.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBagData(res))
                .subscribe(serverObserver);
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error, false);
    }

    @Override
    public void submitOk() {
        doAfterSumbit(tmHead.getText().toString(), true);
    }

    @Override
    public void submitError() {
        doAfterSumbit(tmHead.getText().toString(), false);
    }

    @Override
    public void serverData() {

        if (!checkExist(data.getVal("objCode"))) {
            String jzdj = data.getVal("jzdj");
            String jzcc = data.getVal("jzcc") == null || data.getVal("jzcc").equals("") ? "0" : data.getVal("jzcc");
            String db = data.getVal("zh_db");

            if (product.getDetailList().size() > 0) {
                if (!jzdj.equals(jzdjGlobal) || Double.parseDouble(jzccGlobal) != Double.parseDouble(jzcc) || !dbGlobal.equals(db)) {
                    noteMsgView.setText(String.format(activity.getResources().getString(R.string.base_info_diff), jzdj, jzcc, db));
                    noticeDialog.show();
                } else {
                    add();
                }
            } else {
                jzdjGlobal = jzdj;
                jzccGlobal = jzcc;
                dbGlobal = db;
                add();
                baseInfos.setText(String.format(activity.getResources().getString(R.string.case_infos), jzdjGlobal, jzccGlobal, dbGlobal));
            }
        } else {
            MyApplication.toast(R.string.duplicate_data, false);
        }
    }

    public void add() {
        CkDetail p = new CkDetail();
        p.setLb(data.getVal("objType"));
        p.setTm(data.getVal("objCode"));
        p.setSl(data.getVal("pieces"));
        product.getDetailList().add(p);

        calSum();

        dataAdapter.notifyDataSetChanged();
    }

    public void calSum() {
        int hjInt = 0;
        for (CkDetail rkDetail : product.getDetailList()) {
            String sl = rkDetail.getSl();
            if (sl == null || sl.equals("")) {
                sl = "0";
            }
            hjInt = new BigDecimal(hjInt).add(new BigDecimal(sl)).intValue();
        }
        hj.setText((hjInt + ""));
        product.setHj(hjInt + "");
    }

    @Override
    public void setData(ServerResult res) {
        data = res;
    }

    @Override
    public void clear() {
        tmHead.setText(MyApplication.getResString(R.string.wait_scan));
        jzrq.setText("");
        hj.setText("");
        bill.setText("");
        baseInfos.setText("");
        product.getDetailList().clear();
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void serverError(Throwable e) {

    }

    public boolean checkExist(String code) {

        for (CkDetail d : product.getDetailList()) {
            if (d.getTm().equals(code)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void preventSubmit() {

    }

    @Override
    public void ableSubmit() {

    }
}
