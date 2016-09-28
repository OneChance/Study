package com.logic.mes.adapter;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.entity.TjProduct;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TjListAdapter extends RecyclerView.Adapter<TjListAdapter.ViewHolder> {

    private Context context;
    private List<TjProduct> list;
    private final ButtonCallbacks callbacks;

    public interface ButtonCallbacks {
        void click(int position);
    }

    public TjListAdapter(Context context, List<TjProduct> list, ButtonCallbacks callbacks) {
        this.list = list;
        this.context = context;
        this.callbacks = callbacks;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tj_product_row, viewGroup, false);
        return new ViewHolder(v, callbacks);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TjProduct p = list.get(position);
        holder.brickId.setText(p.getBrickId());
        holder.length.setText(p.getLength());
        holder.weight.setText(p.getWeight());
        holder.validLength.setText(p.getValidLength());
        holder.bbc.setText(p.getBbc());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        @InjectView(R.id.content_view)
        public PercentRelativeLayout content;
        @InjectView(R.id.tj_brick_id)
        public TextView brickId;
        @InjectView(R.id.tj_length)
        public TextView length;
        @InjectView(R.id.tj_weight)
        public TextView weight;
        @InjectView(R.id.tj_valid_length)
        public TextView validLength;
        @InjectView(R.id.tj_bbc)
        public TextView bbc;

        public ViewHolder(View v, final ButtonCallbacks callbacks) {
            super(v);
            ButterKnife.inject(this, v);

            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbacks.click(getAdapterPosition());
                }
            });
        }
    }
}
