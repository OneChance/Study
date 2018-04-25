package com.logic.mes.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.entity.process.RkDetail;

import java.util.List;

import atownsend.swipeopenhelper.BaseSwipeOpenViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RkListAdapter extends RecyclerView.Adapter<RkListAdapter.ViewHolder> {

    private Context context;
    private List<RkDetail> list;
    private final ButtonCallbacks callbacks;
    private final RkDetailCallback rkDetailCallback;

    public interface RkDetailCallback {
        void selectChange(int position, String value);
    }

    public interface ButtonCallbacks {
        void removePosition(int position);
    }

    public RkListAdapter(Context context, List<RkDetail> list, ButtonCallbacks callbacks, RkDetailCallback rkDetailCallback) {
        this.list = list;
        this.context = context;
        this.callbacks = callbacks;
        this.rkDetailCallback = rkDetailCallback;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rk_product_row, viewGroup, false);
        return new ViewHolder(v, callbacks, rkDetailCallback);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RkDetail p = list.get(position);
        holder.lb.setText(p.getLb());
        holder.tm.setText(p.getTm());
        holder.sl.setText(p.getSl());
        SpinnerAdapter apsAdapter = holder.jf.getAdapter();
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (p.getJf().equals(apsAdapter.getItem(i).toString())) {
                holder.jf.setSelection(i, true);
                break;
            }
        }
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
        @BindView(R.id.rk_v_lb)
        public TextView lb;
        @BindView(R.id.rk_v_tm)
        public TextView tm;
        @BindView(R.id.rk_v_sl)
        public TextView sl;
        @BindView(R.id.rk_delete_button)
        public TextView deleteButton;
        @BindView(R.id.rk_v_jf)
        Spinner jf;

        public ViewHolder(View v, final ButtonCallbacks callbacks, final RkDetailCallback rkDetailCallback) {
            super(v);
            ButterKnife.bind(this, v);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbacks.removePosition(getAdapterPosition());
                }
            });

            jf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    rkDetailCallback.selectChange(getAdapterPosition(), jf.getItemAtPosition(i).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

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
