package com.logic.mes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.logic.mes.R;
import com.logic.mes.entity.process.FxDetail;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FxDetailListAdapter extends RecyclerView.Adapter<FxDetailListAdapter.ViewHolder> {

    private Context context;
    private List<FxDetail> list;
    private final FxDetailCallbacks callbacks;

    public FxDetailListAdapter(Context context, List<FxDetail> list, FxDetailCallbacks callbacks) {
        this.list = list;
        this.context = context;
        this.callbacks = callbacks;
    }

    public interface FxDetailCallbacks {
        void pieceReasonChange(int position, String val);

        void numWeightChange(int position, String val);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fx_detail_row, viewGroup, false);
        return new ViewHolder(v, callbacks);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FxDetail p = list.get(position);
        holder.pieceReason.setText(p.getPieceReason());
        holder.weightNum.setText(p.getWeightNum());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        @InjectView(R.id.fx_piece_reason)
        public EditText pieceReason;
        @InjectView(R.id.fx_num_weight)
        public EditText weightNum;

        public ViewHolder(View v, final FxDetailCallbacks callbacks) {
            super(v);
            ButterKnife.inject(this, v);

            pieceReason.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    callbacks.pieceReasonChange(getAdapterPosition(), pieceReason.getText().toString());
                }
            });

            weightNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    callbacks.numWeightChange(getAdapterPosition(), weightNum.getText().toString());
                }
            });
        }
    }
}
