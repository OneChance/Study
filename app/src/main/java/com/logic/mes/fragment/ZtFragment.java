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
import com.logic.mes.adapter.ZtListAdapter;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.process.ZtProduct;

import java.util.ArrayList;
import java.util.List;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ZtFragment extends BaseTagFragment implements ZtListAdapter.ButtonCallbacks,IScanReceiver{

    public ZtFragment() {
        this.tagNameId = R.string.zt_tab_name;
    }

    public final int SCAN_CODE_TH = 0;
    public final int SCAN_CODE_XZ = 1;

    @InjectView(R.id.zt_b_scan_th)
    Button scanTh;
    @InjectView(R.id.zt_b_scan_xz)
    Button scanXz;
    @InjectView(R.id.zt_v_th_head)
    TextView thHead;
    @InjectView(R.id.zt_product_list)
    RecyclerView listView;
    @InjectView(R.id.zt_save)
    Button save;

    @InjectView(R.id.zt_b_submit)
    Button submit;
    @InjectView(R.id.zt_b_clear)
    Button clear;

    List<ZtProduct> list;
    ZtListAdapter dataAdapter;
    FragmentActivity activity;
    IScanReceiver receiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.zt, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;

        scanTh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyApplication.scanUtil.send(receiver, SCAN_CODE_STATION);
                receiver.receive("2222",SCAN_CODE_TH);
            }
        });

        scanXz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))){
                    MyApplication.toast(R.string.th_scan_first);
                }else{
                    //MyApplication.scanUtil.send(receiver, SCAN_CODE_PRODUCT);
                    receiver.receive("241-234234-25435-123",SCAN_CODE_XZ);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size()>0){
                    DBHelper.getInstance(activity).delete(ZtProduct.class);
                    DBHelper.getInstance(activity).save(list);
                    MyApplication.toast(R.string.product_save_success);
                }else{
                    MyApplication.toast(R.string.xz_scan_need);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))){
                    MyApplication.toast(R.string.th_scan_first);
                }else if(list.size()==0){
                    MyApplication.toast(R.string.xz_scan_need);
                }else{

                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thHead.setText(MyApplication.getResString(R.string.wait_scan));
                list.clear();
                dataAdapter.notifyDataSetChanged();
            }
        });

        if(list==null){
            list = new ArrayList<>();
        }

        List<ZtProduct> plist = DBHelper.getInstance(activity).query(ZtProduct.class);
        if (plist.size() > 0) {
            list = plist;
            thHead.setText(plist.get(0).getTh());
        }

        dataAdapter = new ZtListAdapter(getActivity(), list, this);
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);
        helper.attachToRecyclerView(listView);
        helper.setCloseOnAction(false);

        return view;
    }

    @Override
    public void removePosition(int position) {
        ZtProduct p = list.get(position);
        if (p.getId() != 0) {
            DBHelper.getInstance(activity).delete(p);
        }
        dataAdapter.removePosition(position);
    }

    @Override
    public void receive(String res,int scanCode) {
        if (scanCode == SCAN_CODE_TH) {
            thHead.setText(res);
        } else if (scanCode == SCAN_CODE_XZ) {
            //取产品信息
            ZtProduct p = new ZtProduct();
            p.setTh(thHead.getText().toString());
            p.setXh(res);
            list.add(p);
            dataAdapter.notifyDataSetChanged();
        }
    }
}
