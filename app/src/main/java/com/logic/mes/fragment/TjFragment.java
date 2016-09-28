package com.logic.mes.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.adapter.TjDetailListAdapter;
import com.logic.mes.adapter.TjListAdapter;
import com.logic.mes.db.DBHelper;
import com.logic.mes.dialog.MaterialDialog;
import com.logic.mes.entity.TjDetail;
import com.logic.mes.entity.TjProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TjFragment extends BaseTagFragment implements TjListAdapter.ButtonCallbacks {

    public TjFragment() {
        this.tabNameId = R.string.tj_tab_name;
    }

    public final int SCAN_CODE_PRODUCT = 1;

    @InjectView(R.id.tj_scan_product)
    Button scanProduct;
    @InjectView(R.id.tj_brick_id)
    TextView brick;
    @InjectView(R.id.tj_product_list)
    RecyclerView listView;
    @InjectView(R.id.tj_save)
    Button save;

    FragmentActivity activity;

    List<TjProduct> list;
    TjListAdapter dataAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tj, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();

        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        List<TjProduct> plist = DBHelper.getInstance(activity).query(TjProduct.class);
        if (plist.size() > 0) {
            list = plist;
            brick.setText(plist.get(0).getBrickId());
        }

        dataAdapter = new TjListAdapter(getActivity(), list, this);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");

            brick.setText(scanResult);

            DBHelper.getInstance(activity).deleteAll(TjProduct.class);
            list.clear();

            //取产品信息
            TjProduct p = new TjProduct();
            Random r = new Random();
            p.setBrickId(r.nextInt() + "");
            p.setLength(r.nextInt() + "");
            p.setWeight(r.nextInt() + "");
            p.setValidLength(r.nextInt() + "");
            p.setBbc(r.nextInt() + "");

            TjDetail d1 = new TjDetail();
            d1.setPieceReason(r.nextInt() + "");
            d1.setWeightNum(r.nextInt() + "");
            TjDetail d2 = new TjDetail();
            d2.setPieceReason(r.nextInt() + "");
            d2.setWeightNum(r.nextInt() + "");

            List<TjDetail> dList = new ArrayList<>();
            dList.add(d1);
            dList.add(d2);

            p.setdList(dList);

            list.add(p);
            dataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void click(int position) {

        TjProduct p = list.get(position);

        final MaterialDialog dialog = new MaterialDialog(activity);

        View view = View.inflate(activity, R.layout.tj_detail, null);

        RecyclerView dListView = (RecyclerView) view.findViewById(R.id.tj_detail_list);
        dListView.setLayoutManager(new LinearLayoutManager(activity));
        TjDetailListAdapter detailDataAdapter = new TjDetailListAdapter(getActivity(), p.getdList());
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
}
