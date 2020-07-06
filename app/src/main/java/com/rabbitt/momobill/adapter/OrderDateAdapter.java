package com.rabbitt.momobill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.CheckOrderActivity;
import com.rabbitt.momobill.model.OrderDate;

import java.util.ArrayList;

public class OrderDateAdapter extends RecyclerView.Adapter<OrderDateAdapter.MyViewHolder> {

    private ArrayList<OrderDate> dataSet;

    Context context;

    public OrderDateAdapter(ArrayList<OrderDate> data) {
        this.dataSet = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.date_card, parent, false);

        view.setOnClickListener(CheckOrderActivity.myOnClickListener);

        context = view.getContext();


        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView dateTxt;
        dateTxt = holder.dateTxt;
        dateTxt.setText(dataSet.get(position).getFdate());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dateTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.dateTxt = itemView.findViewById(R.id.date_txt);
        }
    }
}
