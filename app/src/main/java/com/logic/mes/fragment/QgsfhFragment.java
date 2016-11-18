package com.logic.mes.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.logic.mes.EditTextUtil;
import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.process.QgsfhProduct;
import com.logic.mes.entity.server.ProcessUtil;
import com.logic.mes.entity.server.ServerResult;
import com.logic.mes.net.NetUtil;
import com.logic.mes.observer.ServerObserver;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.logic.mes.R.layout.qgsfh;

public class QgsfhFragment extends BaseTagFragment implements IScanReceiver, ServerObserver.ServerDataReceiver, ProcessUtil.SubmitResultReceiver {

    public QgsfhFragment() {
        this.tagNameId = R.string.qgsfh_tab_name;
    }

    @InjectView(R.id.qgsfh_save)
    Button save;
    @InjectView(R.id.qgsfh_v_jzbh)
    TextView jzbh;
    @InjectView(R.id.qgsfh_b_submit)
    Button bSubmit;
    @InjectView(R.id.qgsfh_b_clear)
    Button bClear;
    @InjectView(R.id.qgsfh_v_yzd)
    EditText yzd;
    @InjectView(R.id.qgsfh_v_hbp)
    EditText hbp;
    @InjectView(R.id.qgsfh_v_zb)
    EditText zb;
    @InjectView(R.id.qgsfh_v_dp)
    EditText dp;
    @InjectView(R.id.qgsfh_v_kxs)
    EditText kxs;
    @InjectView(R.id.qgsfh_v_lds)
    EditText lds;
    @InjectView(R.id.qgsfh_v_zqbb)
    EditText zqbb;

    FragmentActivity activity;
    IScanReceiver receiver;
    ServerObserver serverObserver;
    ProcessUtil.SubmitResultReceiver submitResultReceiver;

    @Override
    public void setReceiver() {
        receiver = this;
        MyApplication.getScanUtil().setReceiver(receiver, 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(qgsfh, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;
        submitResultReceiver = this;
        serverObserver = new ServerObserver(this, "qgs", activity);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (jzbh.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first);
                } else {
                    //先清空表
                    DBHelper.getInstance(activity).delete(QgsfhProduct.class);
                    QgsfhProduct qgsfh = createQgsfh();
                    DBHelper.getInstance(activity).save(qgsfh);
                    MyApplication.toast(R.string.product_save_success);
                }
            }
        });

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jzbh.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))) {
                    MyApplication.toast(R.string.brickid_scan_first);
                } else {
                    QgsfhProduct qgsfh = createQgsfh();
                    qgsfh.setCode("qgs");
                    new ProcessUtil(activity).submit(submitResultReceiver, qgsfh);
                }
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        List<QgsfhProduct> plist = DBHelper.getInstance(activity).query(QgsfhProduct.class);
        if (plist.size() > 0) {
            QgsfhProduct qgsfh = plist.get(0);
            setPbjValue(qgsfh);
        }

        EditTextUtil.setNoKeyboard(yzd);
        EditTextUtil.setNoKeyboard(hbp);
        EditTextUtil.setNoKeyboard(zb);
        EditTextUtil.setNoKeyboard(dp);
        EditTextUtil.setNoKeyboard(kxs);
        EditTextUtil.setNoKeyboard(lds);
        EditTextUtil.setNoKeyboard(zqbb);

        return view;
    }

    @Override
    public void scanReceive(String res, int scanCode) {
        jzbh.setText(res);
        NetUtil.SetObserverCommonAction(NetUtil.getServices(false).getBrickInfo(res))
                .subscribe(serverObserver);
    }

    @Override
    public void serverData(ServerResult res) {
        EditTextUtil.setTextEnd(yzd, res.getRelVal("yb", "qgs", "yzd"));
        EditTextUtil.setTextEnd(hbp, res.getRelVal("yb", "qgs", "hbp"));
        EditTextUtil.setTextEnd(zb, res.getRelVal("yb", "qgs", "zb"));
        EditTextUtil.setTextEnd(dp, res.getRelVal("yb", "qgs", "dp"));
        EditTextUtil.setTextEnd(kxs, res.getRelVal("yb", "qgs", "kxs"));
        EditTextUtil.setTextEnd(lds, res.getRelVal("yb", "qgs", "lds"));
        EditTextUtil.setTextEnd(zqbb, res.getRelVal("yb", "qgs", "zqbb"));
    }

    @Override
    public void scanError() {
        MyApplication.toast(R.string.server_error);
    }

    public void setPbjValue(QgsfhProduct qgsfh) {
        jzbh.setText(qgsfh.getBrickId());
        EditTextUtil.setTextEnd(yzd, qgsfh.getYzd());
        EditTextUtil.setTextEnd(hbp, qgsfh.getHbp());
        EditTextUtil.setTextEnd(zb, qgsfh.getZb());
        EditTextUtil.setTextEnd(dp, qgsfh.getDp());
        EditTextUtil.setTextEnd(kxs, qgsfh.getKxs());
        EditTextUtil.setTextEnd(lds, qgsfh.getLds());
        EditTextUtil.setTextEnd(zqbb, qgsfh.getZqbb());
    }

    @Override
    public void submitOk() {
        clear();
    }

    public QgsfhProduct createQgsfh() {
        QgsfhProduct qgsfh = new QgsfhProduct();
        qgsfh.setBrickId(jzbh.getText().toString());
        qgsfh.setYzd(yzd.getText().toString());
        qgsfh.setHbp(hbp.getText().toString());
        qgsfh.setZb(zb.getText().toString());
        qgsfh.setDp(dp.getText().toString());
        qgsfh.setKxs(kxs.getText().toString());
        qgsfh.setLds(lds.getText().toString());
        qgsfh.setZqbb(zqbb.getText().toString());
        return qgsfh;
    }

    @Override
    public void clear() {
        jzbh.setText(R.string.wait_scan);
        yzd.setText("");
        hbp.setText("");
        zb.setText("");
        dp.setText("");
        kxs.setText("");
        lds.setText("");
        zqbb.setText("");
        DBHelper.getInstance(activity).delete(QgsfhProduct.class);
    }

    @Override
    public void serverError() {

    }
}
