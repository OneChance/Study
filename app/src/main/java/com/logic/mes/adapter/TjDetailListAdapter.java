package com.logic.mes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.entity.process.TjDetail;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TjDetailListAdapter extends RecyclerView.Adapter<TjDetailListAdapter.ViewHolder> {

    private Context context;
    private List<TjDetail> list;

    public TjDetailListAdapter(Context context, List<TjDetail> list) {
        this.list = list;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tj_detail_row, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TjDetail p = list.get(position);
        holder.pieceReason.setText(p.getPieceReason());
        holder.weightNum.setText(p.getWeightNum());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        @InjectView(R.id.tj_piece_reason)
        public TextView pieceReason;
        @InjectView(R.id.tj_num_weight)
        public TextView weightNum;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }
}
