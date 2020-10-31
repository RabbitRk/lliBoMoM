package com.rabbitt.momobill.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.CustomActivity;
import com.rabbitt.momobill.fragment.InvoiceFrag;
import com.rabbitt.momobill.model.Credit;
import com.rabbitt.momobill.model.Product;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.holder> {

    private static final String TAG = "maluClientAdapter";
    private List<Product> dataModelArrayList;
    private CustomActivity context;
    private OnRecyleItemListener mOnRecycleItemListener;

    public CustomAdapter(List<Product> ClientAdap, CustomActivity context, OnRecyleItemListener onRecyleItemListener) {
        this.dataModelArrayList = ClientAdap;
        this.context = context;
        this.mOnRecycleItemListener = onRecyleItemListener;
    }

    @NonNull
    @Override
    public CustomAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list, null);
        return new holder(view, mOnRecycleItemListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.holder holder, int position) {

        Product dataModel = dataModelArrayList.get(position);

        holder.amount.setText(dataModel.getSale_rate());
        holder.name.setText(dataModel.getProduct_name());

    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, amount;
        Button pop;
        OnRecyleItemListener onRecyleItemListener;

        public holder(@NonNull View itemView, OnRecyleItemListener onRecyleItemListener) {
            super(itemView);
            this.onRecyleItemListener = onRecyleItemListener;

            name = itemView.findViewById(R.id.txt_pro_name);
            amount = itemView.findViewById(R.id.txt_pro_rate);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecyleItemListener.OnSettle(getAdapterPosition());
        }
    }

    public interface OnRecyleItemListener {
        void OnSettle(int position);
    }
}