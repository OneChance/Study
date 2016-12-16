package com.logic.mes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.entity.process.JbDetail;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class JbListAdapter extends RecyclerView.Adapter<JbListAdapter.ViewHolder> {

    private Context context;
    private List<JbDetail> list;

    public interface ButtonCallbacks {
        void removePosition(int position);
    }

    public JbListAdapter(Context context, List<JbDetail> list) {
        this.list = list;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.jb_product_row, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final JbDetail p = list.get(position);
        holder.brickId.setText(p.getBrickId());
        holder.length.setText(p.getLength());
        holder.station.setText(p.getStation());
        holder.level.setText(p.getLevel());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        @InjectView(R.id.jb_brick_id)
        TextView brickId;
        @InjectView(R.id.jb_length)
        TextView length;
        @InjectView(R.id.jb_level)
        TextView level;
        @InjectView(R.id.jb_v_station)
        public TextView station;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

}
