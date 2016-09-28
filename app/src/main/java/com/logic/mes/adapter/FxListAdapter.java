package com.logic.mes.adapter;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.logic.mes.R;
import com.logic.mes.entity.FxProduct;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FxListAdapter extends RecyclerView.Adapter<FxListAdapter.ViewHolder> {

    private Context context;
    private List<FxProduct> list;
    private final ButtonCallbacks callbacks;

    public interface ButtonCallbacks {
        void click(int position);

        void numChange(int position, String val);
    }

    public FxListAdapter(Context context, List<FxProduct> list, ButtonCallbacks callbacks) {
        this.list = list;
        this.context = context;
        this.callbacks = callbacks;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fx_product_row, viewGroup, false);
        return new ViewHolder(v, callbacks);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FxProduct p = list.get(position);
        holder.brickId.setText(p.getBrickId());
        holder.length.setText(p.getLength());
        holder.weight.setText(p.getWeight());
        holder.validLength.setText(p.getValidLength());
        holder.bbc.setText(p.getBbc());
        holder.num.setText(p.getNum());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        @InjectView(R.id.content_view)
        public PercentRelativeLayout content;
        @InjectView(R.id.fx_brick_id)
        public TextView brickId;
        @InjectView(R.id.fx_length)
        public TextView length;
        @InjectView(R.id.fx_weight)
        public TextView weight;
        @InjectView(R.id.fx_valid_length)
        public TextView validLength;
        @InjectView(R.id.fx_bbc)
        public TextView bbc;
        @InjectView(R.id.fx_num)
        public EditText num;

        public ViewHolder(View v, final ButtonCallbacks callbacks) {
            super(v);
            ButterKnife.inject(this, v);

            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbacks.click(getAdapterPosition());
                }
            });

            num.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    callbacks.numChange(getAdapterPosition(), num.getText().toString());
                }
            });
        }
    }
}
