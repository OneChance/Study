package com.logic.mes.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.entity.process.PandianDetail;

import java.util.List;

import atownsend.swipeopenhelper.BaseSwipeOpenViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PandianListAdapter extends RecyclerView.Adapter<PandianListAdapter.ViewHolder> {

    private Context context;
    private List<PandianDetail> list;
    private final ButtonCallbacks callbacks;

    public interface ButtonCallbacks {
        void removePosition(int position);
    }

    public PandianListAdapter(Context context, List<PandianDetail> list, ButtonCallbacks callbacks) {
        this.list = list;
        this.context = context;
        this.callbacks = callbacks;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pandian_product_row, viewGroup, false);
        return new ViewHolder(v, callbacks);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PandianDetail p = list.get(position);
        holder.brickId.setText(p.getBrickId());
        holder.sequenceNum.setText(p.getSequenceNum());
        holder.weight.setText(String.valueOf(p.getWeight()));
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
        @BindView(R.id.pandian_v_sequence_num)
        TextView sequenceNum;
        @BindView(R.id.pandian_v_brickid)
        public TextView brickId;
        @BindView(R.id.pandian_v_weight)
        EditText weight;
        @BindView(R.id.pb_delete_button)
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
