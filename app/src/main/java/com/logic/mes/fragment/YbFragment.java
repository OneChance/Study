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
import com.logic.mes.entity.process.YbProduct;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.logic.mes.R.layout.yb;

public class YbFragment extends BaseTagFragment implements IScanReceiver{

    public YbFragment() {
        this.tagNameId = R.string.yb_tab_name;
    }

    @InjectView(R.id.yb_scan_product)
    Button scanProduct;
    @InjectView(R.id.yb_save)
    Button save;
    @InjectView(R.id.yb_v_jzbh)
    TextView jzbh;
    @InjectView(R.id.yb_b_submit)
    Button bSubmit;
    @InjectView(R.id.yb_b_clear)
    Button bClear;
    @InjectView(R.id.yb_v_yzd)
    EditText yzd;
    @InjectView(R.id.yb_v_hbp)
    EditText hbp;
    @InjectView(R.id.yb_v_zb)
    EditText zb;
    @InjectView(R.id.yb_v_dp)
    EditText dp;
    @InjectView(R.id.yb_v_kxs)
    EditText kxs;
    @InjectView(R.id.yb_v_lds)
    EditText lds;
    @InjectView(R.id.yb_v_zqbb)
    EditText zqbb;

    FragmentActivity activity;
    IScanReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(yb, container, false);

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
                    DBHelper.getInstance(activity).delete(YbProduct.class);

                    YbProduct yb = new YbProduct();
                    yb.setJzbh(jzbh.getText().toString());
                    yb.setYzd(yzd.getText().toString());
                    yb.setHbp(hbp.getText().toString());
                    yb.setZb(zb.getText().toString());
                    yb.setDp(dp.getText().toString());
                    yb.setKxs(kxs.getText().toString());
                    yb.setLds(lds.getText().toString());
                    yb.setZqbb(zqbb.getText().toString());
                    DBHelper.getInstance(activity).save(yb);
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
                    DBHelper.getInstance(activity).delete(YbProduct.class);
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

        List<YbProduct> plist = DBHelper.getInstance(activity).query(YbProduct.class);
        if (plist.size() > 0) {
            YbProduct yb = plist.get(0);
            setPbjValue(yb);
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
        YbProduct yb = new YbProduct();
        yb.setJzbh("8888");
        yb.setYzd("");
        yb.setHbp("");
        yb.setZb("");
        yb.setDp("");
        yb.setKxs("");
        yb.setLds("");
        yb.setZqbb("");
        setPbjValue(yb);
    }

    @Override
    public void error() {

    }

    public void setPbjValue(YbProduct yb){
        jzbh.setText(yb.getJzbh());
        yzd.setText(yb.getYzd());
        hbp.setText(yb.getHbp());
        zb.setText(yb.getZb());
        dp.setText(yb.getDp());
        kxs.setText(yb.getKxs());
        lds.setText(yb.getLds());
        zqbb.setText(yb.getZqbb());
    }
}
