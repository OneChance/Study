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
import com.logic.mes.entity.process.QxProduct;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.logic.mes.R.layout.qx;

public class QxFragment extends BaseTagFragment implements IScanReceiver{

    public QxFragment() {this.tagNameId = R.string.qx_tab_name;}

    @InjectView(R.id.yb_scan_product)
    Button scanProduct;
    @InjectView(R.id.yb_save)
    Button save;
    @InjectView(R.id.qx_v_jzbh_head)
    TextView jzbhHead;
    @InjectView(R.id.qx_b_submit)
    Button bSubmit;
    @InjectView(R.id.qx_b_clear)
    Button bClear;
    @InjectView(R.id.qx_v_station)
    TextView station;
    @InjectView(R.id.qx_v_jzbh)
    TextView jzbh;
    @InjectView(R.id.qx_v_llcps)
    TextView llcps;
    @InjectView(R.id.qx_v_sjcps)
    EditText sjcps;
    @InjectView(R.id.qx_v_hs)
    EditText hs;
    @InjectView(R.id.qx_v_ps)
    EditText ps;
    @InjectView(R.id.qx_v_bb)
    EditText bb;
    @InjectView(R.id.qx_v_yp)
    EditText yp;
    @InjectView(R.id.qx_v_jjqxs)
    EditText jjqxs;
    @InjectView(R.id.qx_v_sbqxs)
    EditText sbqxs;
    @InjectView(R.id.qx_v_qt)
    EditText qt;
    @InjectView(R.id.qx_v_zj)
    TextView zj;

    FragmentActivity activity;
    IScanReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(qx, container, false);

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

                if(jzbhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))){
                    MyApplication.toast(R.string.brickid_scan_first);
                }else{
                    //先清空表
                    DBHelper.getInstance(activity).delete(QxProduct.class);

                    QxProduct qx = new QxProduct();
                    qx.setJzbh(jzbh.getText().toString());
                    qx.setStation(station.getText().toString());
                    qx.setLlcps(llcps.getText().toString());
                    qx.setSjcps(sjcps.getText().toString());
                    qx.setHs(hs.getText().toString());
                    qx.setPs(ps.getText().toString());
                    qx.setBb(bb.getText().toString());
                    qx.setYp(yp.getText().toString());
                    qx.setJjqxs(jjqxs.getText().toString());
                    qx.setSbqxs(sbqxs.getText().toString());
                    qx.setZj(zj.getText().toString());
                    qx.setQt(qt.getText().toString());
                    DBHelper.getInstance(activity).save(qx);
                    MyApplication.toast(R.string.product_save_success);
                }
            }
        });

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(jzbhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))){
                    MyApplication.toast(R.string.brickid_scan_first);
                }else{
                    //保存成功后删除数据库数据
                    DBHelper.getInstance(activity).delete(QxProduct.class);
                }
            }
        });

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jzbhHead.setText(R.string.wait_scan);
                jzbh.setText("");
                station.setText("");
                llcps.setText("");
                sjcps.setText("");
                hs.setText("");
                ps.setText("");
                bb.setText("");
                yp.setText("");
                jjqxs.setText("");
                sbqxs.setText("");
                zj.setText("0");
                qt.setText("");
            }
        });

        List<QxProduct> plist = DBHelper.getInstance(activity).query(QxProduct.class);
        if (plist.size() > 0) {
            QxProduct qx = plist.get(0);
            setPbjValue(qx);
        }

        EditTextUtil.setNoKeyboard(sjcps);
        EditTextUtil.setNoKeyboard(hs);
        EditTextUtil.setNoKeyboard(ps);
        EditTextUtil.setNoKeyboard(bb);
        EditTextUtil.setNoKeyboard(yp);
        EditTextUtil.setNoKeyboard(jjqxs);
        EditTextUtil.setNoKeyboard(sbqxs);
        EditTextUtil.setNoKeyboard(qt);

        return view;
    }

    @Override
    public void receive(String res,int scanCode) {
        QxProduct qx = new QxProduct();
        qx.setJzbh("8888");
        qx.setStation("1F");
        qx.setLlcps("");
        qx.setSjcps("");
        qx.setHs("");
        qx.setPs("");
        qx.setBb("");
        qx.setYp("");
        qx.setJjqxs("");
        qx.setSbqxs("");
        qx.setZj("0");
        qx.setQt("");
        setPbjValue(qx);
    }

    @Override
    public void error() {

    }

    public void setPbjValue(QxProduct qx){
        jzbhHead.setText(qx.getJzbh());
        jzbh.setText(qx.getJzbh());
        station.setText(qx.getStation());
        llcps.setText(qx.getLlcps());
        sjcps.setText(qx.getSjcps());
        hs.setText(qx.getHs());
        ps.setText(qx.getPs());
        bb.setText(qx.getBb());
        yp.setText(qx.getYp());
        jjqxs.setText(qx.getJjqxs());
        sbqxs.setText(qx.getSbqxs());
        zj.setText(qx.getZj());
        qt.setText(qx.getQt());
    }
}
