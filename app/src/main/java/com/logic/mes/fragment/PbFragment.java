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
import com.logic.mes.adapter.PbListAdapter;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.PbProduct;

import java.util.ArrayList;
import java.util.List;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class PbFragment extends BaseTagFragment implements PbListAdapter.ButtonCallbacks,IScanReceiver{

    public PbFragment() {
        this.tabNameId = R.string.pb_tab_name;
    }

    public final int SCAN_CODE_STATION = 0;
    public final int SCAN_CODE_PRODUCT = 1;

    @InjectView(R.id.pb_scan_station)
    Button scanStation;
    @InjectView(R.id.pb_scan_product)
    Button scanProduct;
    @InjectView(R.id.pb_station)
    TextView station;
    @InjectView(R.id.pb_product_list)
    RecyclerView listView;
    @InjectView(R.id.pb_save)
    Button save;
    List<PbProduct> list;
    PbListAdapter dataAdapter;
    FragmentActivity activity;
    IScanReceiver receiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pb, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;

        scanStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.scanUtil.send(receiver, SCAN_CODE_STATION);
            }
        });

        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.scanUtil.send(receiver, SCAN_CODE_PRODUCT);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.getInstance(activity).save(list);
            }
        });

        if(list==null){
            list = new ArrayList<>();
        }

        List<PbProduct> plist = DBHelper.getInstance(activity).query(PbProduct.class);
        if (plist.size() > 0) {
            list = plist;
        }

        dataAdapter = new PbListAdapter(getActivity(), list, this);
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);
        helper.attachToRecyclerView(listView);
        helper.setCloseOnAction(false);

        return view;
    }

    @Override
    public void removePosition(int position) {
        PbProduct p = list.get(position);
        if (p.getId() != 0) {
            DBHelper.getInstance(activity).delete(p);
        }
        dataAdapter.removePosition(position);
    }

    @Override
    public void receive(String res,int scanCode) {
        if (scanCode == SCAN_CODE_STATION) {
            station.setText(res);
        } else if (scanCode == SCAN_CODE_PRODUCT) {
            //取产品信息
            PbProduct p = new PbProduct();
            p.setBrickId("13424234234");
            p.setLength("11232131235");
            list.add(p);
            dataAdapter.notifyDataSetChanged();
        }
    }
}
