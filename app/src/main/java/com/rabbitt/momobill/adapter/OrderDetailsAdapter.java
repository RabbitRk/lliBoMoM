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

        TextView nameTxt,productTxt;
        nameTxt = holder.nameTxt;
        productTxt = holder.productTxt;
        nameTxt.setText(dataSet.get(position).getName());
        productTxt.setText(dataSet.get(position).getProduct());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt,productTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.client_name);
            productTxt = itemView.findViewById(R.id.product_name);
        }

    }
}
