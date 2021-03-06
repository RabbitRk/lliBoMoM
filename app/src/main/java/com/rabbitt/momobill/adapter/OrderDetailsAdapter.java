package com.rabbitt.momobill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.model.OrderDetails;

import java.util.ArrayList;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.MyViewHolder> {

    private ArrayList<OrderDetails> dataSet;
    Context context;

    public OrderDetailsAdapter(ArrayList<OrderDetails> data) {
        this.dataSet = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.details_card, parent, false);

        context = view.getContext();

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TextView productTxt, unitTxt, amountTxt;

        productTxt = holder.productTxt;
        unitTxt = holder.unitTxt;
        amountTxt = holder.amountTxt;

        productTxt.setText(dataSet.get(position).getProduct());
        unitTxt.setText(dataSet.get(position).getUnit());
        amountTxt.setText(dataSet.get(position).getAmount());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView productTxt, unitTxt, amountTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productTxt = itemView.findViewById(R.id.product_name);
            unitTxt = itemView.findViewById(R.id.units_txt);
            amountTxt = itemView.findViewById(R.id.amount_txt);
        }

    }
}
