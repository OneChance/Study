package com.example.zhouhui.study.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.zhouhui.study.IScanReceiver;
import com.example.zhouhui.study.MyApplication;
import com.example.zhouhui.study.R;
import com.example.zhouhui.study.adapter.FxDetailListAdapter;
import com.example.zhouhui.study.adapter.FxListAdapter;
import com.example.zhouhui.study.db.DBHelper;
import com.example.zhouhui.study.entity.FxDetail;
import com.example.zhouhui.study.entity.FxProduct;
import com.example.zhouhui.study.view.dialog.MaterialDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FxFragment extends BaseTagFragment implements FxListAdapter.ButtonCallbacks,IScanReceiver {

    public FxFragment() {
        this.tabNameId = R.string.fx_tab_name;
    }

    @InjectView(R.id.fx_scan_product)
    Button scanProduct;
    @InjectView(R.id.fx_brick_id)
    TextView brick;
    @InjectView(R.id.fx_product_list)
    RecyclerView listView;
    @InjectView(R.id.fx_save)
    Button save;

    FragmentActivity activity;

    List<FxProduct> list;
    FxListAdapter dataAdapter;
    IScanReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fx, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;

        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.scanUtil.send(receiver, 0);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.getInstance(activity).save(list);
            }
        });

        if (list == null) {
            list = new ArrayList<>();
        }

        List<FxProduct> plist = DBHelper.getInstance(activity).query(FxProduct.class);
        if (plist.size() > 0) {
            list = plist;
            brick.setText(plist.get(0).getBrickId());
        }

        dataAdapter = new FxListAdapter(getActivity(), list, this);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);

        return view;
    }


    @Override
    public void receive(String res,int scanCode) {

        brick.setText(res);
        DBHelper.getInstance(activity).deleteAll(FxProduct.class);
        list.clear();

        //取产品信息
        FxProduct p = new FxProduct();
        Random r = new Random();
        p.setBrickId(r.nextInt() + "");
        p.setLength(r.nextInt() + "");
        p.setWeight(r.nextInt() + "");
        p.setValidLength(r.nextInt() + "");
        p.setBbc(r.nextInt() + "");
        p.setNum(r.nextInt() + "");

        FxDetail d1 = new FxDetail();
        d1.setPieceReason(r.nextInt() + "");
        d1.setWeightNum(r.nextInt() + "");
        FxDetail d2 = new FxDetail();
        d2.setPieceReason(r.nextInt() + "");
        d2.setWeightNum(r.nextInt() + "");

        List<FxDetail> dList = new ArrayList<>();
        dList.add(d1);
        dList.add(d2);

        p.setdList(dList);

        list.add(p);
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void click(int position) {

        FxProduct p = list.get(position);

        final MaterialDialog dialog = new MaterialDialog(activity);

        View view = View.inflate(activity, R.layout.fx_detail, null);

        RecyclerView dListView = (RecyclerView) view.findViewById(R.id.fx_detail_list);
        dListView.setLayoutManager(new LinearLayoutManager(activity));

        final List<FxDetail> dList = p.getdList();

        FxDetailListAdapter detailDataAdapter = new FxDetailListAdapter(getActivity(), dList, new FxDetailListAdapter.FxDetailCallbacks() {
            @Override
            public void pieceReasonChange(int position, String val) {
                dList.get(position).setPieceReason(val);
            }

            @Override
            public void numWeightChange(int position, String val) {
                dList.get(position).setWeightNum(val);
            }
        });
        dListView.setAdapter(detailDataAdapter);

        dialog.setTitle(p.getBrickId());
        dialog.setContentView(view);
        dialog.setNegativeButton(R.string.dialog_dismiss, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(view);
            }
        });

        dialog.show();
    }

    @Override
    public void numChange(int position, String val) {
        list.get(position).setNum(val);
    }

}
