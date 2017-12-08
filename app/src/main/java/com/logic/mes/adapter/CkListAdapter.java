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
import com.logic.mes.entity.process.CkDetail;

import java.util.List;

import atownsend.swipeopenhelper.BaseSwipeOpenViewHolder;
import butterknife.ButterKnife;
import butterknife.BindView;

public class CkListAdapter extends RecyclerView.Adapter<CkListAdapter.ViewHolder> {

    private Context context;
    private List<CkDetail> list;
    private final ButtonCallbacks callbacks;

    public interface ButtonCallbacks {
        void removePosition(int position);
    }

    public CkListAdapter(Context context, List<CkDetail> list, ButtonCallbacks callbacks) {
        this.list = list;
        this.context = context;
        this.callbacks = callbacks;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ck_product_row, viewGroup, false);
        return new ViewHolder(v, callbacks);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CkDetail p = list.get(position);
        holder.lb.setText(p.getLb());
        holder.tm.setText(p.getTm());
        holder.sl.setText(p.getSl());
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

        @BindView(R.id.content_view)
        public PercentRelativeLayout content;
        @BindView(R.id.ck_v_lb)
        public TextView lb;
        @BindView(R.id.ck_v_tm)
        public TextView tm;
        @BindView(R.id.ck_v_sl)
        public TextView sl;
        @BindView(R.id.ck_delete_button)
        public TextView deleteButton;

        public ViewHolder(View v, final ButtonCallbacks callbacks) {
            super(v);
            ButterKnife.bind(this, v);

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
