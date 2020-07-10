package com.rabbitt.momobill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.OrderNameActivity;
import com.rabbitt.momobill.model.OrderName;

import java.util.ArrayList;

public class OrderNameAdapter extends RecyclerView.Adapter<OrderNameAdapter.MyViewHolder> {

    Context context;
    private ArrayList<OrderName> dataSet;

    public OrderNameAdapter(ArrayList<OrderName> data) {
        this.dataSet = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.name_card, parent, false);

        view.setOnClickListener(OrderNameActivity.myOnClickListener);

        context = view.getContext();


        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TextView nameTxt = holder.nameTxt;
        nameTxt.setText(dataSet.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameTxt = itemView.findViewById(R.id.name_txt);
        }
    }
}
