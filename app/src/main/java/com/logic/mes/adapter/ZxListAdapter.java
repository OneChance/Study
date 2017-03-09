package com.logic.mes.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.entity.process.ZxProduct;

import java.util.List;

import atownsend.swipeopenhelper.BaseSwipeOpenViewHolder;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ZxListAdapter extends RecyclerView.Adapter<ZxListAdapter.ViewHolder> {

    private Context context;
    private List<ZxProduct> list;
    private final ButtonCallbacks callbacks;

    public interface ButtonCallbacks {
        void removePosition(int position);
    }

    public ZxListAdapter(Context context, List<ZxProduct> list, ButtonCallbacks callbacks) {
        this.list = list;
        this.context = context;
        this.callbacks = callbacks;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.zx_product_row, viewGroup, false);
        return new ViewHolder(v, callbacks);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ZxProduct p = list.get(position);
        holder.sl.setText(p.getSl());
        holder.hh.setText(p.getHh());
        holder.level.setText(p.getLevel());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void removePosition(int adapterPosition) {
        list.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }


    public static class ViewHolder
            extends BaseSwipeOpenViewHolder {

        @InjectView(R.id.content_view)
        public PercentRelativeLayout content;
        @InjectView(R.id.zx_v_hh)
        public TextView hh;
        @InjectView(R.id.zx_v_sl)
        public TextView sl;
        @InjectView(R.id.zx_v_level)
        public TextView level;
        @InjectView(R.id.zx_delete_button)
        public TextView deleteButton;

        public ViewHolder(View v, final ButtonCallbacks callbacks) {
            super(v);
            ButterKnife.inject(this, v);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbacks.removePosition(getAdapterPosition());
                }
            });
        }

        @NonNull
        @Override
        public View getSwipeView() {
            return content;
        }

        @Override
        public float getEndHiddenViewSize() {
            return deleteButton.getMeasuredWidth();
        }

        @Override
        public float getStartHiddenViewSize() {
            return 0;
        }

        @Override
        public void notifyEndOpen() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.swipe_delete));
        }

        @Override
        public void notifyStartOpen() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.swipe_back));
        }

    }

}
