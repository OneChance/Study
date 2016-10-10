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
import com.logic.mes.adapter.ZxListAdapter;
import com.logic.mes.db.DBHelper;
import com.logic.mes.entity.process.ZxProduct;

import java.util.ArrayList;
import java.util.List;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ZxFragment extends BaseTagFragment implements ZxListAdapter.ButtonCallbacks,IScanReceiver{

    public ZxFragment() {
        this.tagNameId = R.string.zx_tab_name;
    }

    public final int SCAN_CODE_XZ = 0;
    public final int SCAN_CODE_HZ = 1;

    @InjectView(R.id.zx_b_scan_xz)
    Button scanXz;
    @InjectView(R.id.zx_b_scan_hz)
    Button scanHz;
    @InjectView(R.id.zx_v_xh_head)
    TextView xhHead;
    @InjectView(R.id.zx_product_list)
    RecyclerView listView;
    @InjectView(R.id.zx_save)
    Button save;

    @InjectView(R.id.zx_b_submit)
    Button submit;
    @InjectView(R.id.zx_b_clear)
    Button clear;

    List<ZxProduct> list;
    ZxListAdapter dataAdapter;
    FragmentActivity activity;
    IScanReceiver receiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.zx, container, false);

        ButterKnife.inject(this, view);

        activity = getActivity();
        receiver = this;

        scanXz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyApplication.scanUtil.send(receiver, SCAN_CODE_STATION);
                receiver.receive("721",SCAN_CODE_XZ);
            }
        });

        scanHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(xhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))){
                    MyApplication.toast(R.string.xz_scan_first);
                }else{
                    //MyApplication.scanUtil.send(receiver, SCAN_CODE_PRODUCT);
                    receiver.receive("1234",SCAN_CODE_HZ);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.getInstance(activity).delete(ZxProduct.class);
                DBHelper.getInstance(activity).save(list);
                MyApplication.toast(R.string.product_save_success);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(xhHead.getText().toString().equals(MyApplication.getResString(R.string.wait_scan))){
                    MyApplication.toast(R.string.xz_scan_first);
                }else if(list.size()==0){
                    MyApplication.toast(R.string.hz_scan_need);
                }else{

                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xhHead.setText(MyApplication.getResString(R.string.wait_scan));
                list.clear();
                dataAdapter.notifyDataSetChanged();
            }
        });

        if(list==null){
            list = new ArrayList<>();
        }

        List<ZxProduct> plist = DBHelper.getInstance(activity).query(ZxProduct.class);
        if (plist.size() > 0) {
            list = plist;
            xhHead.setText(plist.get(0).getXh());
        }

        dataAdapter = new ZxListAdapter(getActivity(), list, this);
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(dataAdapter);
        helper.attachToRecyclerView(listView);
        helper.setCloseOnAction(false);

        return view;
    }

    @Override
    public void removePosition(int position) {
        ZxProduct p = list.get(position);
        if (p.getId() != 0) {
            DBHelper.getInstance(activity).delete(p);
        }
        dataAdapter.removePosition(position);
    }

    @Override
    public void receive(String res,int scanCode) {
        if (scanCode == SCAN_CODE_XZ) {
            xhHead.setText(res);
        } else if (scanCode == SCAN_CODE_HZ) {
            //取产品信息
            ZxProduct p = new ZxProduct();
            p.setXh("1234");
            p.setHh("241-234234-25435-123");
            list.add(p);
            dataAdapter.notifyDataSetChanged();
        }
    }
}
