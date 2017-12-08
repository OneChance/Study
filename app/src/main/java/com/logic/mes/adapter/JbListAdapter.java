package com.logic.mes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.entity.process.JbDetail;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

public class JbListAdapter extends RecyclerView.Adapter<JbListAdapter.ViewHolder> {

    private Context context;
    private List<JbDetail> list;
    private final JbDetailCallback jbDetailCallback;

    public interface JbDetailCallback {
        void checkedChange(int position, boolean checked);
    }


    public JbListAdapter(Context context, List<JbDetail> list, JbDetailCallback jbDetailCallback) {
        this.list = list;
        this.context = context;
        this.jbDetailCallback = jbDetailCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.jb_product_row, viewGroup, false);
        return new ViewHolder(v, jbDetailCallback);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final JbDetail p = list.get(position);
        holder.brickId.setText(p.getBrickId());
        holder.length.setText(p.getLength());
        holder.station.setText(p.getStation());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        @BindView(R.id.jb_brick_id)
        TextView brickId;
        @BindView(R.id.jb_length)
        TextView length;
        @BindView(R.id.jb_v_bf)
        CheckBox bf;
        @BindView(R.id.jb_v_station)
        public TextView station;

        public ViewHolder(View v, final JbDetailCallback jbDetailCallback) {
            super(v);
            ButterKnife.bind(this, v);

            bf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    jbDetailCallback.checkedChange(getAdapterPosition(), b);
                }
            });
        }
    }

}
