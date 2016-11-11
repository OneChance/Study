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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.logic.mes.R.layout.qgsfh;

public class QgsfhFragment extends BaseTagFragment implements IScanReceiver{

    public QgsfhFragment() {
        this.tagNameId = R.string.qgsfh_tab_name;
    }

    @InjectView(R.id.qgsfh_scan_product)
    Button scanProduct;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(qgsfh, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;

        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getScanUtil().send(receiver, 0);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(jzbh.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))){
                    MyApplication.toast(R.string.brickid_scan_first);
                }else{
                    //先清空表
                    DBHelper.getInstance(activity).delete(QgsfhProduct.class);

                    QgsfhProduct qgsfh = new QgsfhProduct();
                    qgsfh.setJzbh(jzbh.getText().toString());
                    qgsfh.setYzd(yzd.getText().toString());
                    qgsfh.setHbp(hbp.getText().toString());
                    qgsfh.setZb(zb.getText().toString());
                    qgsfh.setDp(dp.getText().toString());
                    qgsfh.setKxs(kxs.getText().toString());
                    qgsfh.setLds(lds.getText().toString());
                    qgsfh.setZqbb(zqbb.getText().toString());
                    DBHelper.getInstance(activity).save(qgsfh);
                    MyApplication.toast(R.string.product_save_success);
                }
            }
        });

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(jzbh.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))){
                    MyApplication.toast(R.string.brickid_scan_first);
                }else{
                    //保存成功后删除数据库数据
                    DBHelper.getInstance(activity).delete(QgsfhProduct.class);
                }
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jzbh.setText(R.string.wait_scan);
                yzd.setText("");
                hbp.setText("");
                zb.setText("");
                dp.setText("");
                kxs.setText("");
                lds.setText("");
                zqbb.setText("");
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
    public void receive(String res,int scanCode) {
        QgsfhProduct qgsfh = new QgsfhProduct();
        qgsfh.setJzbh("8888");
        qgsfh.setYzd("");
        qgsfh.setHbp("");
        qgsfh.setZb("");
        qgsfh.setDp("");
        qgsfh.setKxs("");
        qgsfh.setLds("");
        qgsfh.setZqbb("");
        setPbjValue(qgsfh);
    }

    @Override
    public void error() {

    }

    public void setPbjValue(QgsfhProduct qgsfh){
        jzbh.setText(qgsfh.getJzbh());
        yzd.setText(qgsfh.getYzd());
        hbp.setText(qgsfh.getHbp());
        zb.setText(qgsfh.getZb());
        dp.setText(qgsfh.getDp());
        kxs.setText(qgsfh.getKxs());
        lds.setText(qgsfh.getLds());
        zqbb.setText(qgsfh.getZqbb());
    }
}
