package com.logic.mes.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.logic.mes.IScanReceiver;
import com.logic.mes.MyApplication;
import com.logic.mes.R;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.process.PbjProduct;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PbjFragment extends BaseTagFragment implements IScanReceiver{

    public PbjFragment() {
        this.tagNameId = R.string.pbj_tab_name;
    }

    @InjectView(R.id.pbj_scan_product)
    Button scanProduct;
    @InjectView(R.id.pbj_save)
    Button save;
    @InjectView(R.id.pbj_b_quali)
    Button bQuali;
    @InjectView(R.id.pbj_b_unquali)
    Button bUnquali;
    @InjectView(R.id.pbj_brick_id)
    TextView brickId;
    @InjectView(R.id.pbj_v_code)
    TextView codeValue;
    @InjectView(R.id.pbj_v_bb)
    EditText bbValue;
    @InjectView(R.id.pbj_v_zcbc)
    EditText zcbcValue;
    @InjectView(R.id.pbj_v_zdbc)
    EditText zdbcValue;
    @InjectView(R.id.pbj_v_yxbc)
    EditText yxbcValue;
    @InjectView(R.id.pbj_v_size)
    EditText sizeValue;
    @InjectView(R.id.pbj_v_dj)
    EditText djValue;

    FragmentActivity activity;
    IScanReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pbj, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;

        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyApplication.scanUtil.send(receiver, 0);
                receiver.receive("12345",0);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(codeValue!=null && !codeValue.equals("")){

                    //先清空表
                    DBHelper.getInstance(activity).delete(PbjProduct.class);

                    PbjProduct pbj = new PbjProduct();
                    pbj.setBrickId(brickId.getText().toString());
                    pbj.setCodeValue(codeValue.getText().toString());
                    pbj.setBbValue(bbValue.getText().toString());
                    pbj.setSizeValue(sizeValue.getText().toString());
                    pbj.setYxbcValue(yxbcValue.getText().toString());
                    pbj.setZcbcValue(zcbcValue.getText().toString());
                    pbj.setZdbcValue(zdbcValue.getText().toString());
                    pbj.setDjValue(djValue.getText().toString());
                    DBHelper.getInstance(activity).save(pbj);
                    MyApplication.toast("保存成功");
                }
            }
        });

        bQuali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bUnquali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        List<PbjProduct> plist = DBHelper.getInstance(activity).query(PbjProduct.class);
        if (plist.size() > 0) {
            PbjProduct pbj = plist.get(0);
            setPbjValue(pbj);
        }

        return view;
    }

    @Override
    public void receive(String res,int scanCode) {
        PbjProduct pbj = new PbjProduct();
        pbj.setBrickId("1111");
        pbj.setCodeValue("2222");
        pbj.setBbValue("3333");
        pbj.setSizeValue("4444");
        pbj.setYxbcValue("5555");
        pbj.setZcbcValue("6666");
        pbj.setZdbcValue("7777");
        pbj.setDjValue("8888");
        setPbjValue(pbj);
    }

    public void setPbjValue(PbjProduct pbj){
        brickId.setText(pbj.getBrickId());
        codeValue.setText(pbj.getCodeValue());
        bbValue.setText(pbj.getBbValue());
        zcbcValue.setText(pbj.getZcbcValue());
        zdbcValue.setText(pbj.getZdbcValue());
        yxbcValue.setText(pbj.getYxbcValue());
        sizeValue.setText(pbj.getSizeValue());
        djValue.setText(pbj.getDjValue());
    }
}
